package utils;

import utils.serialize.converters.DateTimeConverter;

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

    public static long safeLong(String[] ss, long def)
    {
        return safeLong(ss[0], def);
    }

    public static Boolean safeBool(String req)
    {
        Boolean res = false; // as serialization doesn't return parameter if not checked
        if ("1".equals(req) || "true".equalsIgnoreCase(req) || "on".equalsIgnoreCase(req))
            res = true;
//        if ("0".equals(req) || "false".equalsIgnoreCase(req) || "off".equalsIgnoreCase(req))
//            res = false;
        return res;
    }

    public static Boolean safeBool(String[] reqStr)
    {
        return safeBool(reqStr[0]);
    }

    public static Date dateFromReqString(String dt)
    {
        Date date = null;
        try {
            date = DateTimeConverter.sdf.parse(dt);
        } catch (Exception e) {
        }
        return date;
    }

    public static Date dateFromReqString(String[] dts)
    {
        return dateFromReqString(dts[0]);
    }
}
