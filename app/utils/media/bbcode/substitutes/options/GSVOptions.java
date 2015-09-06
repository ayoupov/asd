package utils.media.bbcode.substitutes.options;

import java.util.regex.Matcher;

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

    public GSVOptions(String optionCapture)
    {
        super(optionCapture);
        this.setHeight(heightPattern.matcher(optionCapture));
    }

    public void setHeight(Matcher height)
    {
        if (height.find())
            this.height = safeInt(height.group(1), 300);
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
}
