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
public class H3 extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[header3](.+?)\\[\\/header3]", Pattern.DOTALL);
    private String replacement = "<h3>$1</h3>";

    @Override
    public String getTag()
    {
        return "header3";
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
