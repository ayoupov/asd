package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller
{

    public static Result index()
    {
        return ok(index.render("Your new application is ready."));
    }

    public static Result settings()
    {
        ObjectNode result = Json.newObject();

    }
}
