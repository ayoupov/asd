package utils.media.bbcode.substitutes.complex;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 10.09.2015
 * Time: 19:55
 */
public class GridUtil
{
    protected static final int colWidth = 100;
    protected static final int colGutterWidth = 20;

    public static int gridWidth(int from, int to, boolean gutter)
    {
        if (gutter)
            return (to - from + 1) * (colWidth + colGutterWidth) + colGutterWidth;
        else
            return (to - from + 1) * (colWidth + colGutterWidth) - colGutterWidth;
    }

    public static int leftMargin(int from, int to, boolean gutter)
    {
        if (gutter)
            return (from - 2) * (colWidth + colGutterWidth) - colGutterWidth;
        else
            return (from - 2) * (colWidth + colGutterWidth);
    }
}
