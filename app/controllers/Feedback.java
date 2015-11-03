package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.internal.UserFeedback;
import models.internal.UserManager;
import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.service.auth.ASDAuthUser;

import java.util.Date;

import static models.internal.UserManager.createUser;
import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class Feedback extends Controller
{

    public static Result feedback()
    {
        Form<UserFeedback> feedbackForm = Form.form(UserFeedback.class).bindFromRequest();
        if (!feedbackForm.hasErrors()) {
            beginTransaction();
            User user = UserManager.getLocalUser(session());
            if (user == null)
                user = loginEmail();
            UserFeedback uf = feedbackForm.get();
            if (user == null && uf.getName() != null) {
                user = new User(uf.getName(), UserRole.Guest, UserStatus.Active);
            }
            uf.setSuggestedBy(user);
            uf.setSuggestedOn(new Date());
            saveOrUpdate(uf);
            commitTransaction();
            return ok(Json.newObject().put("success", true));
        } else
            return badRequest(feedbackForm.errorsAsJson());
    }

    private static User loginEmail()
    {
        AuthUser authUserFromRequest = getIdentityFromRequest();
        User user = null;
        if (authUserFromRequest != null) {
            user = UserManager.findByAuthUserIdentity(authUserFromRequest);
            if (user == null) // new user
            {
                user = createUser(authUserFromRequest, UserRole.User);
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
