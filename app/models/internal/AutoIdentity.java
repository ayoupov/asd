package models.internal;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 18.09.2015
 * Time: 14:08
 */
public class AutoIdentity extends AuthUser
{
    private static final AutoIdentity instance = new AutoIdentity();

    public static AutoIdentity getInstance()
    {
        return instance;
    }

    private AutoIdentity()
    {
    }

    @Override
    public String getId()
    {
        return "asd:robot";
    }

    @Override
    public String getProvider()
    {
        return "internal";
    }
}
