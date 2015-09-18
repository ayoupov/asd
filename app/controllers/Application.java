package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.vividsolutions.jts.geom.Point;
import models.MediaContentType;
import models.internal.ContentManager;
import models.user.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.serialize.PointConverter;
import views.html.index;

import java.util.List;
import java.util.Map;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

public class Application extends Controller
{
    static ObjectMapper alsoGeometryMapper;

    static {
        alsoGeometryMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("PointSerializerModule");
        module.addSerializer(Point.class, new PointConverter());
        alsoGeometryMapper.registerModule(module);
    }

    public static Result index()
    {
        beginTransaction();
        // add other static data
        long churchCount = ContentManager.getChurchCount();
        commitTransaction();
        Html content = index.render(churchCount);
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

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";

    public static Result oAuthDenied(final String providerKey) {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        flash(FLASH_ERROR_KEY,
                "You need to accept the OAuth connection in order to use this website!");
        return redirect(routes.Application.index());
    }

    public static User getLocalUser(final Http.Session session) {
        final User localUser = ContentManager.findUserByAuthIdentity(PlayAuthenticate
                .getUser(session));
        return localUser;
    }

}
