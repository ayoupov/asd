package models.internal.search.filters;

import play.mvc.Http;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.08.2015
 * Time: 13:15
 */
public class ImageFilter extends QueryFilter
{

    protected String churchFilter;

    public ImageFilter(Http.Request request)
    {
        super(request, "images");
        applyExtra(request);
    }

    public String getChurchFilter()
    {
        return churchFilter;
    }

    public void setChurchFilter(String churchFilter)
    {
        this.churchFilter = churchFilter;
    }

    public void applyExtra(Http.Request request)
    {
        String churchString = request.getQueryString("images_church");
        if (churchString != null)
            churchFilter = churchString;
    }
}
