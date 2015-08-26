package utils.media.fragments;

import org.panaggelica.media.Fragment;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 20:41
 */
@Fragment
public class QuoteFragment extends SimpleTagFragment
{
    private static final String prefix = "<div class='content-quote-wrapper'>" +
            "<div class='content-quote-symbol'>,,</div>" +
            "<div class='content-quote\'>";
    private static final String postfix = "</div></div>";
    public static final String TAG = "quote";

    private String content;

    public QuoteFragment(String content)
    {
        this.content = content;
    }

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
        return new QuoteFragment(content);
    }

    public QuoteFragment()
    {
    }
}
