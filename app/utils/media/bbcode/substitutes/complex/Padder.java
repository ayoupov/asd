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
public class Padder extends FullwidthSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[padder\\s*?\\/]", Pattern.DOTALL);
    private String replacement = "<div class='content-padder'></div>";

    @Override
    public String getTag()
    {
        return "padder";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        return replacement;
    }
}
