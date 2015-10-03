package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Point;
import models.Church;
import models.MediaContentType;
import models.internal.ContentManager;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.serialize.PointConverter;
import utils.web.PasswordProtectionAnnotation;
import views.html.index;

import java.util.List;
import java.util.Map;

import static models.internal.UserManager.getLocalUser;
import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

@PasswordProtectionAnnotation
public class Application extends Controller
{
    static ObjectMapper alsoGeometryMapper;

    static {
        alsoGeometryMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("PointSerializerModule");
        module.addSerializer(Point.class, new PointConverter());
        alsoGeometryMapper.registerModule(module);
    }

    public static Result index(String churchId)
    {
        beginTransaction();
        // add other static data
        long churchCount = ContentManager.getChurchCount();
        Church currentChurch = ContentManager.getChurch(churchId);
        System.out.println("currentChurch = " + currentChurch);
        Html content = index.render(churchCount, currentChurch, getLocalUser(session()));
        commitTransaction();
        return ok(content);
    }


    public static Result summary()
    {
        beginTransaction();
        Json.setObjectMapper(alsoGeometryMapper);
        ObjectNode result = Json.newObject();
        // stories and articles
        // stories ids, sorted by starred
        // stories starred quantity
        // but! stories are to be also sorted by date
        // articles ids, sorted by starred
        // articles starred quantity
        List articleSummary = ContentManager.getSummary(MediaContentType.Article);
        List storySummary = ContentManager.getSummary(MediaContentType.Story);
        Map<String, Object> countSummary = ContentManager.getChurchCountSummary();
//        List churches = ContentManager.getChurchesShort();
        ObjectNode dataNode = Json.newObject();
        dataNode.put("articles", Json.toJson(articleSummary));
        dataNode.put("stories", Json.toJson(storySummary));
        dataNode.put("geostats", Json.toJson(countSummary));
//        dataNode.put("churches", Json.toJson(churches));
        result.put("data", dataNode);
        result.put("success", true);
        commitTransaction();
        response().setHeader("Cache-Control", "no-transform,public,max-age=3600,s-maxage=3600");
        return ok(result);
    }

}
