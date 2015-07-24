package utils.map;

import com.vividsolutions.jts.geom.Geometry;
import models.address.Geometrified;
import models.internal.GeographyManager;
import org.geotools.geojson.geom.GeometryJSON;
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
    public static final int START_ZOOM = 6;
    public static final int MAX_ZOOM = 20;
    public static final String TILES_CONFIG_PREF = "tiles.config.";
    public static final String TILES_CONFIG_NSWE_EXTENT = TILES_CONFIG_PREF + "nswe.extent";
    private static HashMap<String, Class<? extends Geometrified>> classes = new HashMap<>();
    private static HashMap<String, Integer> zooms = new HashMap<>();

    public static void buildTiles(String whereTo) throws ClassNotFoundException
    {
        Properties props = ServerProperties.getProperties();
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
                            File file = new File(path);
                            file.mkdirs();
                            path = path + "/" + tileY + ".geojson";
                            BoundingBox bb = tile2boundingBox(tileX, tileY, zoom);
                            System.out.println("going to extract entities to " + path + " from " + bb);
                            List<Geometrified> features = GeographyManager.findByBBox(clazz, bb);
                            if (features != null && features.size() > 0) {
                                System.out.println("adding " + features.size() + " features");
                                GeometryJSON geojson = new GeometryJSON();
                                try (FileOutputStream fos = new FileOutputStream(path);) {
                                    for (Geometrified geometrified : features) {
                                        Geometry geometry = geometrified.getGeometry();
                                        geojson.write(geometry, fos);
                                    }
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
            return (int) Math.round(lat2tile(east, zoom));
        }

        public int maxX(int zoom)
        {
            return (int) Math.round(lat2tile(west, zoom));
        }

        public int minY(int zoom)
        {
            return (int) Math.round(long2tile(north, zoom));
        }

        public int maxY(int zoom)
        {
            return (int) Math.round(long2tile(south, zoom));
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
        buildTiles("d:/prog/asd/app/assets/tiles");
    }

}
