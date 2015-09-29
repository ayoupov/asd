package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Auth extends Controller
{

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";

    public static Result oAuthDenied(final String providerKey)
    {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        flash(FLASH_ERROR_KEY,
                "You need to accept the OAuth connection in order to use this website!");
        // todo: church back?
        return redirect(routes.Application.index(null));
    }


    public static Result auth(String provider)
    {
        String callbackUrl = request().getHeader("Referer");
        if (request().method().equals("GET")
                && callbackUrl != null && !"".equals(callbackUrl) && checkAgainstWhitelist(callbackUrl))
        {
            session().put("pa.url.orig",
                    callbackUrl);
        }

        System.out.println("callbackUrl = " + callbackUrl);
        return com.feth.play.module.pa.controllers.Authenticate.authenticate(provider);
    }

    private static boolean checkAgainstWhitelist(String callbackUrl)
    {
        // todo: todo:todo
        return true;
    }

    public static Result logout()
    {
        return com.feth.play.module.pa.controllers.Authenticate.logout();
    }
}
