package utils.service.auth;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.google.inject.Inject;
import play.Application;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Http;

import static com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.UsernamePassword;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 05.10.2015
 * Time: 16:52
 */
public class EmailAuthProvider<R,
        UL extends UsernamePasswordAuthUser,
        US extends UsernamePasswordAuthUser,
        L extends UsernamePassword,
        S extends UsernamePassword> extends
        com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider<R,
                UL,
                US,
                L,
                S>
{
    @Inject
    public EmailAuthProvider(Application app)
    {
        super(app);
    }

    @Override
    protected R generateVerificationRecord(US user)
    {
        System.out.println("generateVerificationRecord");
        return null;
    }

    @Override
    protected String getVerifyEmailMailingSubject(US user, Http.Context ctx)
    {
        System.out.println("getVerifyEmailMailingSubject");
        return null;
    }

    @Override
    protected Mailer.Mail.Body getVerifyEmailMailingBody(R verificationRecord, US user, Http.Context ctx)
    {
        System.out.println("getVerifyEmailMailingBody");
        return null;
    }

    @Override
    protected UL buildLoginAuthUser(L login, Http.Context ctx)
    {
        System.out.println("buildLoginAuthUser");
        return null;
    }

    @Override
    protected UL transformAuthUser(US authUser, Http.Context context)
    {
        System.out.println("transformAuthUser");
        return null;
    }

    @Override
    protected US buildSignupAuthUser(S signup, Http.Context ctx)
    {
        System.out.println("buildSignupAuthUser");
        return null;
    }

    @Override
    protected LoginResult loginUser(UL authUser)
    {
        System.out.println("loginUser");
        return null;
    }

    @Override
    protected SignupResult signupUser(US user)
    {
        System.out.println("signupUser");
        return null;
    }

    @Override
    protected Form<S> getSignupForm()
    {
        System.out.println("getSignupForm");
        return null;
    }

    @Override
    protected Form<L> getLoginForm()
    {
        System.out.println("getLoginForm");
        return null;
    }

    @Override
    protected Call userExists(UsernamePasswordAuthUser authUser)
    {
        System.out.println("userExists");
        return null;
    }

    @Override
    protected Call userUnverified(UsernamePasswordAuthUser authUser)
    {
        System.out.println("userUnverified");
        return null;
    }

    @Override
    public Object authenticate(final Http.Context context, final Object payload)
            throws AuthException
    {
        System.out.println("payload = " + payload);
        return super.authenticate(context, payload);
    }
}