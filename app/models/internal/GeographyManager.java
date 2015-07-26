package models.internal;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import models.address.Address;
import models.address.Geometrified;
import models.address.Parish;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.opengis.filter.spatial.BBOX;
import utils.map.GeocodeUtils;
import utils.map.TileBuilder;

import java.util.ArrayList;
import java.util.List;

import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.07.2015
 * Time: 18:33
 */
public class GeographyManager
{
    private static final double TOLERANCE = 100 * 1.56961231e-7; // rough magic (meters * earth rad const)
    private static boolean once = false;

    public static Address check(Geometry geometry)
    {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Address address = (Address) session.createQuery("select a from Address a " +
                "where ST_Contains(ST_Buffer(:g, :t), a.geometry) = 1")
                .setParameter("g", geometry).setParameter("t", TOLERANCE).uniqueResult();
        transaction.commit();
        return address;
    }

    public static Address add(Point point, String unfolded)
    {
        // 1. reverse geocode -> unfolded
        // 2. find belonging parish
        // 3. store in geocache

        Address result = new Address();
        result.setGeometry(point);
        if (unfolded == null)
            result.setUnfolded(GeocodeUtils.getAddress(point));
        else
            result.setUnfolded(unfolded);
        result.setParish(findParish(point));
        return result;
    }

    private static Parish findParish(Point point)
    {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Parish parish = (Parish) session.createQuery("select p from Parish p where ST_contains(p.geometry, :p) = 1")
                .setParameter("p", point).uniqueResult();
        transaction.commit();
        return parish;
    }

    public static int getChurchesInParish(Parish parish)
    {
        if (parish == null)
            return 0;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        Long count = (Long) session.createQuery("select count(*) from Parish p, Church c " +
                "where ST_contains(p.geometry, c.address.geometry) = 1 and c.address.parish = p").uniqueResult();
        transaction.commit();
        if (count == null)
            return 0;
        return count.intValue();
    }

    public static Object findByPoint(Class<? extends Geometrified> clazz, Point point)
    {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        Object obj = getSession()
                .createQuery("select g from " + clazz.getSimpleName() + " g where ST_contains(g.geometry, :p) = 1")
                .setParameter("p", point)
                .uniqueResult();
        transaction.commit();
        return obj;
    }

    public static List<Geometrified> findByBBox(Class clazz, Geometry against)
    {
        List<Geometrified> res = new ArrayList<>();
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        Query query = getSession()
                .createQuery("select g from " + clazz.getSimpleName() + " g");
        query.setCacheable(true);
        List<Geometrified> list = query.list();

        Geometry checkEnv = null;

        for(Geometrified geo : list)
        {
            Geometry geometry = geo.getGeometry();
//            geometry.setSRID(3857);
//            if (!once) {
//                System.out.println(geometry.getSRID());
//                once = true;
////                System.out.println("checking intersection of " + geometry + " and " + against);
//            }
            if (against.intersects(geometry))
                res.add(geo);
        }
        transaction.commit();
        return res;
    }

}
