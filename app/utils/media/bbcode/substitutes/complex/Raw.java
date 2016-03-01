package utils.media.bbcode.substitutes.complex;

import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.FullwidthSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.SubstitutePriority;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:49
 */
@SubstituteAnnotation
public class Raw extends FullwidthSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[raw](.+?)\\[\\/raw]", Pattern.DOTALL);
    private String replacement =
            "$1";

    @Override
    public String getTag()
    {
        return "raw";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        return String.format("%s", unescape(matcher.group(1)));
    }

    private String unescape(String s)
    {
        return s.replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
    }

    @Override
    protected String getPrefix()
    {
        return "";
    }

    @Override
    public SubstitutePriority getPriority()
    {
        return SubstitutePriority.HIGH;
    }
}
