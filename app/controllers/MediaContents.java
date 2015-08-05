package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.MediaContent;
import models.MediaContentType;
import models.internal.media.content.Sequencer;
import models.internal.media.content.strategy.SequencerStrategyMode;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.UUID;

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
        MediaContentType type = MediaContentType.fromString(ctype);
        if (type == null)
            return badRequest("Trying to get content with type : " + ctype);
        MediaContent content = (MediaContent) get(MediaContent.class, id);
        if (content != null) {
            if (!content.contentType.equals(type))
                return badRequest("Wrong content type: " + type);
            return ok("Got request " + request() + "!" + "Found: " + content);
        } else
            return notFound(String.format("MediaContent with id {%s}", id));
    }


    // sequencing on algorithms, depending on content type
    public static Result thumbs(String ctype, Integer quantity)
    {
        MediaContentType type = MediaContentType.fromString(ctype);
        if (type == null)
            return badRequest("Trying to get content with type : " + ctype);
        String seqKey = session().get("mcs_starred_key");
        System.out.println("seqKey = " + seqKey);
        Sequencer mcs = null;
        if (seqKey != null)
            mcs = (Sequencer) Cache.get(seqKey);
        if (mcs == null) {
            seqKey = UUID.randomUUID().toString();
            mcs = new Sequencer(seqKey, type, SequencerStrategyMode.Starred);
        } else
            System.out.println("Found sequencer! seq_key: " + seqKey);
        List<MediaContent> contents = mcs.get(quantity);
        Cache.set(seqKey, mcs);
        ObjectNode result = Json.newObject();
        result.put("left", mcs.left());
        result.put("contents", Json.toJson(contents));
        session("mcs_starred_key", seqKey);
        return ok(result);
    }

    public static Result byTypeAndIds(String ctype, String ids)
    {
        return play.mvc.Results.TODO;
    }
}
