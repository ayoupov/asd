package utils.media.fragments;

import org.panaggelica.media.Fragment;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 20:41
 */
@Fragment
public class QuoteFragment extends SimpleTagFragment
{
    private static final String commonPrefix = "<div class='content-main'><div class='content-quote-wrapper'>";
    private static final String prefix = commonPrefix +
            "<div class='content-quote-symbol'>,,</div>" +
            "<div class='content-quote'>";
    private static final String prefixSecond = commonPrefix +
            "<div class='content-quote content-quote-second'>";
    private static final String postfix = "</div></div></div>";
    public static final String TAG = "quote";

    private String content;
    private boolean second = false;
    private static final String SECOND = "second";

    public QuoteFragment(String[] options, String content)
    {
        List<String> opts = Arrays.asList(options);
        second = (opts.contains(SECOND));
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
        return second ? prefixSecond : prefix;
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
        return new QuoteFragment(options, content);
    }

    public QuoteFragment()
    {
    }
}
