package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.RequestException;
import models.internal.UserManager;
import models.user.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.DataUtils;
import utils.ServerProperties;
import views.html.mediacontent;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static utils.DataUtils.safeBool;
import static utils.DataUtils.safeLong;
import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class MediaContents extends Controller
{

    public static Result byTypeAndId(String ctype, Long id)
    {
        return byTypeAndId(ctype, id, "html");
    }

    public static Result byTypeAndId(String ctype, Long id, String ext)
    {
        beginTransaction();
        MediaContentType type = MediaContentType.fromString(ctype);
        if (type == null)
            return badRequest("Trying to get content with type : " + ctype);
        MediaContent content = (MediaContent) get(MediaContent.class, id);
        commitTransaction();
        if (content != null) {
            if (!content.contentType.equals(type))
                return badRequest("Wrong content type: " + type);
            if ("json".equals(ext))
                return ok(Json.toJson(content));
            else
                return ok(mediacontent.render(content));
        } else
            return notFound(String.format("MediaContent with id {%s}", id));
    }


    // sequencing on algorithms, depending on content type
//    public static Result thumbs(String ctype, Integer quantity)
//    {
//        MediaContentType type = MediaContentType.fromString(ctype);
//        if (type == null)
//            return badRequest("Trying to get content with type : " + ctype);
//        String seqKey = session().get("mcs_starred_key");
//        System.out.println("seqKey = " + seqKey);
//        Sequencer mcs = null;
//        if (seqKey != null)
//            mcs = (Sequencer) Cache.get(seqKey);
//        if (mcs == null) {
//            seqKey = UUID.randomUUID().toString();
//            mcs = new Sequencer(seqKey, type, SequencerStrategyMode.Starred);
//        } else
//            System.out.println("Found sequencer! seq_key: " + seqKey);
//        List<MediaContent> contents = mcs.get(quantity);
//        Cache.set(seqKey, mcs);
//        ObjectNode result = Json.newObject();
//        result.put("left", mcs.left());
//        result.put("contents", Json.toJson(contents));
//        session("mcs_starred_key", seqKey);
//        return ok(result);
//    }

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
        List<User> jauthors = (map.get("authors") != null) ? ContentManager.parseUserList(map.get("authors")) : null;
        if (jauthors == null)
            jauthors = (map.get("authors[]") != null) ? ContentManager.parseUserList(map.get("authors[]")) : null;
        System.out.println("jtitle = " + jtitle);
        System.out.println("jlead = " + jlead);
        System.out.println("jdesc = " + jdesc);
        System.out.println("jcover = " + jcover);
        System.out.println("jtext = " + jtext);
        System.out.println("jstarred = " + jstarred);
        System.out.println("jpublishDate = " + jpublishDate);
        System.out.println("jauthors = " + jauthors);
        if (jtext != null)
            c.setText(jtext);
        if (jlead != null)
            c.setLead(jlead);
        if (jdesc != null)
            c.setCoverDescription(jdesc);
        if (jcover != null)
            c.setCoverImage(findImage(null, jcover));
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
        System.out.println("c = " + c);
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
        // fs fallback option
        if (new File(ServerProperties.getValue("asd.upload.path") + path).exists())
            return new Image("fs fallback option", path);
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
