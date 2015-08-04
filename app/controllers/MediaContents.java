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
        Sequencer mcs = getFromCache(session().getOrDefault("mcs_starred_key", "none"));
        String seqKey;
        if (mcs == null) {
            seqKey = UUID.randomUUID().toString();

            mcs = new Sequencer(seqKey, type, SequencerStrategyMode.Starred);

        } else
            seqKey = mcs.getKey();
        List<MediaContent> contents = mcs.get(quantity);
        storeToCache(seqKey, mcs);
        ObjectNode result = Json.newObject();
        result.put("left", mcs.left());
        result.put("contents", Json.toJson(contents));
        return ok(result);
    }

    private static void storeToCache(String seqKey, Sequencer mcs)
    {
        Cache.set("mcs_starred_key", mcs);
    }

    private static Sequencer getFromCache(String seqKey)
    {
        return (Sequencer) Cache.get(seqKey);
    }
}
