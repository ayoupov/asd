package models.internal;

import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;

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
        User user = getBySocialId("asd:robot");
        if (user == null)
        {
            user = new User("ASD_Robot", UserRole.Administrator, UserStatus.Active, "asd:robot");
            saveOrUpdate(user);
        }
        return user;
    }

    public static User getBySocialId(String socialId)
    {
        User user = (User) getSession().createQuery("from Users u where u.socialId = :sid")
                .setParameter("sid", socialId)
                .uniqueResult();
        return user;
    }
}
