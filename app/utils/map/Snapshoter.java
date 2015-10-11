package utils.map;

import play.Logger;
import utils.ServerProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        snapSome(kml, whereTo, null);
    }

    public static void snapSome(String kml, String whereTo, String what) throws IOException, InterruptedException
    {
        Set<String> ids = new HashSet<>();
        boolean fullParse = what == null;
        if (!fullParse) {
            BufferedReader br = new BufferedReader(new FileReader(what));
            String line;
            while ((line = br.readLine()) != null) {
                if (!ids.add(line.trim()))
                    Logger.warn(line + " already was here!");;
            }
            br.close();
        }
        Map<KMLParser.Church, KMLParser.Coordinates> map = KMLParser.parse(kml);
        String key = ServerProperties.getValue("google.api.key");
        for (Map.Entry<KMLParser.Church, KMLParser.Coordinates> entry : map.entrySet()) {
            KMLParser.Church church = entry.getKey();
            KMLParser.Coordinates coords = entry.getValue();
            String id = beautify(church.id);
            if (fullParse || ids.contains(id)) {
                if (ids.contains(id))
                    ids.remove(id);
                Double lat = coords.lat;
                Double lon = coords.lng;
                boolean logOnce = false;
                for (int zoom = 17; zoom <= 19; zoom++) {
                    File outputfile = new File(whereTo);
                    outputfile = new File(outputfile, id + "_" + zoom + ".png");
                    if (!outputfile.exists()) {
                        String url = String.format(
                                "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=%d&" +
                                        "size=640x640&maptype=satellite&key=%s", lat, lon, zoom, key);
                        BufferedImage img = ImageIO.read(new URL(url));
                        ImageIO.write(img, "png", outputfile);
                        Thread.sleep(200l);
                        if (!logOnce)
                        {
                            Logger.info("Processed " + id);
                            logOnce = true;
                        }
                    }
                }
            }
        }
        Logger.warn("Some ids left: " + ids);

    }
}
