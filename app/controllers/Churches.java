package controllers;

import models.Church;
import play.mvc.Controller;
import play.mvc.Result;

import static utils.HibernateUtils.get;

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
        Church church = (Church) get(Church.class, id);
        if (church != null)
            return ok("Got request " + request() + "!" + "Found: " + church);
        else
            return notFound(String.format("Church with id {%s}", id));
    }

}
