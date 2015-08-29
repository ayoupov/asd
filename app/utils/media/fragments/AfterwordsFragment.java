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
public class AfterwordsFragment extends ContentFragment
{
    private static final String commonPrefix = "<div class='full-width content-grey'><div class='content-main'>";
    private static final String afterwordsPrefix = "<div class='content-afterwords content-afterwords-style'>";
    private static final String simplePrefix = commonPrefix +
            afterwordsPrefix;
    private static final String postfix = "</div></div></div>";

    private static final String prependPrefix = commonPrefix +
            "<div class='content-afterwords-wrapper'><span class='content-afterwords-content'>";
    private static final String prependSuffix = "</span><div class='content-afterwords-style'>";
    private static final String prependPostfix = "</div></div>";

    private static final String TAG = "afterwords";

    private static final String PREPEND = "prepend";
    private boolean prepended = false;

    private String content, prependContent = null, prefix;

    private static final ExcerptFragmentDescription description = new ExcerptFragmentDescription();

    protected String getContent()
    {
        return content;
    }

    protected String getPrefix()
    {
        return prefix;
    }

    protected String getPostfix()
    {
        return postfix;
    }

    public AfterwordsFragment(String[] options, String content)
    {
        prepended = (Arrays.asList(options).contains(PREPEND));
        this.content = content;
        if (prepended) {
            int idx = content.indexOf(":");
            if (idx >= 0) {
                this.prependContent = content.substring(0, idx + 1);
                this.content = content.substring(idx + 1);
            }
            else
                prependContent = "";
        }
    }

    public AfterwordsFragment()
    {
    }

    @Override
    public String render()
    {
        if (prepended)
            return prependPrefix + prependContent + prependSuffix + content + prependPostfix;
        return simplePrefix + content + postfix;
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
        return TAG.equalsIgnoreCase(tag);
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
            options.add(Pair.of("prepend", "prepends main text with text before ':'"));
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
