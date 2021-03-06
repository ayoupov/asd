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
public class BibLinkRev extends AbstractSimpleSubstitute
{
    private static Pattern pattern = Pattern.compile("\\[biblink=reverse](.+?)\\[\\/biblink]");
    private String replacement = "<a id='biblink_rev_$1' href='#biblink_fwd_$1' class='biblink biblink-back-ref'><sup>$1</sup></a>";

    @Override
    public String getTag()
    {
        return "biblinkrev";
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
