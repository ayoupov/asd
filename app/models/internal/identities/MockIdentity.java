package models.internal.identities;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.BasicIdentity;
import com.feth.play.module.pa.user.EmailIdentity;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 18.09.2015
 * Time: 14:08
 */
public class MockIdentity extends AuthUser implements BasicIdentity
{
    private String provider = "internal";
    private String name;
    private String id;

    public MockIdentity(String id, String name)
    {
        this.id = id;
        this.name = name;
        this.provider = "internal";
    }

    public MockIdentity(String id, String name, String provider)
    {
        this.id = id;
        this.name = name;
        this.provider = provider;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getProvider()
    {
        return provider;
    }

    @Override
    public String getEmail()
    {
        return id + "@" + getProvider();
    }

    @Override
    public String getName()
    {
        return name;
    }
}
