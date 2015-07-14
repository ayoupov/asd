package utils.map;

import json.topojson.api.TopojsonApi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 20:41
 */
public class ShapeToTopoConverter
{
    private static final String PATH_TO_OUT = "./res/gis/topo/";
    private static final String PATH_TO_IN = "./res/gis/";

    private static final Map<String, String> FILES_TO_PROCESS = new HashMap<>();
    private static final String CRS = "epsg:2180";

    static {
        FILES_TO_PROCESS.put("dekanaty.shp", "Dekanaty");
        FILES_TO_PROCESS.put("diecezje.shp", "Diecezje");
        FILES_TO_PROCESS.put("diecezje_metropolie_cleaned.shp", "Metropolia");
    }

    public static void main(String[] args) throws IOException
    {
        for (Map.Entry<String, String> entry : FILES_TO_PROCESS.entrySet())
            convert(entry.getKey());
    }

    private static void convert(String fname) throws IOException
    {
        String name = FILES_TO_PROCESS.get(fname);
        fname = PATH_TO_IN + fname;
        // Reading a shp file and writing it as compressed topojson
        TopojsonApi.shpToTopojsonFile(fname,
                CRS,
                toTopo(fname),
                name,
                1000,
                4,
                false);
    }

    private static String toTopo(String fname)
    {
        File file = new File(fname);
        String last = file.getName();
        return PATH_TO_OUT + last.substring(0, last.indexOf(".")) + ".json";
    }
}
