package controllers;

import models.Church;
import models.internal.ChurchSuggestion;
import models.internal.ContentManager;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.serialize.Serializer;

import java.util.List;

import static utils.HibernateUtils.*;

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

    public static Result suggest()
    {
        Form<ChurchSuggestion> suggestionForm = Form.form(ChurchSuggestion.class);
        if (!suggestionForm.hasErrors()) {
            beginTransaction();
            ChurchSuggestion cs = suggestionForm.bindFromRequest().get();
            saveOrUpdate(cs);
            commitTransaction();
            return ok();
        } else
            return badRequest(suggestionForm.errorsAsJson());
    }

    public static Result revsById(String id)
    {
        beginTransaction();
        List<Church> versions = ContentManager.getChurchVersions(id);
        commitTransaction();
        return ok(Json.toJson(versions));
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
}
