package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import utils.ServerProperties;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Auth extends Controller
{

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    private static Set<String> allowedHosts = new HashSet<>();

    static {
        // todo: put in ServerProperties
        allowedHosts.add("localhost");
        allowedHosts.add("architektura7dnia.pl");
    }

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
        return allowedHosts.contains(getHost(callbackUrl));
    }

    private static String getHost(String url)
    {
        try {
            return new URL(url).getHost();
        } catch (Exception e)
        {
            return null;
        }
    }

    public static Result logout()
    {
        return com.feth.play.module.pa.controllers.Authenticate.logout();
    }
}
