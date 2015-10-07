package models.internal;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import models.address.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.opengis.geometry.BoundingBox;
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
        Address address = (Address) session.createQuery("select a from Address a " +
                "where ST_Contains(ST_Buffer(:g, :t), a.geometry) = 1")
                .setParameter("g", geometry).setParameter("t", TOLERANCE).uniqueResult();
        return address;
    }

    public static Address add(Point point, String unfolded)
    {
        // 1. reverse geocode -> unfolded
        // 2. find belonging dekanat
        // 3. store in geocache

        Address result = new Address();
        result.setGeometry(point);
        if (unfolded == null)
            result.setUnfolded(GeocodeUtils.getAddress(point));
        else
            result.setUnfolded(unfolded);
        List<Dekanat> dekanats = findDekanats(point);
        if (dekanats.size() > 1) {
            System.out.println(String.format("Alarma! : %s {%s} is in %d dekanats! ",
                    unfolded, point.toString(), dekanats.size()));
            for (Dekanat d : dekanats)
            {
                System.out.println("d = " + d);
            }
        }
        result.setDekanat(dekanats.get(0));
        return result;
    }

    public static Dekanat findDekanat(Point point)
    {
        Session session = getSession();
        Dekanat dekanat = (Dekanat) session.createQuery("select d from Dekanat d where ST_contains(d.geometry, :p) = 1")
                .setParameter("p", point).uniqueResult();
        return dekanat;
    }

    public static List<Dekanat> findDekanats(Point point)
    {
        Session session = getSession();
        List<Dekanat> dekanats = session.createQuery("select d from Dekanat d where ST_contains(d.geometry, :p) = 1")
                .setParameter("p", point).list();
        return dekanats;
    }

    public static int getChurchesInDekanat(Dekanat dekanat)
    {
        if (dekanat == null)
            return 0;
        Session session = getSession();

        Long count = (Long) session.createQuery("select count(*) from Dekanat d, Church c " +
                "where ST_contains(d.geometry, c.address.geometry) = 1 and c.address.dekanat = d")
                .setCacheable(true)
                .uniqueResult();
        if (count == null)
            return 0;
        return count.intValue();
    }

    public static int getChurchesInDiocese(Diocese diocese)
    {
        if (diocese == null)
            return 0;
        Session session = getSession();

        Long count = (Long) session.createQuery("select count(*) from Diocese d, Church c " +
                "where ST_contains(d.geometry, c.address.geometry) = 1")
                .setCacheable(true)
                .uniqueResult();
        if (count == null)
            return 0;
        return count.intValue();
    }

    public static int getChurchesInMetropolies(Geometrified metropolie)
    {
        if (metropolie == null)
            return 0;
        Session session = getSession();

        Long count = (Long) session.createQuery("select count(c.id) from Metropolie m, Church c " +
                "where ST_contains(m.geometry, c.address.geometry) = 1 and m.id = :mid")
                .setParameter("mid", ((Metropolie) metropolie).getId())
                .setCacheable(true)
                .uniqueResult();
        if (count == null)
            return 0;
        return count.intValue();
    }

    public static Object findByPoint(Class<? extends Geometrified> clazz, Point point)
    {
        Session session = getSession();

        Object obj = session
                .createQuery("select g from " + clazz.getSimpleName() + " g where ST_contains(g.geometry, :p) = 1")
                .setParameter("p", point)
                .uniqueResult();
        return obj;
    }

    public static List<Geometrified> findByBBox(Class<? extends Geometrified> clazz, Geometry against)
    {

        List<Geometrified> res = new ArrayList<>();
        List<Geometrified> list = getGeometrifiedList(clazz);
        Geometry checkEnv = null;

        for (Geometrified geo : list) {
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
        return res;
    }

    public static List<Geometrified> getGeometrifiedList(Class<? extends Geometrified> clazz)
    {
        Session session = getSession();

        Query query = session
                .createQuery("select g from " + clazz.getSimpleName() + " g");
        query.setCacheable(true);
        List<Geometrified> list = query.list();
        return list;
    }

    public static List findChurchesByGeometry(Geometry geometry)
    {
        Session session = getSession();
        List res = session.createQuery(
                "select c.id as id, c.extID as extID, c.name as name, a.geometry as geometry " +
                        "from Church c, Address a " +
                        "where c.address = a and ST_CONTAINS(:geom, a.geometry) = 1")
                .setParameter("geom",  geometry)
                .list();
        return res;
    }

    /* ugly geo check, st_overlaps is not working as intended */
    public static List findDekanatsByGeometry(Geometry geometry)
    {
        Session session = getSession();
        List res = session.createQuery(
                "select d.id, d.name, d.geometry " +
                        "from Dekanat d " +
//                        "where ST_INTERSECTS(:geom, d.geometry) = 1")
                        "where ST_CROSSES(:geom, d.geometry) = 1 and ST_WITHIN(:geom, d.geometry) = 0")
                .setParameter("geom",  geometry)
                .list();
        return res;
    }

    public static List findChurchesByEnv(Envelope envelope)
    {
        Session session = getSession();
        List res = session.createQuery(
                "select c.id as id, c.extID as extID, a.geometry as geometry " +
                        "from Church c, Address a " +
                        "where c.address = a and ST_CONTAINS(:geom, a.geometry) = 1")
                .setParameter("geom",  envelope)
                .list();
        return res;
    }
}
