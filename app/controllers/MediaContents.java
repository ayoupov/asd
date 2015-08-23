package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.RequestException;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.mediacontent;

import java.util.List;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
import static utils.HibernateUtils.get;

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
}
