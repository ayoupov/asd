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
public class ExcerptFragment extends SimpleTagFragment
{
    private static final String prefix = "<div class='content-main'><div class='content-note'>";
    private static final String postfix = "<div class='golden content-underline'></div></div></div>";

    private static final String TAG = "excerpt";

    private String content;

    @Override
    protected String getContent()
    {
        return content;
    }

    @Override
    protected String getPrefix()
    {
        return prefix;
    }

    @Override
    protected String getPostfix()
    {
        return postfix;
    }

    public ExcerptFragment(String content)
    {
        this.content = content;
    }

    public ExcerptFragment()
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
    public ContentFragment newFragment(String[] options, String content)
    {
        return new ExcerptFragment(content);
    }
}
