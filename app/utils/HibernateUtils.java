package utils;

import com.vividsolutions.jts.geom.Coordinate;
import models.address.Diocese;
import models.address.Geometrified;
import models.address.Metropolie;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

import java.io.Serializable;
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
            configuration.configure();
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
        return ourSessionFactory.getCurrentSession();
    }

    public static Serializable save(Object object)
    {
        Transaction transaction = getSession().beginTransaction();
        Serializable res = getSession().save(object);
        transaction.commit();
        return res;
    }

    public static void saveOrUpdate(Object object)
    {
        Transaction transaction = getSession().beginTransaction();
        getSession().saveOrUpdate(object);
        transaction.commit();
    }

    public static Object load(Class<?> clazz, Serializable id)
    {
        Transaction transaction = getSession().beginTransaction();
        Object obj = getSession().load(clazz, id);
        transaction.commit();
        return obj;
    }

    public static Object get(Class<?> clazz, Serializable id)
    {
        Transaction transaction = getSession().beginTransaction();
        Object obj = getSession().get(clazz, id);
        transaction.commit();
        return obj;
    }
}
