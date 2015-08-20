package models.internal.search.filters;

import play.mvc.Http;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.08.2015
 * Time: 13:15
 */
public class StoryFilter extends QueryFilter
{
    public StoryFilter(Http.Request request)
    {
        super(request);
    }
}
