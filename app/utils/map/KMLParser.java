package utils.map;

import de.micromata.opengis.kml.v_2_2_0.*;
import play.Logger;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 23.06.2015
 * Time: 3:32
 */
public class KMLParser
{
    static class Church
    {
        String name;
        String id;

        Church(String name, String id)
        {
            this.id = id;
            this.name = name.replace("\\n", " ");
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Church)) return false;

            Church church = (Church) o;

            if (!name.equals(church.name)) return false;
            return id.equals(church.id);

        }

        @Override
        public int hashCode()
        {
            int result = name.hashCode();
            result = 31 * result + id.hashCode();
            return result;
        }

        @Override
        public String toString()
        {
            return "Church{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    static class Coordinates
    {
        double lat, lng;

        Coordinates(double lat, double lng)
        {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public String toString()
        {
            return "Coordinates{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }

    static Map<Church, Coordinates> parse(String path) throws UnsupportedEncodingException
    {
        Map<Church, Coordinates> res = new LinkedHashMap<Church, Coordinates>();

//        final Kml kml = Kml.unmarshal(readInString(new File(path)));
        final Kml kml = Kml.unmarshal(new File(path));
        final Document document = (Document) kml.getFeature();
        List<Feature> features = document.getFeature(); // got folders here
        int noids = 0, badgeom = 0;
        for (Feature f : features) {
            List<Feature> churches = ((Folder) f).getFeature(); // churches under f folder
            for (Feature c : churches) {
                Placemark churchPlacemark = (Placemark) c;
                String id = churchPlacemark.getDescription();
                if (id == null) {
                    id = "";
//                    System.out.println(churchPlacemark.getName() + " has no id!");
                    noids++;
                } else
                    id = new String(id.getBytes(), "UTF-8");
                String name = new String(churchPlacemark.getName().getBytes(), "UTF-8");
                name = name.replaceAll("\n", "");
                Church church = new Church(name, id);
                boolean good;
                Geometry geometry = churchPlacemark.getGeometry();
                good = geometry instanceof Point;
                if (good) {
                    Point churchPoint = (Point) (geometry);
                    List<Coordinate> coords = churchPoint.getCoordinates();
                    double lat = coords.get(0).getLatitude();
                    double lng = coords.get(0).getLongitude();
                    res.put(church, new Coordinates(lat, lng));
                } else
                    badgeom++;
            }
        }
        Logger.info(noids + " features have no ids!");
        Logger.info(badgeom + " features have bad geometry!");
        return res;
    }

    public static void main(String[] args) throws IOException
    {
        Map<Church, Coordinates> map = parse(args[0]);
//        System.out.println("map = " + map);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("c:\\temp\\map.res"), "UTF-8");
        osw.write(map.toString());
        osw.close();
    }

}
