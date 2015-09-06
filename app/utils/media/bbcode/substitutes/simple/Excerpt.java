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
public class Excerpt extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[excerpt](.+?)\\[\\/excerpt]", Pattern.DOTALL);
    private String replacement = "<div class='content-excerpt'>$1<div class='golden content-underline'></div></div>";

    @Override
    public String getTag()
    {
        return "excerpt";
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
