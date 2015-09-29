package controllers;

import models.internal.UserManager;
import models.user.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
//import views.html.restricted;

@Security.Authenticated(Secured.class)
public class Restricted extends Controller
{

    public static Result index()
    {
        beginTransaction();
        final User localUser = UserManager.getLocalUser(session());
        commitTransaction();
//        return ok(restricted.render(localUser));
        if (localUser == null)
            return forbidden("no user");
        return ok(Json.toJson(localUser));
    }
}
