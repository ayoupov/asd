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
public class HeaderFragment extends SimpleTagFragment
{
    private static final String prefix = "<div class='content-main'><h1>";
    private static final String postfix = "</div>";

    private static final String TAG = "header";

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

    public HeaderFragment(String content)
    {
        this.content = content;
    }

    public HeaderFragment()
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
        return TAG.equals(tag);
    }

    @Override
    public ContentFragment newFragment(String[] options, String content)
    {
        return new HeaderFragment(content);
    }
}
