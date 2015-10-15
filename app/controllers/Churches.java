package controllers;

import models.Church;
import models.MediaContent;
import models.internal.ChurchSuggestion;
import models.internal.ContentManager;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.serialize.Serializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class Churches extends Controller
{

    private static final Set<String> ALLOWED_ENTITIES = new HashSet<>();
    static {
        ALLOWED_ENTITIES.add("story");
        ALLOWED_ENTITIES.add("field");
        ALLOWED_ENTITIES.add("images");
    }

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

    public static Result update()
    {
        String[] reqEntity = request().body().asFormUrlEncoded().get("entity");
        String entity;
        if (reqEntity == null || reqEntity.length == 0 || !ALLOWED_ENTITIES.contains(entity = reqEntity[0]))
        {
            return badRequest();
        } else
        {
            switch(entity)
            {
                case "story" : return processStory();
            }
        }
        return TODO;
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
}
