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
public class Paragraph extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[p](.+?)\\[\\/p]", Pattern.DOTALL);
    private String replacement = "<p>$1</p>";

    @Override
    public String getTag()
    {
        return "p";
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
