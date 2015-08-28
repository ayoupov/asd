package utils.map;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 29.06.2015
 * Time: 21:04
 */
public class AdditiveProcessor
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
        Map<KMLParser.Church, KMLParser.Coordinates> coords = new LinkedHashMap<KMLParser.Church, KMLParser.Coordinates>();
        String line;
        while ((line = reader.readLine()) != null) {
            // skip header
            String[] row = line.split("\\|");
            if ("ID".equals(row[0]))
                continue;
            String address = row[5];
            if (!"NOT_FOUND!".equals(address))
                continue;
            KMLParser.Church church = new KMLParser.Church(row[2], row[1]);
            coords.put(church, new KMLParser.Coordinates(Double.parseDouble(row[3]), Double.parseDouble(row[4])));
        }
        reader.close();

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
            } catch (Exception e) {
                addresses.put(entry.getKey(), "NOT_FOUND!");
                System.out.println("http error: " + e.getMessage());
            }
            Thread.sleep(200l);
        }

        Processor.write(coords, addresses);

    }
}
