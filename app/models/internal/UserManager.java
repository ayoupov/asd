package models.internal;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import models.LinkedAccount;
import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import org.hibernate.Query;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<User> authUserFind = getAuthUserFind(AutoIdentity.getInstance());
        User user = (authUserFind != null && authUserFind.size() > 0) ? authUserFind.get(0) : null;
        if (user == null)
        {
            user = createUser(AutoIdentity.getInstance());
            user.setRole(UserRole.Administrator);
            saveOrUpdate(user);
        }
        return user;
    }

//    public static User getBySocialId(String socialId)
//    {
//        User user = (User) getSession().createQuery("from User u where u.socialId = :sid")
//                .setParameter("sid", socialId)
//                .uniqueResult();
//        return user;
//    }

    public static List<Object[]> getUserNames(String like)
    {
        if (like == null)
            return getSession().createQuery("select u.id, u.name from User u where u.status = :st")
                    .setParameter("st", UserStatus.Active)
                    .setMaxResults(10)
                    .setCacheable(true).list();
        else {
            Query q = getSession().createQuery("select u.id, u.name from User u where u.status = :st and lower(u.name) like :lk")
                    .setParameter("st", UserStatus.Active)
                    .setParameter("lk", "%" + like.toLowerCase() + "%")
                    .setMaxResults(10);
            return q.list();
        }
    }

    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity)
    {
        final List<User> exp = getAuthUserFind(identity);
        return exp.size() > 0;
    }

    private static List<User> getAuthUserFind(final AuthUserIdentity identity)
    {
        // todo: should return List<User> by identity.id and identity.provider
//        return find.where().eq("active", true)
//                .eq("linkedAccounts.providerUserId", identity.getId())
//                .eq("linkedAccounts.providerKey", identity.getProvider());
        return getSession()
                .createQuery("select u from User u, LinkedAccount la " +
                        "where la.providerUserId = :puid " +
                        "and la.providerKey = :pk " +
                        "and la member of u.linkedAccounts ")
                .setParameter("puid", identity.getId())
                .setParameter("pk", identity.getProvider())
                .list();
    }

    public static User findByAuthUserIdentity(final AuthUserIdentity identity)
    {
        if (identity == null) {
            return null;
        }
        List<User> authUserFind = getAuthUserFind(identity);
        User user = (authUserFind != null && authUserFind.size() > 0) ? authUserFind.get(0) : null;;
        return user;
    }

    public static void mergeUsers(User thisUser, final User otherUser)
    {
        List<LinkedAccount> linkedAccounts = thisUser.getLinkedAccounts();
        for (final LinkedAccount acc : otherUser.linkedAccounts) {
            linkedAccounts.add(createLinkedAccount(acc));
        }
        // do all other merging stuff here - like resources, etc.
        // todo: ^^^ !!!
        // deactivate the merged user that got added to this one
        otherUser.setStatus(UserStatus.DEACTIVATED);
        // todo: store both
    }

    public static User createUser(final AuthUser authUser)
    {
        final User user = new User();
        user.setStatus(UserStatus.Active);
        user.linkedAccounts = Collections.singletonList(
                createLinkedAccount(authUser));

        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
            // Remember, even when getting them from FB & Co., emails should be
            // verified within the application as a security breach there might
            // break your security as well!
            user.email = identity.getEmail();
            user.emailValidated = false;
        }

        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                user.name = name;
            }
        }

        saveOrUpdate(user);
        return user;
    }

    public static void mergeUsers(final AuthUser oldUser, final AuthUser newUser)
    {
        mergeUsers(findByAuthUserIdentity(oldUser),
                findByAuthUserIdentity(newUser));
    }

    public Set<String> getProviders(User user)
    {
        List<LinkedAccount> linkedAccounts = user.getLinkedAccounts();
        final Set<String> providerKeys = new HashSet<String>(linkedAccounts.size());
        for (final LinkedAccount acc : linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    public static void addLinkedAccount(final AuthUser oldUser,
                                        final AuthUser newUser)
    {
        // should update linked accounts list and store it
        final User u = findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(createLinkedAccount(newUser));
		saveOrUpdate(u);
    }

    public static User findByEmail(final String email)
    {
        // should return a unique user by email
        return (User) getSession().createQuery("from User u where u.email = :e ")
                .setParameter("e", email)
                .uniqueResult();
    }

    public static LinkedAccount findByProviderKey(final User user, final String key)
    {
        // should return unique la by user and provider name
        return (LinkedAccount) getSession()
                .createQuery("from LinkedAccount la where la.user = :u and la.providerKey = :pk")
                .setParameter("u", user)
                .setParameter("pk", key)
                .uniqueResult();

    }

    public static LinkedAccount createLinkedAccount(final AuthUser authUser)
    {
        final LinkedAccount ret = new LinkedAccount();
        updateLinkedAccount(ret, authUser);
        return ret;
    }

    public static void updateLinkedAccount(LinkedAccount acc, final AuthUser authUser)
    {
        acc.providerKey = authUser.getProvider();
        acc.providerUserId = authUser.getId();
    }

    public static LinkedAccount createLinkedAccount(final LinkedAccount acc)
    {
        final LinkedAccount ret = new LinkedAccount();
        ret.providerKey = acc.providerKey;
        ret.providerUserId = acc.providerUserId;

        return ret;
    }


}
