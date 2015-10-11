package utils.web;

import controllers.Auth;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.ServerProperties;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 02.10.2015
 * Time: 16:20
 */
public class PasswordProtectionAnnotationAction extends Action<PasswordProtectionAnnotation>
{

    public static final String PWDPROVIDED = "pwdprovided";
    private static final String PROTPWD = "dragons&dungeons";
    private static final String PWD = "pwd";
    private static final boolean debugmode = !ServerProperties.isInProduction();

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable
    {
        if (ctx.session().get(PWDPROVIDED) != null) {
//            if (debugmode) System.out.println("passed pwd prot");
            return delegate.call(ctx);
        } else {
            String pwd;
//            if (debugmode) System.out.println(ctx.request());
            if ((pwd = ctx.request().getQueryString(PWD)) != null) {
//                if (debugmode) System.out.println("pwd provided = " + pwd);
                if (PROTPWD.equals(pwd)) {
                    ctx.session().put(PWDPROVIDED, "true");
//                    if (debugmode) System.out.println("got into");
                    return delegate.call(ctx);
                }
            }
            return F.Promise.pure(Auth.pwdprot());
        }
    }
}
