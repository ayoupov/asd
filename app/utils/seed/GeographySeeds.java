package utils.seed;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import utils.seed.geo.ShapeProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 15:25
 */
public class GeographySeeds
{

    public static void seedGeography(String pathToShapeFile, ShapeProcessor processor) throws IOException
    {
        File shapeFile = new File(pathToShapeFile + ".shp");
        String[] split = pathToShapeFile.split("/");
        String fileName = split[split.length - 1];
        int success = 0;
        int fail = 0;

//        FileDataStore store = FileDataStoreFinder.getDataStore(shapeFile);
        ShapefileDataStore store = new ShapefileDataStore(shapeFile.toURI().toURL());
        store.setCharset(Charset.forName("UTF-8"));

        try {

            SimpleFeatureSource featureSource = store.getFeatureSource();

            FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = featureSource.getFeatures();
            FeatureIterator<SimpleFeature> fi = featureCollection.features();
            while (fi.hasNext()) {
                Feature feature = fi.next();
                SimpleFeature simpleFeature = (SimpleFeature) feature;
                if (processor.process(simpleFeature))
                    success++;
                else
                    fail++;
            }
//            store.dispose();
            fi.close();
            System.out.println(String.format("Seeded %s, success : %d, failed : %d", fileName, success, fail));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
