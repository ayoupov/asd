package utils.media;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 16:44
 */
public abstract class ContentFragmentDescription
{
    public abstract String getTag();
    public abstract List<Pair<String, String>> getOptions();
}
