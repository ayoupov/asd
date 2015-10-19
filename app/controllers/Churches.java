package controllers;

import models.Church;
import models.MediaContent;
import models.internal.ChurchSuggestion;
import models.internal.ChurchSuggestionType;
import models.internal.ContentManager;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.serialize.Serializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static Result addStory()
    {
        return play.mvc.Results.TODO;
    }

    public static Result addImages()
    {
        return play.mvc.Results.TODO;
    }
}
