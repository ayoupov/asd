package utils.media.bbcode.substitutes.options;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.DataUtils.safeInt;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 5:04
 */
public class CoubOptions extends ImageOptions
{
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    protected int height = 300;
    private String autostart = TRUE;
    private String originalSize = FALSE;
    private String startWithHD = TRUE;
    private String allowFullScreen = TRUE;
    private String muted = TRUE;
    private boolean gutter = false;
    protected static final Pattern mutedPattern = Pattern.compile("muted=(true|false)");
    protected static final Pattern autostartPattern = Pattern.compile("autostart=(true|false)");
    protected static final Pattern origsizePattern = Pattern.compile("originalsize=(true|false)");
    protected static final Pattern afsPattern = Pattern.compile("allowfullscreen=(true|false)");
    protected static final Pattern hdPattern = Pattern.compile("hd=(true|false)");

    public CoubOptions(String optionCapture)
    {
        super(optionCapture);
        this.setMuted(mutedPattern.matcher(optionCapture));
        this.setAutostart(autostartPattern.matcher(optionCapture));
        this.setStartWithHD(hdPattern.matcher(optionCapture));
        this.setOriginalSize(origsizePattern.matcher(optionCapture));
        this.setAllowFullScreen(afsPattern.matcher(optionCapture));
        this.setHeight(heightPattern.matcher(optionCapture));
        this.setGutter(gutterPattern.matcher(optionCapture));
    }

    public String getMuted()
    {
        return muted;
    }

    public String getAutostart()
    {
        return autostart;
    }

    public String getOriginalSize()
    {
        return originalSize;
    }

    public String getStartWithHD()
    {
        return startWithHD;
    }

    public String getAllowFullScreen()
    {
        return allowFullScreen;
    }

    public void setMuted(Matcher muted)
    {
        if (muted.find()) {
            this.muted = muted.group(1);
        }
    }

    public void setGutter(Matcher gutter)
    {
        if (gutter.find()) {
            this.gutter = gutter.group(1).equals("1");
        }
    }

    public void setAutostart(Matcher autostart)
    {
        if (autostart.find())
            this.autostart = autostart.group(1);
    }

    public void setOriginalSize(Matcher originalSize)
    {
        if (originalSize.find())
            this.originalSize = originalSize.group(1);
    }

    public void setAllowFullScreen(Matcher allowFullScreen)
    {
        if (allowFullScreen.find())
            this.allowFullScreen = allowFullScreen.group(1);
    }

    public void setStartWithHD(Matcher startWithHD)
    {
        if (startWithHD.find())
            this.startWithHD = startWithHD.group(1);
    }


    @Override
    public String toString()
    {
        return "CoubOptions{" +
                "height=" + height +
                ", autostart='" + autostart + '\'' +
                ", originalSize='" + originalSize + '\'' +
                ", startWithHD='" + startWithHD + '\'' +
                ", allowFullScreen='" + allowFullScreen + '\'' +
                ", muted='" + muted + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", gutter='" + gutter + '\'' +
                ", src='" + src + '\'' +
                '}';
    }

    public boolean getGutter()
    {
        return gutter;
    }
}
