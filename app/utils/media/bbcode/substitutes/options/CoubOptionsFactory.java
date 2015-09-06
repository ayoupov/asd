package utils.media.bbcode.substitutes.options;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 4:54
 */
public class CoubOptionsFactory extends ImageOptionsFactory
{
    public static CoubOptions getOptions(String optionCapture)
    {
        return new CoubOptions(optionCapture);
    }
}
