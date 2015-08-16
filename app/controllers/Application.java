package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.MediaContentType;
import models.internal.ContentManager;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

public class Application extends Controller
{

    public static Result index()
    {
        return ok(index.render("Your new application is ready."));
    }

    public static Result summary()
    {
        beginTransaction();
        ObjectNode result = Json.newObject();
        // stories and articles
        // stories ids, sorted by starred
        // stories starred quantity
        // but! stories are to be also sorted by date
        // articles ids, sorted by starred
        // articles starred quantity
        List articleSummary = ContentManager.getSummary(MediaContentType.Article);
        List storySummary = ContentManager.getSummary(MediaContentType.Story);
//        long churchCount = ContentManager.getChurchCount();
        ObjectNode dataNode = Json.newObject();
        dataNode.put("articles", Json.toJson(articleSummary));
        dataNode.put("stories", Json.toJson(storySummary));
        result.put("data", dataNode);
//        result.put("churches", churchCount);
        result.put("success", true);
        commitTransaction();
        return ok(result);
    }
}
