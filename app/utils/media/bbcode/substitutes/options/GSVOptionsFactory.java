package utils.media.bbcode.substitutes.options;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 4:54
 */
public class GSVOptionsFactory extends ImageOptionsFactory
{

    public static GSVOptions getOptions(String optionCapture)
    {
        return new GSVOptions(optionCapture);
    }
}
