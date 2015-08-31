package models.internal;

import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import org.hibernate.Query;

import java.util.List;

import static utils.HibernateUtils.getSession;
import static utils.HibernateUtils.saveOrUpdate;

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
        if (user == null) {
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

    public static List<Object[]> getUserNames(String like)
    {
        if (like == null)
            return getSession().createQuery("select u.id, u.name from Users u where u.status = :st")
                    .setParameter("st", UserStatus.Active)
                    .setMaxResults(10)
                    .setCacheable(true).list();
        else {
            Query q = getSession().createQuery("select u.id, u.name from Users u where u.status = :st and lower(u.name) like :lk")
                    .setParameter("st", UserStatus.Active)
                    .setParameter("lk", "%" + like.toLowerCase() + "%")
                    .setMaxResults(10);
            return q.list();
        }
    }
}
