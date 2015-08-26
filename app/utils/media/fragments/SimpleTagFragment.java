package utils.media.fragments;

import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 20:34
 */
public abstract class SimpleTagFragment extends ContentFragment
{

    @Override
    public String render()
    {
        return getPrefix() + getContent() + getPostfix();
    }

    protected abstract String getContent();

    protected abstract String getPrefix();

    protected abstract String getPostfix();

}
