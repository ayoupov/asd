package utils.service;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.google.inject.Inject;
import models.user.User;
import models.internal.UserManager;
import play.Application;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

public class MyUserServicePlugin extends UserServicePlugin
{

    @Inject
    public MyUserServicePlugin(final Application app)
    {
        super(app);
    }

    @Override
    public Object save(final AuthUser authUser)
    {
        beginTransaction();
        final boolean isLinked = UserManager.existsByAuthUserIdentity(authUser);
        if (!isLinked) {
            long id = UserManager.createUser(authUser).getId();
            commitTransaction();
            return id;
        } else {
            // we have this user already, so return null
            commitTransaction();
            return null;
        }
    }

    @Override
    public Object getLocalIdentity(final AuthUserIdentity identity)
    {
        // For production: Caching might be a good idea here...
        // ...and dont forget to sync the cache when users get deactivated/deleted
        beginTransaction();
        final User u = UserManager.findByAuthUserIdentity(identity);
        commitTransaction();
        if (u != null) {
            return u.getId();
        } else {
            return null;
        }
    }

    @Override
    public AuthUser merge(final AuthUser newUser, final AuthUser oldUser)
    {
        beginTransaction();
        if (!oldUser.equals(newUser)) {
            UserManager.mergeUsers(oldUser, newUser);
        }
        commitTransaction();
        return oldUser;
    }

    @Override
    public AuthUser link(final AuthUser oldUser, final AuthUser newUser)
    {
        beginTransaction();
        UserManager.addLinkedAccount(oldUser, newUser);
        commitTransaction();
        return newUser;
    }

}
