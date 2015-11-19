package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.*;
import models.internal.*;
import models.internal.email.EmailSubstitution;
import models.internal.email.EmailUnsubscription;
import models.internal.email.EmailWrapper;
import models.user.User;
import models.user.UserRole;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.DataUtils;
import utils.ServerProperties;
import utils.serialize.Serializer;
import utils.service.ImageCreator;
import utils.service.auth.ASDAuthUser;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static models.internal.UserManager.getLocalUser;
import static models.internal.UserManager.getUnsubscribeLink;
import static models.internal.email.EmailWrapper.sendEmail;
import static utils.DataUtils.safeInt;
import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class Churches extends Controller
{

    public static Result requestsById(String id)
    {
        beginTransaction();
        Church church = ContentManager.getChurch(id);
        if (church != null) {
            Set<ChurchSuggestion> requests = church.getRequests();
            commitTransaction();
            if (requests != null)
                requests = requests.stream().filter(r -> !r.isFixed() && !r.isIgnored()).collect(Collectors.toSet());
            Json.setObjectMapper(Serializer.emptyMapper);
            return ok(Json.toJson(requests));
        } else {
            commitTransaction();
            return notFound(String.format("Church with id {%s}", id));
        }
    }

    public static Result byId(String id)
    {
        beginTransaction();
        Church church = ContentManager.getChurch(id);
        commitTransaction();
        if (church != null) {
            Json.setObjectMapper(Serializer.emptyMapper);
            return ok(Json.toJson(church));
        } else
            return notFound(String.format("Church with id {%s}", id));
    }

    public static Result images(String id)
    {
        beginTransaction();
        Church church = ContentManager.getChurch(id);
        commitTransaction();
        if (church != null && church.getImages() != null) {
            List<Image> images = church.getImages();
            return ok(Json.toJson(images.stream().filter(i -> i.getApprovedTS() != null).collect(Collectors.toList())));
        } else
            return notFound(String.format("Church with id {%s}", id));
    }

    public static Result suggest()
    {
        return processSuggestion(ChurchSuggestionType.NEW_CHURCH, null);
    }

    public static Result updateField(String field)
    {
        return processSuggestion(ChurchSuggestionType.FIELD, field);
    }

    public static Result ignoreSuggestion(int id)
    {
        beginTransaction();
        try {
            ChurchSuggestion cs = (ChurchSuggestion) getSession().get(ChurchSuggestion.class, id);
            if (cs == null) {
                commitTransaction();
                return badRequest(Json.newObject().put("success", false).put("id", id));
            } else {
                cs.setIgnored(true);
//                Church relatedChurch = cs.getRelatedChurch();
//                relatedChurch.getRequests().remove(cs);
//                update(relatedChurch);
                update(cs);
            }
        } catch (Exception e) {
            Logger.error("while ignoring suggestion", e);
            return badRequest(Json.newObject().put("success", false).put("id", id));
        }
        commitTransaction();
        return ok(Json.newObject().put("success", true).put("id", id));
    }

    public static Result fixSuggestion(int id)
    {
        beginTransaction();
        try {
            ChurchSuggestion cs = (ChurchSuggestion) getSession().get(ChurchSuggestion.class, id);
            if (cs == null) {
                commitTransaction();
                return badRequest(Json.newObject().put("success", false).put("id", id));
            } else {
                cs.setFixed(true);
                User suggestedBy = cs.getSuggestedBy();
                EmailUnsubscription eu = UserManager.findUnsubscription(suggestedBy);
                String hash = (eu == null) ? "" : eu.getHash();
                Church relatedChurch = cs.getRelatedChurch();
//                relatedChurch.getRequests().remove(cs);
//                update(relatedChurch);
                update(cs);
                if (suggestedBy != null)
                sendEmail(EmailWrapper.EmailNames.PassportFixed,
                        null,
                        suggestedBy,
                        Pair.of(EmailSubstitution.Username.name(), suggestedBy.getName()),
                        Pair.of(EmailSubstitution.UnsubscribeLink.name(), getUnsubscribeLink(hash)),
                        Pair.of(EmailSubstitution.ChurchName.name(), relatedChurch.getName()),
                        Pair.of(EmailSubstitution.ChurchLink.name(),
                                getChurchLink(relatedChurch)),
                        Pair.of(EmailSubstitution.ChurchPassportLink.name(),
                                getChurchLink(relatedChurch, "#passport")),
                        Pair.of(EmailSubstitution.ChurchPassportAdd.name(),
                                getChurchLink(relatedChurch, "#passportadd"))
                );
            }

        } catch (Exception e) {
            Logger.error("while fixing suggestion", e);
            return badRequest(Json.newObject().put("success", false).put("id", id));
        }
        commitTransaction();
        return ok(Json.newObject().put("success", true).put("id", id));
    }

    public static String getChurchLink(Church relatedChurch)
    {
        return getChurchLink(relatedChurch, null);
    }

    public static String getChurchLink(Church relatedChurch, String add)
    {
        return ServerProperties.getValue("asd.absolute.url") + "church/" + relatedChurch.getExtID() +
                ((add == null) ? "" : add);
    }

    private static Result processSuggestion(ChurchSuggestionType type, String field)
    {
        Form<ChurchSuggestion> suggestionForm = Form.form(ChurchSuggestion.class);
        if (!suggestionForm.hasErrors()) {
            beginTransaction();
            User user = getLocalUser(session());
            ChurchSuggestion cs = suggestionForm.bindFromRequest().get();
            cs.setType(type);
            cs.setField(field);
            cs.setSuggestedBy(user);
            cs.setFixed(false);
            if (cs.getExtID() != null)
                cs.setRelatedChurch(ContentManager.getChurch(cs.getExtID()));
            cs.setSuggestedOn(new Date());
            saveOrUpdate(cs);
            commitTransaction();
            return ok(Json.newObject().put("success", true));
        } else
            return badRequest(suggestionForm.errorsAsJson());

    }

    private static Result processStory()
    {
        Form<MediaContent> mc = Form.form(MediaContent.class);
        if (mc.hasErrors()) {
            return internalServerError(mc.errorsAsJson());
        } else {
            beginTransaction();
            MediaContent mediaContent = mc.bindFromRequest(request()).get();
            Serializable id = save(mediaContent);
            commitTransaction();
            return ok("{success:'true',id:'" + id + "'}");
        }
    }

    //    @Security.Authenticated(Secured.class)
    public static Result addStory()
    {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        ObjectNode result = Json.newObject();
        Map<String, String[]> map = body.asFormUrlEncoded();
        System.out.println("map = " + map);
        beginTransaction();
        User user = getLocalUser(session());
        if (user == null)
            user = loginEmail();
        if (user == null) {
            commitTransaction();
            return forbidden("alas");
        }

        String jtext = (map.get("text") != null) ? map.get("text")[0] : null,
                jcover = (map.get("cover") != null) ? map.get("cover")[0] : null,
                jtitle = (map.get("title") != null) ? map.get("title")[0] : null,
                jchurch = (map.get("church") != null) ? map.get("church")[0] : null,
                jyear = (map.get("year") != null) ? map.get("year")[0] : null;
        String text = null, title = null, coverPath = null;
        String year = null;
        Church church = null;
        if (jtext != null)
            text = jtext;
        else
            return badRequest("text is null");
        if (jtitle != null)
            title = jtitle;
        if (jcover != null) {
            coverPath = "/assets/images/passport/church_story_" + jcover + "_thumb_is.png";
        } else return badRequest("cover is null");
        if (jyear != null)
            year = jyear;
        String coverThumbPath = coverPath;
        if (jchurch != null)
            church = ContentManager.getChurch(jchurch);
        else
            return badRequest("church is absent");

        MediaContent c = new MediaContent(MediaContentType.Story, text, title, year, null, coverThumbPath, user, church);
        c.setId((Long) save(c));

        try {
            Exporter.exportOne(c);
        } catch (Exception e) {
            Logger.error("cannot save story to archive: ", e);
        }

        Set<MediaContent> media = church.getMedia();
        if (media == null)
            media = new LinkedHashSet<>();
        media.add(c);

        church.setMedia(media);

        int successFiles = processUploadedImages(body, map, user, church);

        update(church);

        try {
            String username = user.getName();
            EmailUnsubscription eu = UserManager.findUnsubscription(user);
            String hash = (eu == null) ? "" : eu.getHash();

            sendEmail(
                    EmailWrapper.EmailNames.AddStory,
                    null,
                    user,
                    Pair.of(EmailSubstitution.Username.name(), username),
                    Pair.of(EmailSubstitution.UnsubscribeLink.name(), getUnsubscribeLink(hash))
            );
        } catch (Exception e) {
            Logger.error("error while sending email", e);
        }

        commitTransaction();


        result.put("success", true);
        result.put("id", c.getId());
        result.put("files", successFiles);
        return ok(result);
    }

    public static Result addImages()
    {
        ObjectNode result = Json.newObject();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Map<String, String[]> map = body.asFormUrlEncoded();

        String jchurch = (map.get("church") != null) ? map.get("church")[0] : null;
        Church church;

        beginTransaction();
        if (jchurch != null)
            church = ContentManager.getChurch(jchurch);
        else
            return badRequest("church is absent");
        User user = getLocalUser(session());
        if (user == null)
            user = loginEmail();
        if (user == null)
            return forbidden("sorry");
        int successFiles = processUploadedImages(body, map, user, church);
        update(church);
        commitTransaction();
        result.put("success", true);
        result.put("files", successFiles);
        return ok(result);
    }


    public static int processUploadedImages(Http.MultipartFormData body, Map<String, String[]> map, User user, Church church)
    {
        int success = 0;

        List<Http.MultipartFormData.FilePart> files = body.getFiles();

        for (Http.MultipartFormData.FilePart filePart : files) {
            try {

                File file = filePart.getFile();
                String description = "";
                String fileId = filePart.getKey();
                System.out.println("fileId = " + fileId);

                if (fileId != null) {
                    fileId = fileId.substring(fileId.indexOf('_') + 1);
                    description = map.get("description_" + fileId) != null ? map.get("description_" + fileId)[0] : "";
                }
                String filename = filePart.getFilename();
                Image image = ImageCreator.createImageFromUpload(user, file, filename, description);
                if (image != null) {
                    image.setId((Long) save(image));

                    List<Image> churchImages = church.getImages();
                    if (churchImages == null)
                        churchImages = new ArrayList<>();
                    if (churchImages.removeIf(i -> image.getPath().equalsIgnoreCase(i.getPath())))
                        Logger.info("updating image " + image);
                    churchImages.add(image);
                    church.setImages(churchImages);
                    success++;
                }
            } catch (Exception e) {
                Logger.error("pic upload error", e);
            }

        }
        return success;
    }

    private static User loginEmail()
    {
        AuthUser authUserFromRequest = getIdentityFromRequest();
        User user = null;
        if (authUserFromRequest != null) {
            user = UserManager.findByAuthUserIdentity(authUserFromRequest);
            if (user == null) // new user
            {
                user = UserManager.createUser(authUserFromRequest, UserRole.User);
                System.out.println("user = " + user);
                PlayAuthenticate.storeUser(session(), authUserFromRequest);
                user = getLocalUser(session());
                System.out.println("user = " + user);
            }
        }
        return user;
    }

    private static AuthUser getIdentityFromRequest()
    {
        System.out.println("request() = " + request());
        DynamicForm df = Form.form().bindFromRequest();
        String username = df.get("username");
        String useremail = df.get("useremail");
        AuthUser authUser = null;
        if (username != null && !"".equals(username) && useremail != null && !"".equals(useremail))
            authUser = new ASDAuthUser(username, "", useremail);
        return authUser;
    }

    public static Result updateChurch(String extID)
    {
        ObjectNode result = Json.newObject();
        Http.RequestBody body = request().body();
        Map<String, String[]> map = body.asFormUrlEncoded();

        beginTransaction();
        User user = getLocalUser(session());
        boolean isNew;
        Church c = ContentManager.getChurch(extID);
        isNew = c == null;
        if (isNew) {
            c = new Church(extID, user);
        }
        try {
            result.put("entity", "church");
            result.put("id", c.getExtID());

            // possible changeable fields
            String jname = (map.get("name") != null) ? map.get("name")[0] : null,
                    jconstructionStart = (map.get("constructionStart") != null) ? map.get("constructionStart")[0] : null,
                    jconstructionEnd = (map.get("constructionEnd") != null) ? map.get("constructionEnd")[0] : null,
                    jwebsite = (map.get("website") != null) ? map.get("website")[0] : null,
                    jsynonyms = (map.get("synonyms") != null) ? map.get("synonyms")[0] : null;

            Date jpublishDate = (map.get("approvedDT") != null) ? DataUtils.dateFromReqString(map.get("approvedDT")) : null;

            Set<Architect> jarchitects = (map.get("architects") != null) ? ContentManager.parseArchitectsList(map.get("architects")) : null;
            if (jarchitects == null)
                jarchitects = (map.get("authors[]") != null) ? ContentManager.parseArchitectsList(map.get("architects[]")) : Collections.<Architect>emptySet();

            c.setArchitects(jarchitects);
            for (Architect architect : jarchitects) {
                Set<Church> churches = architect.getChurches();
                if (churches == null)
                    churches = new HashSet<>();
                churches.add(c);
                architect.setChurches(churches);
            }


            c.setConstructionStart(safeInt(jconstructionStart, 0));
            c.setConstructionEnd(safeInt(jconstructionEnd, 0));
            if (jname != null && !"".equals(jname))
                c.setName(jname);
            if (jwebsite != null && !"".equals(jwebsite))
                c.setWebsite(jwebsite);

            Set<String> synonymSet = createSynonymSet(jsynonyms);
            if (synonymSet != null)
                c.getSynset().addAll(synonymSet);

            if (jpublishDate != null) {
                c.setApprovedDT(jpublishDate);
                c.setApprovedBy(user);
            }
        } catch (Exception e)

        {
            Logger.error("while updating church", e);
            rollbackTransaction();
            return badRequest(result);
        }

        saveOrUpdate(c);
        Logger.info("c = " + c);
        commitTransaction();

        result.put("success", true);
        return ok(result);
    }

    private static Set<String> createSynonymSet(String synonyms)
    {
        Set<String> res = null;
        if (synonyms == null || "".equals(synonyms.trim()))
            return res;

        String[] split = synonyms.split(",;");
        for (String s : split) {
            res = new LinkedHashSet<>();
            res.add(s);
        }
        return res;
    }

}
