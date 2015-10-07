package utils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.vividsolutions.jts.geom.Coordinate;
import models.address.Diocese;
import models.address.Geometrified;
import models.address.Metropolie;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.07.2015
 * Time: 18:39
 */
public class HibernateUtils
{
    private static final SessionFactory ourSessionFactory;
    private static final ServiceRegistry serviceRegistry;

    static {
        try {
            Configuration configuration = new Configuration();
            String hibConfigRes = ServerProperties.isInProduction() ? "hibernate.prod.cfg.xml" : "hibernate.cfg.xml";
            System.out.println("hibConfigRes = " + hibConfigRes);
            configuration.configure(hibConfigRes);
            Reflections reflections = new Reflections("models");

            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(javax.persistence.Entity.class);

            for (Class<?> clazz : classes) {
                configuration.addAnnotatedClass(clazz);
            }
            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException
    {
        Session session = ourSessionFactory.getCurrentSession();
        // get method which invoked getSession()

        return session;
    }

    public static void closeSession() throws HibernateException
    {
        Session session = ourSessionFactory.getCurrentSession();
        session.flush();
    }

    public static Serializable save(Object object)
    {
        Session session = getSession();
        Serializable res = session.save(object);
        return res;
    }

    public static void saveOrUpdate(Object object)
    {
        Session session = getSession();
        session.saveOrUpdate(object);
    }

    public static void update(Object object)
    {
        Session session = getSession();
        session.update(object);
    }

    public static Object load(Class<?> clazz, Serializable id)
    {
        Object obj = getSession().load(clazz, id);
        return obj;
    }

    public static Object get(Class<?> clazz, Serializable id)
    {
//        Transaction transaction = getSession().beginTransaction();
        Object obj = getSession().get(clazz, id);
//        transaction.commit();
        return obj;
    }

    public static void beginTransaction()
    {
        Session session = getSession();
//        Transaction transaction = threadTransactions.get(Thread.currentThread());
        Transaction transaction = session.getTransaction();
        if (transaction == null || !transaction.isActive())
            transaction = session.beginTransaction();
    }

    public static void commitTransaction()
    {
        getSession().getTransaction().commit();
//        ourSessionFactory.close();
//        getSession().close();
    }

    public static boolean delete(Class cl, Serializable id)
    {
        boolean res = true;
        try {
            getSession().delete(get(cl, id));
        } catch (Exception e)
        {
            res = false;
        }
        return res;
    }
}
