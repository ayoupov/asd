package utils.seed.geo;

import play.Logger;
import utils.ServerProperties;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 14:48
 */
public class StaticRegionalDataProvider
{

    private static Map<String, Long> metropolias = new HashMap<>();
    private static Map<String, String> diecezje = new HashMap<>();
    private static Map<String, Long> dmConn = new HashMap<>();
    private static final String path = ServerProperties.getValue("asd.seed.data.folder") + "diecezje-skroty.csv";

    static
    {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        String line; int i = 0; long lastMetropoliaId = 0; String lastMetropoliaName = "";
        while ((line = br.readLine()) != null)
        {
            i++;
            if (i == 1)
                continue;
            // parse
            String[] split = line.split(",");
            long mId = 0;
            String mName = "";
            String dName = "";
            String dAbbr = "";
            // 0 -- mId, 1 -- mName
            if ("".equals(split[0])) {
                mId = lastMetropoliaId;
                mName = lastMetropoliaName;
            }
            else {
                mId = Long.parseLong(split[0]);
                mName = split[1];
                lastMetropoliaId = mId;
                lastMetropoliaName = mName;
            }
            metropolias.put(mName, mId);
            // 2 -- dName
            dName = split[2];
            // 3 -- dAbbr
            dAbbr = split[3];
            diecezje.put(dName, dAbbr);
            dmConn.put(dAbbr, mId);
        }

    }


    public static Map<String, Long> getMetropolias()
    {
        return metropolias;
    }

    public static Map<String, String> getDiecezje()
    {
        return diecezje;
    }

    public static Map<String, Long> getDmConn()
    {
        return dmConn;
    }

    public static void main(String[] args)
    {
        Logger.debug("metropolias = " + metropolias);
        Logger.debug("diecezje = " + diecezje);
        Logger.debug("dmConn = " + dmConn);
    }
}


