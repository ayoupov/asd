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
public class H2 extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[header2](.+?)\\[\\/header2]", Pattern.DOTALL);
    private String replacement = "<h2>$1</h2>";

    @Override
    public String getTag()
    {
        return "header2";
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
