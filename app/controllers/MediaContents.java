package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.RequestException;
import models.internal.UserManager;
import models.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import utils.DataUtils;
import utils.ServerProperties;
import utils.media.images.Thumber;
import views.html.mediacontent;

import java.io.*;
import java.util.*;

import static utils.DataUtils.safeBool;
import static utils.DataUtils.safeLong;
import static utils.HibernateUtils.*;
import static utils.ServerProperties.isInProduction;
import static utils.media.images.Thumber.thumbNameWeb;
import static utils.serialize.Serializer.emptyMapper;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class MediaContents extends Controller
{

    public static String absoluteUploadPath = ServerProperties.getValue("asd.upload.path");
    public static String relativeUploadPath = ServerProperties.getValue("asd.upload.relative.path");

    static String publicPath = ServerProperties.getValue("asd.public.path");

    public static Result byTypeAndId(String ctype, String id) throws IOException
    {
        return byTypeAndId(ctype, id, "html");
    }

    public static Result byTypeAndId(String ctype, String id, String ext) throws IOException
    {
        beginTransaction();
        MediaContentType type = MediaContentType.fromString(ctype);
        if (type == null)
            return badRequest("Trying to get content with type : " + ctype);
        MediaContent content = ContentManager.getMediaByIdAndAlternative(id);
        commitTransaction();
        if (content != null) {
            if (!content.getContentType().equals(type))
                return badRequest("Wrong content type: " + type);
            if ("json".equals(ext)) {
                Json.setObjectMapper(emptyMapper);
                return ok(Json.toJson(content));
            } else {
                // check if static version exists
                File dir = new File(publicPath + ctype);
                File mediaFile = new File(dir, content.getId() + ".html");
                if (mediaFile.exists())
                    return ok(mediaFile, true);
                Html rendered = mediacontent.render(content, false);
                saveToFS(dir, mediaFile, rendered);
                return ok(rendered);
            }
        } else
            return notFound(String.format("MediaContent with id {%s}", id));
    }

    private static void saveToFS(File dir, File mediaFile, Html rendered) throws IOException
    {
        if (!isInProduction()) {
            Logger.info("Saving to: ");
            Logger.info("dir = " + dir);
            Logger.info("mediaFile = " + mediaFile);
        }
        dir.mkdirs();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(mediaFile), "UTF-8"));
        out.write(rendered.toString());
        out.close();
    }

    public static Result byTypeAndIds(String ctype, String ids) throws RequestException, JsonProcessingException
    {
        ObjectNode result = Json.newObject();
        MediaContentType mct = MediaContentType.fromString(ctype);
        beginTransaction();
        List<MediaContent> contents = ContentManager.getByIds(ids);
        result.put("data", Json.parse(emptyMapper.writeValueAsString(contents)));
        result.put("success", true);
        commitTransaction();
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result previewGet(String ctype, String id)
    {
        File dir = new File(publicPath + "preview/" + ctype);
        File mediaFile = new File(dir, id + ".html");
        if (mediaFile.exists())
            return ok(mediaFile, true);
        else
            return badRequest("preview file not found!");
    }


    static String[] MC_WHITELIST = new String[]{"text", "title", "year", "lead", "starred",
            "coverDescription", "approvedDT", "alternativeId"};
    static String[] MC_GREYLIST = new String[]{"authors", "authors[]", "cover", "church", "ctype"};

    @Security.Authenticated(Secured.class)
    public static Result previewPost(String ctype) throws IOException
    {
        Form<MediaContent> mcf = Form.form(MediaContent.class).bindFromRequest(MC_WHITELIST);
        if (mcf.hasErrors())
            return badRequest(mcf.errorsAsJson());
        MediaContent content = mcf.get();
        beginTransaction();
        DynamicForm dynamicForm = Form.form().bindFromRequest(request(), MC_GREYLIST);
        Map<String, String> data = dynamicForm.data();

        System.out.println("preview data = " + data);

        // fill in other fields
        // authors
        String userList = data.get("authors");
        if (userList == null)
            userList = data.get("authors[]");
        if (userList != null && !"".equals(userList))
            content.setAuthors(ContentManager.parseUserList(userList.split("\\,")));
        else
            content.setAuthors(new HashSet<>());
        // cover
        String cover = data.get("cover");
        if (cover != null) {
            content.setCover(findImage(null, cover));
            String coverThumbPath = thumbNameWeb(new File(cover), Thumber.ThumbType.ISOTOPE);
            content.setCoverThumbPath(coverThumbPath);
            String hoverThumbPath = thumbNameWeb(new File(cover), Thumber.ThumbType.HOVER);
            content.setHoverThumbPath(hoverThumbPath);
        }
        User user = UserManager.getLocalUser(session());
        String id = user.getHash() + "_" + (new Date().getTime() / 1000);
        content.setContentType(MediaContentType.fromString(ctype));
        commitTransaction();
        Html rendered = mediacontent.render(content, true);
        File dir = new File(publicPath + "preview/" + ctype);
        File mediaFile = new File(dir, id + ".html");
        saveToFS(dir, mediaFile, rendered);
        return ok(Json.newObject().put("success", true).put("previewId", id));
    }

    @Security.Authenticated(Secured.class)
    public static Result update(String ctype)
    {
        ObjectNode result = Json.newObject();
        Http.RequestBody body = request().body();
        Map<String, String[]> map = body.asFormUrlEncoded();
        MediaContentType mct = MediaContentType.fromString(ctype);

        beginTransaction();
        long id = safeLong(map.get("id"), 0);
        boolean isNew = id == 0;
        MediaContent c;
        if (!isNew)
            c = (MediaContent) get(MediaContent.class, id);
        else {
            c = new MediaContent(mct);
        }
        result.put("entity", ctype);

        if (mct != c.getContentType()) {
            commitTransaction();
            result.put("error", "wrong entity type");
            return badRequest(result);
        }

        // possible changeable fields
        String jtext = (map.get("text") != null) ? map.get("text")[0] : null,
                jlead = (map.get("lead") != null) ? map.get("lead")[0] : null,
                jcover = (map.get("cover") != null) ? map.get("cover")[0] : null,
                jalt = (map.get("alt") != null) ? map.get("alt")[0] : null,
                jdesc = (map.get("coverDescription") != null) ? map.get("coverDescription")[0] : null,
                jtitle = (map.get("title") != null) ? map.get("title")[0] : null;
        Boolean jstarred = (map.get("starred") != null) ? safeBool(map.get("starred")) : false;
        Date jpublishDate = (map.get("approvedDT") != null) ? DataUtils.dateFromReqString(map.get("approvedDT")) : null;
        Set<User> jauthors = (map.get("authors") != null) ? ContentManager.parseUserList(map.get("authors")) : null;
        if (jauthors == null)
            jauthors = (map.get("authors[]") != null) ? ContentManager.parseUserList(map.get("authors[]")) : null;
        if (jtext != null)
            c.setText(jtext);
        if (jlead != null)
            c.setLead(jlead);
        if (jalt != null)
            c.setAlt(jalt);
        if (jdesc != null)
            c.setCoverDescription(jdesc);
        if (jcover != null && !"".equals(jcover)) {
            Logger.info("updating cover with " + jcover);
            Image coverImage = findImage(null, jcover);
            Logger.info("found: " + coverImage);
            c.setCover(coverImage);
            if (coverImage == null)
                c.setCoverThumbPath(null);
            else {
                String coverThumbPath = thumbNameWeb(new File(jcover), Thumber.ThumbType.ISOTOPE);
                c.setCoverThumbPath(coverThumbPath);
                System.out.println("coverThumbPath = " + coverThumbPath);
                String hoverThumbPath = thumbNameWeb(new File(jcover), Thumber.ThumbType.HOVER);
                System.out.println("hoverThumbPath = " + hoverThumbPath);
                c.setHoverThumbPath(hoverThumbPath);
            }
        }
        if (jtitle != null)
            c.setTitle(jtitle);
        if (jstarred != null)
            c.setStarred(jstarred);
        if (jauthors != null)
            c.setAuthors(jauthors);
        if (jpublishDate != null)
            c.setApprovedDT(jpublishDate);
        if (isNew)
            c.setId((Long) save(c));
        else
            saveOrUpdate(c);
        Logger.info("c = " + c);
        commitTransaction();

        File dir = new File(publicPath + ctype);
        File mediaFile = new File(dir, c.getId() + ".html");
        if (mediaFile.exists())
            mediaFile.delete();

        result.put("entity", ctype);
        result.put("success", true);
        result.put("id", c.getId());
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result remove(String ctype, long mcid)
    {
        ObjectNode result = Json.newObject();
        beginTransaction();
        result.put("entity", ctype);
        result.put("id", mcid);
        MediaContent mc = (MediaContent) get(MediaContent.class, mcid);
        commitTransaction();
        if (mc != null) {
            mc.getAuthors().iterator().forEachRemaining(user -> {
                user.getAuthorOf().remove(mc);
            });
            beginTransaction();
            boolean deleted = delete(mc);
            commitTransaction();
            result.put("success", deleted);
            if (deleted) {
                File dir = new File(publicPath + ctype);
                File mediaFile = new File(dir, mcid + ".html");
                if (mediaFile.exists())
                    mediaFile.delete();
                return ok(result);
            } else
                return internalServerError(result);
        }
        else
            return notFound(result);
    }

    private static Image findImage(Serializable id, String path)
    {
        Image i;
        if (id != null) {
            i = (Image) get(Image.class, id);
            if (i != null)
                return i;
        }

        i = ContentManager.findImageByPath(path);
        if (i != null) {
            Logger.info("found");
            return i;
        }
        // fallback option 1

        i = ContentManager.findImageByPath(relativeUploadPath + path);
        if (i != null) {
            Logger.info("found fo 1");
            return i;
        }

        // fs fallback option 2
        Logger.info("searching for an image with path: " + (absoluteUploadPath + path));
        File lastHope = new File(absoluteUploadPath + path);
        if (lastHope.exists() && !lastHope.isDirectory()) {
            Logger.info("found fo 2");
            return new Image("fs fallback option", relativeUploadPath + path);
        }
        return null;
    }

    public static Result listAuthors()
    {
        beginTransaction();
        String q = request().getQueryString("q");
        List<Object[]> users = UserManager.getUserNames(q);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] arr : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("value", arr[0]);
            map.put("text", arr[1]);
            out.add(map);
        }
        commitTransaction();
        return ok(Json.toJson(out));
    }
}
