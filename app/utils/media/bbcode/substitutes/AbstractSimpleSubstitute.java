package utils.media.bbcode.substitutes;

import utils.media.bbcode.BBCodeParser;

import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:35
 */
public abstract class AbstractSimpleSubstitute implements Substitute
{

    @Override
    public boolean isSimple()
    {
        return true;
    }

    @Override
    public String process(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        return "";
    }
}
