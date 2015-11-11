package utils.web;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.08.2015
 * Time: 2:45
 */
public class RenderUtils
{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / YY");


    public static String contentDateToString(Date date)
    {
        return date == null ? "" : sdf.format(date);
    }
}
