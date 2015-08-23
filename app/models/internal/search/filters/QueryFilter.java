package models.internal.search.filters;

import play.mvc.Http;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.08.2015
 * Time: 13:15
 */
public class QueryFilter
{
    protected String nameFilter;

    protected int maxResults, page, pageSize = 20;

    public QueryFilter(Http.Request request)
    {
        apply(request);
    }

    private int safeInt(String s, int def)
    {
        int res;
        try {
            res = Integer.parseInt(s);
        } catch (Exception e) {
            res = def;
        }
        return res;
    }

    public String getNameFilter()
    {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter)
    {
        this.nameFilter = nameFilter;
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }

    public int getPage()
    {
        return page;
    }

    public void setPage(int page)
    {
        this.page = page;
    }

    @Override
    public String toString()
    {
        return "QueryFilter{" +
                "nameFilter='" + nameFilter + '\'' +
                ", maxResults=" + maxResults +
                ", page=" + page +
                '}';
    }

    public void apply(Http.Request request)
    {
        page = safeInt(request.getQueryString("page"), 0);
        maxResults = safeInt(request.getQueryString("max"), pageSize);
        nameFilter = request.getQueryString("like");
        if (nameFilter == null)
            nameFilter = "";
    }
}
