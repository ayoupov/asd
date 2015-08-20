package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Point;
import models.Church;
import models.address.Dekanat;
import models.address.Diocese;
import models.address.Geometrified;
import models.address.Metropolie;
import models.internal.ContentManager;
import models.internal.GeographyManager;
import play.libs.Json;
import play.mvc.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 18.08.2015
 * Time: 15:23
 */
public class Stats extends Controller
{
    public static Result count()
    {
        Map<Dekanat, Integer> deks = new HashMap<>();
        Map<Diocese, Integer> dios = new HashMap<>();
        Map<Metropolie, Integer> metros = new HashMap<>();
        beginTransaction();
        ObjectNode res = Json.newObject();
        List<Church> churches = ContentManager.getChurches();
        for (Church church : churches)
        {
            System.out.println("church = " + church);
            Dekanat dek = church.address.getDekanat();
            Integer dekCount = deks.get(dek);
            if (dekCount == null)
                dekCount = 0;
            deks.put(dek, dekCount+1);
            Diocese dio = dek.getDiocese();
            Integer dioCount = dios.get(dio);
            if (dioCount == null)
                dioCount = 0;
            dios.put(dio, dioCount+1);
            Metropolie metro = dio.getMetropolie();
            Integer metroCount = metros.get(metro);
            if (metroCount == null)
                metroCount = 0;
            metros.put(metro, metroCount+1);
            System.out.println(" [dek = " + dek + "]");
        }
        res.put("deks", Json.toJson(deks));
        res.put("dios", Json.toJson(dios));
        res.put("metros", Json.toJson(metros));
        commitTransaction();
        return ok(res);
    }

    public static Result test()
    {
        beginTransaction();
        Church church = ContentManager.getChurch("TK-282");
        Dekanat d = GeographyManager.findDekanat((Point) church.address.getGeometry());
        System.out.println("d = " + d);
        commitTransaction();
        return ok("t");
    }
}
