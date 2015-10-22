package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Church;
import models.MediaContent;
import models.MediaContentType;
import models.internal.search.SearchManager;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.serialize.Serializer;

import java.util.ArrayList;
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
        List<Church> churches;
        try {
            churches =
                    SearchManager.searchChurches(q);
        } catch (Exception e) {
            churches = new ArrayList<>();
            Logger.error("search: " + e.getMessage());
        }
        commitTransaction();
//        if (churches == null || churches.size() == 0) {
//            return notFound(result);
//        }
//        else
//        {
        Json.setObjectMapper(Serializer.searchMapper);
        result.put("results", Json.toJson(churches));
        result.put("success", "true");
        return ok(result);
//        }
    }

    public static Result mediaContentByText(String q, String type)
    {
        beginTransaction();
        ObjectNode result = Json.newObject();
        result.put("q", q);
        MediaContentType ctype = MediaContentType.fromString(type);
        if (ctype == null)
            return notFound(type);
        List<MediaContent> content;
        try {
            content = SearchManager.searchMediaContent(q, ctype);
        } catch (Exception e) {
            content = new ArrayList<>();
            Logger.error("search: " + e.getMessage());
        }
        commitTransaction();
//        if (content == null || content.size() == 0) {
//            return notFound(result);
//        }
//        else
//        {
        Json.setObjectMapper(Serializer.searchMapper);
        result.put("results", Json.toJson(content));
//            result.put("excerpt", Json.toJson())
        result.put("success", "true");
        return ok(result);
//        }
    }

}
