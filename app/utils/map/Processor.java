package utils.map;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import utils.ServerProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 23.06.2015
 * Time: 4:21
 */
public class Processor
{
    public static String dataDir = ServerProperties.getValue("asd.seed.data.folder");

    public static void main(String[] args) throws IOException, InterruptedException
    {
        Map<KMLParser.Church, KMLParser.Coordinates> coords = KMLParser.parse(args[0]);
        Map<KMLParser.Church, String> addresses = new LinkedHashMap<KMLParser.Church, String>();

        final Geocoder geocoder = new Geocoder();

        for (Map.Entry<KMLParser.Church, KMLParser.Coordinates> entry : coords.entrySet()) {
            LatLng ll = new LatLng(entry.getValue().lat + "", entry.getValue().lng + "");
            GeocoderRequestBuilder grb = new GeocoderRequestBuilder().setLocation(ll).setLanguage("pl");
            GeocoderRequest greq = grb.getGeocoderRequest();
            try {
                GeocodeResponse gresp = geocoder.geocode(greq);
                if (GeocoderStatus.OK.equals(gresp.getStatus())) {
                    List<GeocoderResult> results = gresp.getResults();
                    String address = results.get(0).getFormattedAddress();
                    addresses.put(entry.getKey(), address);
                    System.out.println("address = " + address);
                } else {
                    addresses.put(entry.getKey(), "NOT_FOUND!");
                    System.out.println("status = " + gresp.getStatus().value());
                }
            } catch (Exception e)
            {
                addresses.put(entry.getKey(), "NOT_FOUND!");
                System.out.println("http error: " + e.getMessage());
            }
            Thread.sleep(200l);
        }

        write(coords, addresses);
    }

    public static void noGeocode(String kml) throws IOException
    {
        Map<KMLParser.Church, KMLParser.Coordinates> coords = KMLParser.parse(kml);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dataDir + "churches_no_geocode.csv"), "UTF-8");
        String header = "ID|Ext_ID|Name|Lat|Lng|Address\n";
        osw.write(header);
        int i = 1;
        for (Map.Entry<KMLParser.Church, KMLParser.Coordinates> entry : coords.entrySet())
        {
            KMLParser.Church church = entry.getKey();
            KMLParser.Coordinates coordinates = entry.getValue();
            osw.write(i + "|" + church.id + "|" + church.name + "|" + coordinates.lat + "|" + coordinates.lng + "||\n");
            i++;
        }
        osw.close();
    }

    public static void write(Map<KMLParser.Church, KMLParser.Coordinates> coords, Map<KMLParser.Church, String> addresses) throws IOException
    {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dataDir + "churches.csv"), "UTF-8");
        String header = "ID|Ext_ID|Name|Lat|Lng|Address\n";
        osw.write(header);
        int i = 1;
        for (Map.Entry<KMLParser.Church, KMLParser.Coordinates> entry : coords.entrySet())
        {
            KMLParser.Church church = entry.getKey();
            KMLParser.Coordinates coordinates = entry.getValue();
            String address = addresses.get(church);
            osw.write(i + "|" + church.id + "|" + church.name + "|" + coordinates.lat + "|" + coordinates.lng + "|" + address + "\n");
            i++;
        }
        osw.close();
    }
}
