package utils.media.fragments;

import org.apache.commons.lang3.tuple.Pair;
import org.panaggelica.media.Fragment;
import org.panaggelica.media.FragmentDescription;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;
import utils.media.ContentProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 17:57
 */
@Fragment
public class UnformattedFragment extends ContentFragment
{
    private static final String prefix = "<div class='content-main'><p>";
    private static final String postfix = "</p></div>";
    private static final String TAG = "";

    protected String tag = "";

    private String content;

    private static final UnformattedFragmentDescription description = new UnformattedFragmentDescription();

    @Override
    public String render()
    {
        return prefix + content + postfix;
    }

    public UnformattedFragment(String content)
    {
        this.content = content.replaceAll("\n", "</p><p>");
    }

    public UnformattedFragment()
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
    public boolean accepts(String tag)
    {
        return tag.equals(this.tag);
    }

    @Override
    public ContentFragment newFragment(String[] options, String content)
    {
        return new UnformattedFragment(content);
    }

    @FragmentDescription
    public static class UnformattedFragmentDescription extends ContentFragmentDescription
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
