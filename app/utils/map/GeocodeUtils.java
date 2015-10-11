package utils.map;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.*;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.07.2015
 * Time: 18:34
 */
public class GeocodeUtils
{

    final static Geocoder geocoder = new Geocoder();


    public static GeocodeResponse geocode(Point point)
    {
        LatLng ll = new LatLng(point.getCoordinate().y + "", point.getCoordinate().x + "");
        try {
            GeocoderRequestBuilder grb = new GeocoderRequestBuilder().setLocation(ll).setLanguage("pl");
            GeocoderRequest greq = grb.getGeocoderRequest();
            return geocoder.geocode(greq);
        } catch (Exception e)
        {
            return null;
        }
    }


    public static String getAddress(Point point)
    {
        LatLng ll = new LatLng(point.getCoordinate().y + "", point.getCoordinate().x + "");
        String address = null;
        try {
            GeocoderRequestBuilder grb = new GeocoderRequestBuilder().setLocation(ll).setLanguage("pl");
            GeocoderRequest greq = grb.getGeocoderRequest();
            GeocodeResponse gresp = geocoder.geocode(greq);
            if (GeocoderStatus.OK.equals(gresp.getStatus())) {
                List<GeocoderResult> results = gresp.getResults();
                address = results.get(0).getFormattedAddress();
            }
        } catch (IOException ioe)
        {
            // todo: better logging
            ioe.printStackTrace();
        }
        return address;
    }
}
