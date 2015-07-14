package models.internal;

import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtils;

import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 21:56
 */
public class UserManager
{
    public static User getAutoUser()
    {
        User user = (User) get(User.class, "asd:robot");
        if (user == null)
        {
            user = new User("ASD_Robot", UserRole.Administrator, UserStatus.Active);
            user.setId("asd:robot");
            saveOrUpdate(user);
        }
        return user;
    }
}
