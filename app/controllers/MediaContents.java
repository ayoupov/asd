package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.RequestException;
import models.user.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.DataUtils;
import views.html.mediacontent;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public static Result update(String ctype)
    {
        ObjectNode result = Json.newObject();
        Http.RequestBody body = request().body();
        Map<String, String[]> map = body.asFormUrlEncoded();
        JsonNode node = Json.parse(map.get(ctype)[0]); // ugly as f*ck, thanks, raptor
        node = node.get(ctype);
        System.out.println("node = " + node);
        MediaContentType mct = MediaContentType.fromString(ctype);

        beginTransaction();
        long id = node.get("id").asLong();
        System.out.println("id = " + id);
        boolean isNew = id == 0;
        MediaContent c;
        if (!isNew)
            c = (MediaContent) get(MediaContent.class, id);
        else {
            c = new MediaContent(mct);
        }

        if (mct != c.contentType) {
            commitTransaction();
            return badRequest();
        }

        // possible changeable fields
        String jtext = (node.get("text") != null) ? node.get("text").asText() : null, jlead = (node.get("lead") != null) ? node.get("lead").asText() : null, jtitle = (node.get("title") != null) ? node.get("title").asText() : null;
        Boolean jstarred = (node.get("starred") != null) ? node.get("starred").asBoolean() : null;
        Date jpublishDate = (node.get("approvedDT") != null) ? DataUtils.dateFromReqString(node.get("approvedDT").asText()) : null;
        List<User> jauthors = (node.get("authors") != null) ? ContentManager.parseUserList(node.get("authors").asText()) : null;
        if (jtext != null)
            c.setText(jtext);
        if (jlead != null)
            c.setLead(jlead);
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
        result.put("success", ctype);
        result.put("id", c.getId());
        return ok(result);


    }
}
