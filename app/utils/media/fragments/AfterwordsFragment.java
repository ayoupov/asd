package utils.media.fragments;

import org.apache.commons.lang3.tuple.Pair;
import org.panaggelica.media.Fragment;
import org.panaggelica.media.FragmentDescription;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 17:57
 */
@Fragment
public class AfterwordsFragment extends SimpleTagFragment
{
    private static final String commonPrefix = "<div class='full-width content-grey'><div class='content-main'>";
    private static final String simplePrefix = commonPrefix +
            "<p class='content-afterwords'>";
    private static final String consultedPrefix = commonPrefix +
            "<p class='content-afterwords content-afterwords-consulted'>";
    private static final String postfix = "</p></div></div>";

    private static final String TAG = "afterwords";

    private static final String CONSULTED = "consulted";

    private String content, prefix;

    private static final ExcerptFragmentDescription description = new ExcerptFragmentDescription();

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

    public AfterwordsFragment(String[] options, String content)
    {
        if (Arrays.asList(options).contains(CONSULTED))
            prefix = consultedPrefix;
        else prefix = simplePrefix;
        this.content = content;
    }

    public AfterwordsFragment()
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
        return new AfterwordsFragment(options, content);
    }

    @FragmentDescription
    public static class ExcerptFragmentDescription extends ContentFragmentDescription
    {

        private static List<Pair<String, String>> options = new ArrayList<>();

        static {
            options.add(Pair.of("consulted", "prepends text with Konsultacje:"));
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
