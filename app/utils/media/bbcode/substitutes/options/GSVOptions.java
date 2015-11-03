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
public class GSVOptions extends ImageOptions
{
    protected int height = 500;
    protected int heading = 0;
    protected boolean gutter = false;

    protected static final Pattern headingPattern = Pattern.compile("heading=(\\d+)");


    public GSVOptions(String optionCapture)
    {
        super(optionCapture);
        this.setHeight(heightPattern.matcher(optionCapture));
        this.setHeading(headingPattern.matcher(optionCapture));
        this.setGutter(gutterPattern.matcher(optionCapture));
    }

    public int getHeight()
    {
        return height;
    }

    public int getHeading()
    {
        return heading;
    }

    @Override
    public String getSrc()
    {
        String src =  super.getSrc();
        return src.replace(",", "%2C");
    }

    public void setHeading(Matcher heading)
    {
        if (heading.find())
            this.heading = safeInt(heading.group(1), 0);
    }

    public void setHeight(Matcher height)
    {
        if (height.find())
            this.height = safeInt(height.group(1), 500);
    }

    public void setGutter(Matcher gutter)
    {
        if (gutter.find()) {
            this.gutter = gutter.group(1).equals("1");
        }
    }

    public boolean getGutter()
    {
        return gutter;
    }

}
