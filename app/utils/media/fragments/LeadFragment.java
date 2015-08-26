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
public class LeadFragment extends SimpleTagFragment
{
    private static final String prefix = "<div class='content-lead-wrapper'><div class='content-lead'>";
    private static final String postfix = "</div></div>";

    private static final String TAG = "lead";

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

    @Override
    public String render()
    {
        return prefix + content + postfix;
    }

    public LeadFragment(String content)
    {
        this.content = content;
    }

    public LeadFragment()
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
        return new LeadFragment(content);
    }
}
