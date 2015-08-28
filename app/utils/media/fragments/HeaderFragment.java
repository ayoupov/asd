package utils.media.fragments;

import org.apache.commons.lang3.tuple.Pair;
import org.panaggelica.media.Fragment;
import org.panaggelica.media.FragmentDescription;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

import java.util.ArrayList;
import java.util.List;

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

    private static final HeaderFragmentDescription description = new HeaderFragmentDescription();

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
        return description;
    }

    @Override
    public String getTag()
    {
        return TAG;
    }

    @Override
    public ContentFragment newFragment(String[] options, String content)
    {
        return new HeaderFragment(content);
    }

    @FragmentDescription
    public static class HeaderFragmentDescription extends ContentFragmentDescription
    {

        private static List<Pair<String, String>> options = new ArrayList<>();

        static {
        }

        @Override
        public String getTag()
        {
            return TAG;
        }

        @Override
        public List<Pair<String, String>> getOptions()
        {
            return options;
        }
    }

}
