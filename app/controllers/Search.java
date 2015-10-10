package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Church;
import models.MediaContent;
import models.MediaContentType;
import models.internal.search.SearchManager;
import play.libs.Json;
import play.mvc.*;
import utils.serialize.Serializer;

import java.util.List;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 15.07.2015
 * Time: 1:36
 */
public class Search extends Controller
{

    public static Result churchesByNameAndAddress(String q)
    {
        beginTransaction();
        ObjectNode result = Json.newObject();
        result.put("q", q);
        List<Church> churches = SearchManager.searchChurches(q);
        commitTransaction();
        if (churches == null || churches.size() == 0) {
            return notFound(result);
        }
        else
        {
            Json.setObjectMapper(Serializer.entityMapper);
            result.put("results", Json.toJson(churches));
            result.put("success", "true");
            return ok(result);
        }
    }

    public static Result mediaContentByText(String q, String type)
    {
        beginTransaction();
        ObjectNode result = Json.newObject();
        result.put("q", q);
        MediaContentType ctype = MediaContentType.fromString(type);
        if (ctype == null)
            return notFound(type);
        List<MediaContent> content = SearchManager.searchMediaContent(q, ctype);
        commitTransaction();
        if (content == null || content.size() == 0) {
            return notFound(result);
        }
        else
        {
            Json.setObjectMapper(Serializer.entityMapper);
            result.put("results", Json.toJson(content));
//            result.put("excerpt", Json.toJson())
            result.put("success", "true");
            return ok(result);
        }
    }

}
