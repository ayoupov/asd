package utils.media.fragments;

import org.panaggelica.media.Fragment;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 17:57
 */
@Fragment
public class UnformattedFragment extends ContentFragment
{
    private static final String prefix = "<div class='main-content><p>";
    private static final String postfix = "</p></div>";
    private static final String TAG = "";

    protected String tag = "";

    private String content;

    @Override
    public String render()
    {
        return prefix + content + postfix;
    }

    public UnformattedFragment(String content)
    {
        this.content = content;
    }

    public UnformattedFragment()
    {
    }

    @Override
    public ContentFragmentDescription getDescription()
    {
        return null;
    }

    @Override
    public String getTag()
    {
        return TAG;
    }

    @Override
    public boolean accepts(String tag)
    {
        return tag.equals(this.tag);
    }

    @Override
    public ContentFragment newFragment(String[] options, String content)
    {
        return new UnformattedFragment(content);
    }
}
