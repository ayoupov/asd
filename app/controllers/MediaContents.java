package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Church;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.RequestException;
import models.internal.UserManager;
import models.user.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import utils.DataUtils;
import utils.HibernateUtils;
import utils.ServerProperties;
import views.html.mediacontent;

import java.io.*;
import java.util.*;

import static utils.DataUtils.*;
import static utils.HibernateUtils.*;
import static utils.ServerProperties.isInProduction;
import static utils.media.images.Thumber.thumbName;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class MediaContents extends Controller
{

    private static String absoluteUploadPath = ServerProperties.getValue("asd.upload.path");
    private static String relativeUploadPath = ServerProperties.getValue("asd.upload.relative.path");

    static String publicPath = ServerProperties.getValue("asd.public.path");

    public static Result byTypeAndId(String ctype, Long id) throws IOException
    {
        return byTypeAndId(ctype, id, "html");
    }

    public static Result byTypeAndId(String ctype, Long id, String ext) throws IOException
    {
        Long givenId = id;
        beginTransaction();
        MediaContentType type = MediaContentType.fromString(ctype);
        if (type == null)
            return badRequest("Trying to get content with type : " + ctype);
        MediaContent content = (MediaContent) get(MediaContent.class, givenId);
        commitTransaction();
        if (content != null) {
            if (!content.contentType.equals(type))
                return badRequest("Wrong content type: " + type);
            if ("json".equals(ext))
                return ok(Json.toJson(content));
            else {
                // check if static version exists
                File dir = new File(publicPath + ctype);
                File mediaFile = new File(dir, givenId + ".html");
                if (mediaFile.exists())
                    return ok(mediaFile, true);
                Html rendered = mediacontent.render(content);
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

    public static Result byTypeAndIds(String ctype, String ids) throws RequestException
    {
        ObjectNode result = Json.newObject();
        MediaContentType mct = MediaContentType.fromString(ctype);
        beginTransaction();
        List<MediaContent> contents = ContentManager.getByIds(ids);
        result.put("data", Json.toJson(contents));
        result.put("success", true);
        commitTransaction();
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public static Result newStory()
    {
        ObjectNode result = Json.newObject();
        Http.RequestBody body = request().body();
        Map<String, String[]> map = body.asFormUrlEncoded();
        beginTransaction();
        User user = UserManager.getLocalUser(session());
        String jtext = (map.get("text") != null) ? map.get("text")[0] : null,
                jcover = (map.get("cover") != null) ? map.get("cover")[0] : null,
                jtitle = (map.get("title") != null) ? map.get("title")[0] : null,
                jchurch = (map.get("church") != null) ? map.get("church")[0] : null,
                jyear = (map.get("year") != null) ? map.get("year")[0] : null;
        String text = null, title = null, coverPath = null;
        Integer year = null;
        Church church = null;
        if (jtext != null)
            text = jtext;
        else
            return badRequest("text is null");
        if (jtitle != null)
            title = jtitle;
        if (jcover != null) {
//            coverPath = ServerProperties.getValue("asd.upload.relative.path") + User.anonymousHash() + ("/church_story_" + jcover + ".png");
            coverPath = User.anonymousHash() + ("/church_story_" + jcover + "_thumb.png");
//            System.out.println("coverPath = " + coverPath);
        } else return badRequest("cover is null");
        if (jyear != null)
            year = safeInt(jyear, 2015);
//        Image cover = findImage(null, coverPath);
//        String coverThumbPath = thumbName(new File(coverPath));
        Image coverThumb = findImage(null, coverPath);
        if (jchurch != null)
            church = ContentManager.getChurch(jchurch);
        else
            return badRequest("church is absent");
        MediaContent c = new MediaContent(MediaContentType.Story, text, title, year, null, coverThumb, user, church);
        c.setId((Long) save(c));
        Set<MediaContent> media = church.getMedia();
        if (media == null)
            media = new LinkedHashSet<>();
        media.add(c);
        church.setMedia(media);
        HibernateUtils.update(church);
        commitTransaction();
        result.put("success", true);
        result.put("id", c.getId());
        return ok(result);
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

        if (mct != c.contentType) {
            commitTransaction();
            result.put("error", "wrong entity type");
            return badRequest(result);
        }

        // possible changeable fields
        String jtext = (map.get("text") != null) ? map.get("text")[0] : null,
                jlead = (map.get("lead") != null) ? map.get("lead")[0] : null,
                jcover = (map.get("cover") != null) ? map.get("cover")[0] : null,
                jdesc = (map.get("desc") != null) ? map.get("desc")[0] : null,
                jtitle = (map.get("title") != null) ? map.get("title")[0] : null;
        Boolean jstarred = (map.get("starred") != null) ? safeBool(map.get("starred")) : false;
        Date jpublishDate = (map.get("approvedDT") != null) ? DataUtils.dateFromReqString(map.get("approvedDT")) : null;
        Set<User> jauthors = (map.get("authors") != null) ? ContentManager.parseUserList(map.get("authors")) : null;
        if (jauthors == null)
            jauthors = (map.get("authors[]") != null) ? ContentManager.parseUserList(map.get("authors[]")) : null;
//        System.out.println("jtitle = " + jtitle);
//        System.out.println("jlead = " + jlead);
//        System.out.println("jdesc = " + jdesc);
//        System.out.println("jcover = " + jcover);
//        System.out.println("jtext = " + jtext);
//        System.out.println("jstarred = " + jstarred);
//        System.out.println("jpublishDate = " + jpublishDate);
//        System.out.println("jauthors = " + jauthors);
        if (jtext != null)
            c.setText(jtext);
        if (jlead != null)
            c.setLead(jlead);
        if (jdesc != null)
            c.setCoverDescription(jdesc);
        if (jcover != null) {
            c.setCover(findImage(null, jcover));
            String coverThumbPath = thumbName(new File(jcover));
            Image coverThumb = findImage(null, coverThumbPath);
            c.setCoverThumb(coverThumb);
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
        result.put("entity", ctype);
        result.put("success", true);
        result.put("id", c.getId());
        return ok(result);
    }

    public static Result remove(String ctype, long mcid)
    {
        ObjectNode result = Json.newObject();
        beginTransaction();
        result.put("entity", ctype);
        result.put("id", mcid);
        boolean deleted = delete(MediaContent.class, mcid);
        result.put("success", deleted);
        commitTransaction();
        if (deleted)
            return ok(result);
        else
            return internalServerError(result);
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
        if (i != null)
            return i;
        // fallback option 1

        i = ContentManager.findImageByPath(relativeUploadPath + path);
        if (i != null)
            return i;

        // fs fallback option 2
        Logger.info("searching for an image with path: " + (absoluteUploadPath + path));
        if (new File(absoluteUploadPath + path).exists())
            return new Image("fs fallback option", relativeUploadPath + path);
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
