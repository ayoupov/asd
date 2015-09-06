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
public class Italic extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[i](.+?)\\[\\/i]", Pattern.DOTALL);
    private String replacement = "<i>$1</i>";

    @Override
    public String getTag()
    {
        return "i";
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
