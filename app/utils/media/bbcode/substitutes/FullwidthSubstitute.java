package utils.media.bbcode.substitutes;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 2:30
 */
public abstract class FullwidthSubstitute extends ComplexSubstitute
{
    @Override
    protected String getPrefix()
    {
        return "</div></div><div class='full-width'>";
    }

    @Override
    protected String getPostfix()
    {
        return "</div><div class='content-main-wrapper'><div class='content-main'>";
    }
}
