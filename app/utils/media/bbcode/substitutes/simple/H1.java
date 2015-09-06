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
public class H1 extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[header](.+?)\\[\\/header]", Pattern.DOTALL);
    private String replacement = "<h1>$1</h1>";

    @Override
    public String getTag()
    {
        return "header";
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
