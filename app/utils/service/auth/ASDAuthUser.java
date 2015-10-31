package utils.service.auth;

import com.feth.play.module.pa.providers.password.DefaultUsernamePasswordAuthUser;
import com.feth.play.module.pa.user.NameIdentity;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 29.10.2015
 * Time: 16:39
 */
public class ASDAuthUser extends DefaultUsernamePasswordAuthUser implements NameIdentity
{
    private String name = "Unknown";

    public ASDAuthUser(String clearPassword, String email)
    {
        super(clearPassword, email);
    }

    public ASDAuthUser(String name, String clearPassword, String email)
    {
        super(clearPassword, email);
        this.name = name;

    }

    public String getName()
    {
        return name;
    }
}
