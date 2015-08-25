package utils;

import utils.serialize.OnlyDateConverter;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.08.2015
 * Time: 16:50
 */
public class DataUtils
{
    public static int safeInt(String s, int def)
    {
        int res;
        try {
            res = Integer.parseInt(s);
        } catch (Exception e) {
            res = def;
        }
        return res;
    }

    public static long safeLong(String s, long def)
    {
        long res;
        try {
            res = Long.parseLong(s);
        } catch (Exception e) {
            res = def;
        }
        return res;
    }

    public static Date dateFromReqString(String dt)
    {
        Date date = null;
        try {
            date = OnlyDateConverter.sdf.parse(dt);
        } catch (Exception e) {
        }
        return date;
    }
}
