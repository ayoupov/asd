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
public class Link extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[link=(.+?)](.+?)\\[\\/link]");
    private String replacement = "<a href='$1'>$2</a>";

    @Override
    public String getTag()
    {
        return "link";
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
