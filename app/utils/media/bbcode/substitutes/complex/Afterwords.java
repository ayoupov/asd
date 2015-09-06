package utils.media.bbcode.substitutes.complex;

import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.FullwidthSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:49
 */
@SubstituteAnnotation
public class Afterwords extends FullwidthSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[afterwords](.+?)\\[\\/afterwords]", Pattern.DOTALL);
    private String replacement =
            "<div class='content-afterwords'>$1</div>";

    @Override
    public String getTag()
    {
        return "afterwords";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        return String.format("<div class='content-afterwords-wrapper'><div class='content-afterwords'>%s</div></div>", matcher.group(1));
    }

    @Override
    protected String getPrefix()
    {
        return "</div></div><div class='full-width content-grey'>";
    }

}
