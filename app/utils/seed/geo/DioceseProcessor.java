package utils.seed.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import models.address.Diocese;
import models.address.Metropolie;
import org.opengis.feature.simple.SimpleFeature;
import utils.HibernateUtils;
import utils.lang.PolishSupport;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 20:28
 */
public class DioceseProcessor implements ShapeProcessor
{
    @Override
    public boolean process(SimpleFeature feature)
    {
        Map<String, String> names = StaticRegionalDataProvider.getDiecezje();
        Map<String, Long> connection = StaticRegionalDataProvider.getDmConn();
        Geometry geometry = ((Geometry) (feature).getDefaultGeometry());
        String nameFromFeature = feature.getAttribute("jpt_nazwa_").toString();
        Long idFromShape = (Long) feature.getAttribute("id");
        boolean res = false;
        for (Map.Entry<String, String> entry : names.entrySet()) {
            String name = entry.getKey();
            if (PolishSupport.similar(name, nameFromFeature)) {
                String abbr = entry.getValue();
                Point centroid = ShapeRegionalDataProvider.getDioceseCentroid(idFromShape);

                Metropolie metropolie = (Metropolie) HibernateUtils.load(Metropolie.class, connection.get(abbr));
                if (metropolie == null) {
                    System.out.println("Metropolie " + connection.get(abbr) + " not found!");
                    break;
                }
                Diocese diocese = new Diocese(abbr, name, geometry, centroid, metropolie);
                if (name.startsWith("Archi")) diocese.setArchidiocese(true);
                // store to db and change res accordingly
                HibernateUtils.saveOrUpdate(diocese);
                res = true;
            }
        }
        if (!res)
            System.out.println(String.format("Can't find match for %s diocese!", nameFromFeature));
        return res;
    }

    public DioceseProcessor()
    {
    }
}
