package models.internal.identities;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.BasicIdentity;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 18.09.2015
 * Time: 14:08
 */
public class AutoIdentity extends AuthUser implements BasicIdentity
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
        return "robot";
    }

    @Override
    public String getProvider()
    {
        return "internal";
    }

    @Override
    public String getEmail()
    {
        return "asd.robot@localhost";
    }

    @Override
    public String getName()
    {
        return "ASD Robot";
    }
}
