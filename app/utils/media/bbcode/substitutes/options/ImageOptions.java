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
public class ImageOptions
{
    protected int from = 1;
    protected int to = 8;
    protected String src;
    protected String caption;
    protected static final Pattern fromPattern = Pattern.compile("from=([1-8])");
    protected static final Pattern toPattern = Pattern.compile("to=([1-8])");
    protected static final Pattern srcPattern = Pattern.compile("src=\\{(.+?)\\}");
    protected static final Pattern heightPattern = Pattern.compile("height=(\\d+?)");


    public ImageOptions(String optionCapture)
    {
        setFrom(fromPattern.matcher(optionCapture));
        setTo(toPattern.matcher(optionCapture));
        setSrc(srcPattern.matcher(optionCapture));
    }

    public void setFrom(Matcher mFrom)
    {
        if (mFrom.find())
            from = safeInt(mFrom.group(1), 1);
    }

    public void setTo(Matcher mTo)
    {
        if (mTo.find())
            to = safeInt(mTo.group(1), 8);
    }

    public void setSrc(Matcher mSrc)
    {
        if (mSrc.find())
            src = mSrc.group(1);
    }

    public int getFrom()
    {
        return from;
    }

    public int getTo()
    {
        return to;
    }

    public String getSrc()
    {
        return src;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    public String getCaption()
    {
        return caption;
    }

}
