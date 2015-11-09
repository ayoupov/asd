package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Church;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.email.EmailSubstitution;
import models.internal.email.EmailTemplate;
import models.internal.email.EmailWrapper;
import models.user.User;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.serialize.Serializer;
import utils.web.PasswordProtectionAnnotation;
import views.html.index;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import static controllers.Admin.roleCheck;
import static models.internal.UserManager.getLocalUser;
import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
import static utils.HibernateUtils.saveOrUpdate;
import static utils.ServerProperties.isInProduction;

@PasswordProtectionAnnotation
public class Application extends Controller
{

    public static Result index(String churchId)
    {
        beginTransaction();
        Json.setObjectMapper(Serializer.emptyMapper);
        // add other static data
        long churchCount = ContentManager.getChurchCount();
        Church currentChurch = ContentManager.getChurch(churchId);
        if (!isInProduction()) Logger.info("currentChurch = " + currentChurch);
        Html content = index.render(churchCount, currentChurch, getLocalUser(session()));
        commitTransaction();
        return ok(content);
    }


    public static Result summary()
    {
        beginTransaction();
        Json.setObjectMapper(Serializer.pointMapper);
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


    public static Result emailGet(String name)
    {
        if (roleCheck()) {
            beginTransaction();
            JsonNode jsonNode = Json.toJson(ContentManager.emailByName(name));
            commitTransaction();
            return ok(jsonNode);
        } else return forbidden();
    }

    public static Result emailPost(String name)
    {
        if (roleCheck()) {
            Form<EmailTemplate> etf = Form.form(EmailTemplate.class).bindFromRequest(request());
            if (!etf.hasErrors()) {
                beginTransaction();
                EmailTemplate emailTemplate = etf.get();
                saveOrUpdate(emailTemplate);
                commitTransaction();
                ObjectNode result = Json.newObject();
                result.put("success", true);
                return ok(result);
            } else {
                return notFound();
            }
        } else return forbidden();
    }

    public static Result emailCheck(String name) throws MalformedURLException, EmailException
    {
        if (roleCheck()) {

            Form<EmailTemplate> etf = Form.form(EmailTemplate.class).bindFromRequest(request());
            if (!etf.hasErrors()) {
                EmailTemplate emailTemplate = etf.get();
                beginTransaction();
                User user = getLocalUser(session());
                commitTransaction();
                String username = user.getName();
                Pair<String, String> pair = Pair.of(EmailSubstitution.Username.name(), username);
                EmailWrapper.sendEmail(emailTemplate.getProcessedBody(pair), emailTemplate.getProcessedSubject(pair), null, user);
                ObjectNode result = Json.newObject();
                result.put("success", true);
                return ok(result);
            } else {
                return notFound();
            }
        } else return forbidden();
    }
}
