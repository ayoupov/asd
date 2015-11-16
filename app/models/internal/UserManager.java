package models.internal;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import models.LinkedAccount;
import models.internal.email.EmailSubstitution;
import models.internal.email.EmailUnsubscription;
import models.internal.email.EmailWrapper;
import models.internal.identities.AutoIdentity;
import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Query;
import play.Logger;
import play.api.mvc.Session;
import play.mvc.Http;
import utils.ServerProperties;
import utils.service.auth.Str2Hex;

import java.io.Serializable;
import java.util.*;

import static com.feth.play.module.pa.PlayAuthenticate.getProvider;
import static models.internal.email.EmailWrapper.sendEmail;
import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 21:56
 */
public class UserManager
{
    private static Random userHashRandom = new Random(1000003434l);

    public static User getAutoUser()
    {
        List<User> authUserFind = getAuthUserFind(AutoIdentity.getInstance());
        User user = (authUserFind != null && authUserFind.size() > 0) ? authUserFind.get(0) : null;
        if (user == null) {
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
                        "and la.user = u ")
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
        User user = (authUserFind != null && authUserFind.size() > 0) ? authUserFind.get(0) : null;
        ;
        return user;
    }

    public static void mergeUsers(User thisUser, final User otherUser)
    {
        Logger.info("Merging users ", thisUser, otherUser);
        List<LinkedAccount> linkedAccounts = thisUser.getLinkedAccounts();
        for (final LinkedAccount acc : otherUser.getLinkedAccounts()) {
            LinkedAccount account = createLinkedAccount(acc);
            account.setUser(thisUser);
            saveOrUpdate(account);
            linkedAccounts.add(account);
        }
        // do all other merging stuff here - like resources, etc.
        // ???

        // deactivate the merged user that got added to this one
        otherUser.setStatus(UserStatus.DEACTIVATED);
        saveOrUpdate(thisUser);
        saveOrUpdate(otherUser);
    }

    public static User createUser(final AuthUser authUser)
    {
        return createUser(authUser, UserRole.User);
    }

    public static User createUser(final AuthUser authUser, UserRole role)
    {
        final User user = new User();
        user.setStatus(UserStatus.Active);
        user.setRole(role);
        LinkedAccount account = createLinkedAccount(authUser);
        user.setLinkedAccounts(Collections.singletonList(account));

//        user.setHash(nextHash());
        String name = null, email = null;
        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
            // Remember, even when getting them from FB & Co., emails should be
            // verified within the application as a security breach there might
            // break your security as well!
            user.setEmailValidated(false);
            email = identity.getEmail();
            if (email != null) {
                user.setEmail(email);
            }
        }

        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            name = identity.getName();
            if (name != null) {
                user.setName(name);
            }
        }

        EmailUnsubscription eu = null;
        if (email != null && name != null && authUser instanceof EmailIdentity && authUser instanceof NameIdentity) {
            user.setHash(StringUtils.substring(Str2Hex.byteArray2Hex((email + name).getBytes()), 0, 32));
            // send greetings
            try {
                String hash = UUID.randomUUID().toString();
                eu = new EmailUnsubscription(user, hash);
                sendEmail(
                        EmailWrapper.EmailNames.UserRegistered,
                        null,
                        user,
                        Pair.of(EmailSubstitution.Username.name(), user.getName()),
                        Pair.of(EmailSubstitution.UnsubscribeLink.name(), UserManager.getUnsubscribeLink(hash))
                );
                user.setUnsubscribed(false);
            } catch (Exception e) {
                Logger.error("error while sending greetings", e);
            }
        } else
            user.setHash(StringUtils.substring(Str2Hex.byteArray2Hex((account.getProviderUserId() + account.getProviderKey()).getBytes()), 0, 32));

        Serializable id = save(user);
        user.setId((Long) id);
        account.setUser(user);
        saveOrUpdate(account);
        if (eu != null)
            save(eu);
        return user;
    }

    private static String nextHash()
    {
        return UUID.randomUUID().toString().replace("-", "");
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
        LinkedAccount account = createLinkedAccount(newUser);
        u.getLinkedAccounts().add(account);
        account.setUser(u);
        saveOrUpdate(u);
        saveOrUpdate(account);
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

    public static User getLocalUser(final Http.Session session)
    {
        final User localUser = UserManager.findByAuthUserIdentity(PlayAuthenticate
                .getUser(session));
        return localUser;
    }


    /**
     * rewritten for scala class FileManager
     **/
    public static User getLocalUser(final Session session)
    {
        final User localUser = UserManager.findByAuthUserIdentity(getUser(session));
        return localUser;
    }

    public static AuthUser getUser(Session session)
    {
        String provider = session.get("pa.p.id").get();
        String id = session.get("pa.u.id").get();
        long expires = getExpiration(session);

        if ((provider != null) && (id != null)) {
            return getProvider(provider).getSessionAuthUser(id, expires);
        }
        return null;
    }

    private static long getExpiration(Session session)
    {
        long expires;
        if (session.get("pa.u.exp").get() != null) {
            try {
                expires = Long.parseLong(session.get("pa.u.exp").get());
            } catch (NumberFormatException nfe) {
                expires = -1L;
            }
        } else {
            expires = -1L;
        }
        return expires;
    }

    public static String getUnsubscribeLink(String hash)
    {
        return ServerProperties.getValue("asd.absolute.url") + "unsubscribe/" + hash;
    }

    public static EmailUnsubscription findUnsubscription(User user)
    {
        return (EmailUnsubscription) getSession()
                .createQuery("from EmailUnsubscription eu where eu.subscriber = :u")
                .setEntity("u", user)
                .uniqueResult();
    }
}
