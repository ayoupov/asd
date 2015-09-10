package utils.media.bbcode.substitutes.simple;

import utils.media.bbcode.substitutes.AbstractSimpleSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:49
 */
@SubstituteAnnotation
public class Gap extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[gap=(\\d+)\\s*?\\/*?]");
    private String replacement = "<div class='gap' style='margin-bottom: $1px'></div>";

    @Override
    public String getTag()
    {
        return "gap";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    public String getReplacement()
    {
        return replacement;
    }
}
