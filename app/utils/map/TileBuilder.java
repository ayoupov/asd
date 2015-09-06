package utils.map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import json.geojson.FeatureCollection;
import models.address.Geometrified;
import models.internal.GeographyManager;
import org.geotools.feature.LenientFeatureFactoryImpl;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.Feature;
import utils.ServerProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.07.2015
 * Time: 16:27
 */
public class TileBuilder
{
    public static final String MODELS_ADDRESS = "models.address.";
    public static final String TILES_MAPPING_PREF = "tiles.mapping.";
    public static final int START_ZOOM = 0;
    public static final int MAX_ZOOM = 20;
    public static final String TILES_CONFIG_PREF = "tiles.config.";
    public static final String TILES_CONFIG_NSWE_EXTENT = TILES_CONFIG_PREF + "nswe.extent";
    private static HashMap<String, Class<? extends Geometrified>> classes = new HashMap<>();
    private static HashMap<String, Integer> zooms = new HashMap<>();
    private static String dataDir = ServerProperties.getValue("asd.seed.data.folder");

    public static void buildTiles(String whereTo) throws ClassNotFoundException
    {
        Properties props = ServerProperties.getProperties();
        Boolean rewrite = ServerProperties.getValue("tiles.config.rewrite") != null;
        String extent = ServerProperties.getValue(TILES_CONFIG_NSWE_EXTENT);
        BoundingBox featureBox = new BoundingBox(extent);
        System.out.println("featureBox = " + featureBox);
        for (String propName : props.stringPropertyNames()) {
            if (propName.startsWith(TILES_MAPPING_PREF)) {
                String key = propName.substring(TILES_MAPPING_PREF.length());
                classes.put(key, (Class<? extends Geometrified>) Class.forName(MODELS_ADDRESS + props.getProperty(propName)));
                String zoomKey = TILES_CONFIG_PREF + key + ".start.zoom";
                zooms.put(key, ServerProperties.getIntValue(zoomKey, 10));
            }
        }
        System.out.println("zooms = " + zooms);
        for (int zoom = START_ZOOM; zoom < MAX_ZOOM; zoom++) {
            for (Map.Entry entry : zooms.entrySet()) {
                if ((Integer) entry.getValue() <= zoom) {
                    String layer = (String) entry.getKey();
                    Class clazz = classes.get(layer);
                    int minX = featureBox.minX(zoom);
                    int maxX = featureBox.maxX(zoom);
                    int minY = featureBox.minY(zoom);
                    int maxY = featureBox.maxY(zoom);
                    System.out.println(String.format("working with %s[%d]: [%d-%d,%d-%d]", layer, zoom, minX, maxX, minY, maxY));
                    for (int tileX = minX; tileX <= maxX; tileX++) {
                        for (int tileY = minY; tileY <= maxY; tileY++) {

                            String path = whereTo + String.format("/%s/%d/%d", layer, zoom, tileX);
                            String fullPath = path + "/" + tileY + ".geojson";
                            if (!rewrite && new File(fullPath).exists()) {
                                System.out.println(fullPath + " already here.");
                                continue;
                            }
                            BoundingBox bb = tile2boundingBox(tileX, tileY, zoom);
                            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
                            Envelope envelope = new Envelope(bb.east, bb.west, bb.north, bb.south);
                            Geometry g = gf.toGeometry(envelope);
//                            Geometry g = EnvelopeAdapter.toPolygon(envelope, 3857);
//                            System.out.println("going to extract " + layer +" from: " + g);
//                            CoordinateReferenceSystem sourceCRS = null, targetCRS = null;
//                            Geometry targetGeometry = null;
//                            try {
//                                sourceCRS = CRS.decode("EPSG:4326", true);
//                                targetCRS = CRS.decode("EPSG:3857");
//                                MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
//                                targetGeometry = JTS.transform(g, transform);
////                                System.out.println("checking against: " + targetGeometry);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

//                            Envelope e = org.geotools.referencing.CRS.transform(envelope, crs);
//                            Geometry check = gf.toGeometry(e);
//                            System.out.println("with internal: " + check.getEnvelopeInternal());
//                            List<Geometrified> features = GeographyManager.findByBBox(clazz, g);
                            List<Geometrified> features = GeographyManager.findByBBox(clazz, g);
                            if (features != null && features.size() > 0) {
                                File file = new File(path);
                                file.mkdirs();
                                System.out.println("adding " + features.size() + " features");
                                GeometryJSON geojson = new GeometryJSON();
                                FeatureJSON featureJSON = new FeatureJSON(geojson);
                                try (FileOutputStream fos = new FileOutputStream(fullPath);) {
                                    FeatureCollection fc = new FeatureCollection();
                                    for (Geometrified geometrified : features) {
                                        Geometry geometry = geometrified.getGeometry();
                                        geojson.write(geometry, fos);
                                    }
//                                    featureJSON.writeFeatureCollection(collection, fos);
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }


        // 2. convert z,x,y into latlon
        // 3. extract all features, covered by latlon checking zoom level
        // 4. serialize

    }

    private static double long2tile(double lon, int zoom)
    {
        return (Math.floor((lon + 180) / 360 * Math.pow(2, zoom)));
    }

    private static double lat2tile(double lat, int zoom)
    {
        return (Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1 / Math.cos(lat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, zoom)));
    }

    static double tile2lon(int x, int z)
    {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(int y, int z)
    {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    public static class BoundingBox
    {
        public double north;
        public double south;
        public double west;
        public double east;

        @Override
        public String toString()
        {
            return "BoundingBox{" +
                    "north=" + north +
                    ", south=" + south +
                    ", west=" + west +
                    ", east=" + east +
                    '}';
        }

        public BoundingBox(String extent)
        {
            String[] split = extent.split(",");
            north = Double.parseDouble(split[0]);
            south = Double.parseDouble(split[1]);
            west = Double.parseDouble(split[2]);
            east = Double.parseDouble(split[3]);
        }

        public BoundingBox()
        {

        }

        public int minX(int zoom)
        {
            return (int) Math.round(long2tile(west, zoom));
        }

        public int maxX(int zoom)
        {
            return (int) Math.round(long2tile(east, zoom));
        }

        public int minY(int zoom)
        {
            return (int) Math.round(lat2tile(south, zoom));
        }

        public int maxY(int zoom)
        {
            return (int) Math.round(lat2tile(north, zoom));
        }
    }

    static BoundingBox tile2boundingBox(final int x, final int y, final int zoom)
    {
        BoundingBox bb = new BoundingBox();
        bb.north = Math.abs(tile2lat(y, zoom));
        bb.south = Math.abs(tile2lat(y + 1, zoom));
        bb.west = Math.abs(tile2lon(x, zoom));
        bb.east = Math.abs(tile2lon(x + 1, zoom));
        return bb;
    }

    public static void main(String[] args) throws ClassNotFoundException
    {
        buildTiles(dataDir + "/assets/tiles");
    }

}
