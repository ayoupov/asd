package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.Church;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.internal.*;
import models.internal.email.EmailSubstitution;
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
import utils.serialize.Serializer;
import utils.service.ImageCreator;
import utils.service.auth.ASDAuthUser;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static models.internal.email.EmailWrapper.sendEmail;
import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class Churches extends Controller
{

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
        if (church != null)
            return ok(Json.toJson(church.getImages()));
        else
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

    private static Result processSuggestion(ChurchSuggestionType type, String field)
    {
        Form<ChurchSuggestion> suggestionForm = Form.form(ChurchSuggestion.class);
        if (!suggestionForm.hasErrors()) {
            beginTransaction();
            User user = UserManager.getLocalUser(session());
            ChurchSuggestion cs = suggestionForm.bindFromRequest().get();
            cs.setType(type);
            cs.setField(field);
            cs.setSuggestedBy(user);
            cs.setFixed(false);
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
        User user = UserManager.getLocalUser(session());
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
            sendEmail(
                    EmailWrapper.EmailNames.AddStory,
                    null,
                    user,
                    Pair.of(EmailSubstitution.Username.name(), username));

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
        User user = UserManager.getLocalUser(session());
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
                user = UserManager.getLocalUser(session());
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
}
