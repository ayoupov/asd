package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Church;
import models.internal.search.SearchManager;
import play.libs.Json;
import play.mvc.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 15.07.2015
 * Time: 1:36
 */
public class Search extends Controller
{
    public static Result byText(String q)
    {
        ObjectNode result = Json.newObject();
        result.put("q", q);
        List<Church> churches = SearchManager.searchChurches(q);
        if (churches == null || churches.size() == 0) {
            return notFound(result);
        }
        else
        {
            result.put("churches", Json.toJson(churches));
            return ok(result);
        }
    }
}
