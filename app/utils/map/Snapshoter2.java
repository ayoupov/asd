package utils.map;

import play.Logger;
import utils.ServerProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static utils.map.BadIdsSieve.beautify;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 02.08.2015
 * Time: 20:46
 */
public class Snapshoter2
{

    private static final String dataDir = ServerProperties.getValue("asd.seed.data.folder");
    private static final String snapshotPath = ServerProperties.getValue("asd.snapshoter.path");

    public static void snap2(String csv, String whereTo) throws IOException, InterruptedException
    {
        Set<String> ids = new HashSet<>();
        Map<KMLParser.Church, KMLParser.Coordinates> map = getStatic(csv);
        String key = ServerProperties.getValue("google.api.key");
        for (Map.Entry<KMLParser.Church, KMLParser.Coordinates> entry : map.entrySet()) {
            KMLParser.Church church = entry.getKey();
            KMLParser.Coordinates coords = entry.getValue();
            String id = beautify(church.id);
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
                    if (img != null) {
                        ImageIO.write(img, "png", outputfile);
                        Thread.sleep(200l);
                        if (!logOnce) {
                            Logger.info("Processed " + id);
                            logOnce = true;
                        }
                    } else
                    {
                        Logger.info("Couldn't get file: " + url);
                    }
                }
            }
        }
        Logger.warn("Some ids left: " + ids);

    }

    private static Map<KMLParser.Church, KMLParser.Coordinates> getStatic(String csv) throws IOException
    {
        Map<KMLParser.Church, KMLParser.Coordinates> res = new LinkedHashMap<>();
        InputStream inputStream = new FileInputStream(csv);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(";");
            KMLParser.Church church = new KMLParser.Church("" + lineNum, split[0]);
            double lat = Double.parseDouble(split[1]);
            double lng = Double.parseDouble(split[2]);
            KMLParser.Coordinates coordinates = new KMLParser.Coordinates(lat, lng);
            res.put(church, coordinates);
            lineNum++;
        }
        br.close();
        return res;
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        snap2(dataDir + "snapshot_churches.csv", snapshotPath);
    }
}
