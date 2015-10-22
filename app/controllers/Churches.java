package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Church;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ChurchSuggestion;
import models.internal.ChurchSuggestionType;
import models.internal.ContentManager;
import models.internal.UserManager;
import models.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.HibernateUtils;
import utils.serialize.Serializer;

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
            ChurchSuggestion cs = suggestionForm.bindFromRequest().get();
            cs.setType(type);
            cs.setField(field);
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

    @Security.Authenticated(Secured.class)
    // todo: redo with forms
    public static Result addStory()
    {
        ObjectNode result = Json.newObject();
        Http.RequestBody body = request().body();
        Map<String, String[]> map = body.asFormUrlEncoded();
        beginTransaction();
        User user = UserManager.getLocalUser(session());
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
            coverPath = "/" + User.anonymousHash() + ("/church_story_" + jcover + "_thumb_is.png");
        } else return badRequest("cover is null");
        if (jyear != null)
            year = jyear;
        String coverThumbPath = MediaContents.relativeUploadPath + coverPath;
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
        return play.mvc.Results.TODO;
    }
}
