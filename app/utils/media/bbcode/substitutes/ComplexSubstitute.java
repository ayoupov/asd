package utils.media.bbcode.substitutes;

import utils.media.bbcode.BBCodeParser;

import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 2:02
 */
public abstract class ComplexSubstitute implements Substitute
{
    @Override
    public boolean isSimple()
    {
        return false;
    }

    protected abstract String getPrefix();

    protected abstract String getPostfix();

    @Override
    public String getReplacement()
    {
        return "";
    }

    @Override
    public String process(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        return getPrefix() + getProcessed(sb, matcher, state) + getPostfix();
    }

    protected abstract String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state);
}
