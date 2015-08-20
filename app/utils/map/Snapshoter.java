package utils.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static utils.map.BadIdsSieve.beautify;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 02.08.2015
 * Time: 20:46
 */
public class Snapshoter
{
    public static void snap(String kml, String whereTo) throws IOException, InterruptedException
    {
        Map<KMLParser.Church, KMLParser.Coordinates> map = KMLParser.parse(kml);
        String key = "AIzaSyDj1KZcp0SA3-4hHhkh8aEsbRcJk2iGIXI";
        for (Map.Entry<KMLParser.Church, KMLParser.Coordinates> entry : map.entrySet()) {
            KMLParser.Church church = entry.getKey();
            KMLParser.Coordinates coords = entry.getValue();
            String id = beautify(church.id);
            Double lat =coords.lat;
            Double lon =coords.lng;
            for (int zoom = 17; zoom <= 19; zoom++) {
                String url = String.format(
                        "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=%d&" +
                                "size=640x640&maptype=satellite&key=%s", lat, lon, zoom, key);
                BufferedImage img = ImageIO.read(new URL(url));
                File outputfile = new File(whereTo);
//                outputfile.mkdirs();
                outputfile = new File(outputfile, id + "_" + zoom + ".png");
                ImageIO.write(img, "png", outputfile);
                Thread.sleep(200l);
            }
            System.out.println("Did " + id);

        }
    }
}
