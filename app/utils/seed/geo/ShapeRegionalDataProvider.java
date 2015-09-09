package utils.seed.geo;

import com.vividsolutions.jts.geom.Point;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import utils.ServerProperties;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 09.09.2015
 * Time: 4:48
 */
public class ShapeRegionalDataProvider
{
    private static Map<Long, Point> diocent = new HashMap<>();
    private static Map<Long, Point> metrocent = new HashMap<>();
    private static final String diocentFile = ServerProperties.getValue("asd.seed.data.folder") + "gis/die_centroids_wgs84.shp";
    private static final String metroFile = ServerProperties.getValue("asd.seed.data.folder") + "gis/metro_centroids_wgs84.shp";

    static {
        processMap(diocent, diocentFile);
        processMap(metrocent, metroFile);
    }

    private static void processMap(Map<Long, Point> map, String file)
    {
        try {
            ShapefileDataStore store = new ShapefileDataStore(new File(file).toURI().toURL());
            store.setCharset(Charset.forName("UTF-8"));


            SimpleFeatureSource featureSource = store.getFeatureSource();

            FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = featureSource.getFeatures();
            FeatureIterator<SimpleFeature> fi = featureCollection.features();
            while (fi.hasNext()) {
                Feature feature = fi.next();
                SimpleFeature simpleFeature = (SimpleFeature) feature;
                Long id;
                Object preId = simpleFeature.getAttribute("id");
                if (preId instanceof Integer)
                    id = Long.valueOf((Integer) preId);
                else id = (Long) preId;
                map.put(id, (Point) simpleFeature.getDefaultGeometry());
            }
            fi.close();
            System.out.println("initialized map = " + map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Point getDioceseCentroid(Long idFromShape)
    {
        return diocent.get(idFromShape);
    }

    public static Point getMetropoliaCentroid(Long idFromShape)
    {
        return metrocent.get(idFromShape);
    }
}
