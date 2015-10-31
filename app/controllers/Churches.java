package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.SessionUsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.BasicIdentity;
import models.Church;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ChurchSuggestion;
import models.internal.ChurchSuggestionType;
import models.internal.ContentManager;
import models.internal.UserManager;
import models.user.User;
import models.user.UserRole;
import play.Logger;
import play.data.DynamicForm;
import controllers.Assets;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.HibernateUtils;
import utils.serialize.Serializer;
import utils.service.auth.ASDAuthUser;

import java.io.Serializable;
import java.util.*;

import static utils.HibernateUtils.*;
import static utils.ServerProperties.isInProduction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class Churches extends Controller
{

    public static Result byId(String id)
    {
        beginTransaction();
        Church church = ContentManager.getChurch(id);
        commitTransaction();
        if (church != null) {
            Json.setObjectMapper(Serializer.emptyMapper);
            return ok(Json.toJson(church));
        }
        else
            return notFound(String.format("Church with id {%s}", id));
    }

    public static Result images(String id)
    {
        beginTransaction();
        Church church = ContentManager.getChurch(id);
        commitTransaction();
        if (church != null)
            return ok(Json.toJson(church.getImages()));
        else
            return notFound(String.format("Church with id {%s}", id));
    }

    public static Result suggest()
    {
        return processSuggestion(ChurchSuggestionType.NEW_CHURCH, null);
    }

    public static Result updateField(String field)
    {
        return processSuggestion(ChurchSuggestionType.FIELD, field);
    }

    private static Result processSuggestion(ChurchSuggestionType type, String field)
    {
        Form<ChurchSuggestion> suggestionForm = Form.form(ChurchSuggestion.class);
        if (!suggestionForm.hasErrors()) {
            beginTransaction();
            User user = UserManager.getLocalUser(session());
            ChurchSuggestion cs = suggestionForm.bindFromRequest().get();
            cs.setType(type);
            cs.setField(field);
            cs.setSuggestedBy(user);
            saveOrUpdate(cs);
            commitTransaction();
            return ok(Json.newObject().put("success", true));
        } else
            return badRequest(suggestionForm.errorsAsJson());

    }

    private static Result processStory()
    {
        Form<MediaContent> mc = Form.form(MediaContent.class);
        if (mc.hasErrors())
        {
            return internalServerError(mc.errorsAsJson());
        } else {
            beginTransaction();
            MediaContent mediaContent = mc.bindFromRequest(request()).get();
            Serializable id = save(mediaContent);
            commitTransaction();
            return ok("{success:'true',id:'"+id+"'}");
        }
    }

//    @Security.Authenticated(Secured.class)
    // todo: redo with forms
    public static Result addStory()
    {
        System.out.println(request().cookies());
        ObjectNode result = Json.newObject();
        Http.RequestBody body = request().body();
        Map<String, String[]> map = body.asFormUrlEncoded();
        beginTransaction();
        User user = UserManager.getLocalUser(session());
        if (user == null)
            user = loginEmail();
        if (user == null) {
            commitTransaction();
            return forbidden("alas");
        }

        String jtext = (map.get("text") != null) ? map.get("text")[0] : null,
                jcover = (map.get("cover") != null) ? map.get("cover")[0] : null,
                jtitle = (map.get("title") != null) ? map.get("title")[0] : null,
                jchurch = (map.get("church") != null) ? map.get("church")[0] : null,
                jyear = (map.get("year") != null) ? map.get("year")[0] : null;
        String text = null, title = null, coverPath = null;
        String year = null;
        Church church = null;
        if (jtext != null)
            text = jtext;
        else
            return badRequest("text is null");
        if (jtitle != null)
            title = jtitle;
        if (jcover != null) {
            coverPath = "/assets/images/passport/church_story_" + jcover + "_thumb_is.png";
        } else return badRequest("cover is null");
        if (jyear != null)
            year = jyear;
        String coverThumbPath = coverPath;
        if (jchurch != null)
            church = ContentManager.getChurch(jchurch);
        else
            return badRequest("church is absent");
        MediaContent c = new MediaContent(MediaContentType.Story, text, title, year, null, coverThumbPath, user, church);
        c.setId((Long) save(c));
        Set<MediaContent> media = church.getMedia();
        if (media == null)
            media = new LinkedHashSet<>();
        media.add(c);
        church.setMedia(media);
        HibernateUtils.update(church);
        commitTransaction();
        result.put("success", true);
        result.put("id", c.getId());
        return ok(result);
    }

    public static Result addImages()
    {
        beginTransaction();
        User user = UserManager.getLocalUser(session());
        if (user == null)
            user = loginEmail();
        if (user == null)
            return forbidden("sorry");
        commitTransaction();
        return ok("images ok");
    }

    private static User loginEmail()
    {
        AuthUser authUserFromRequest = getIdentityFromRequest();
        User user = null;
        if (authUserFromRequest != null) {
            user = UserManager.findByAuthUserIdentity(authUserFromRequest);
            if (user == null) // new user
            {
                user = UserManager.createUser(authUserFromRequest, UserRole.User);
                System.out.println("user = " + user);
                PlayAuthenticate.storeUser(session(), authUserFromRequest);
                user = UserManager.getLocalUser(session());
                System.out.println("user = " + user);
            }
        }
        return user;
    }

    private static AuthUser getIdentityFromRequest()
    {
        System.out.println("request() = " + request());
        DynamicForm df = Form.form().bindFromRequest();
        String username = df.get("username");
        String useremail = df.get("useremail");
        AuthUser authUser = null;
        if (username != null && !"".equals(username) && useremail != null && !"".equals(useremail))
            authUser = new ASDAuthUser(username, "", useremail);
        return authUser;
    }
}
