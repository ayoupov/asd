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
public class Quote extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[quote](.+?)\\[\\/quote]", Pattern.DOTALL);
    private String replacement =
            "<div class='content-quote-wrapper'>" +
                    "<div class='content-quote-symbol'>,,</div>" +
                    "<div class='content-quote'>$1</div>" +
                    "</div>";

    @Override
    public String getTag()
    {
        return "quote";
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
