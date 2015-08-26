package utils.media;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 16:31
 */
public abstract class ContentFragment
{
    protected String[] options;

    public abstract String render();

    public abstract ContentFragmentDescription getDescription();

    public abstract String getTag();

    public abstract boolean accepts(String tag);

    public abstract ContentFragment newFragment(String[] options, String content);

}
