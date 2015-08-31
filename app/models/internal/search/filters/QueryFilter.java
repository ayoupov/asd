package models.internal.search.filters;

import play.mvc.Http;

import static utils.DataUtils.safeInt;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.08.2015
 * Time: 13:15
 */
public class QueryFilter
{
    protected String nameFilter = "";

    protected int maxResults = 20, page = 0;
    protected long totalResults = 0;
    private long totalPages = 0;

    public QueryFilter(Http.Request request)
    {
        apply(request, null);
    }

    public QueryFilter(Http.Request request, String entity)
    {
        apply(request, entity);
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

    public void apply(Http.Request request, String entityName)
    {
        boolean unnamed = (entityName == null || "".equals(entityName));

        String prefix = unnamed ? "" : (entityName + "_");
        page = safeInt(request.getQueryString(prefix + "page"), page);
        maxResults = safeInt(request.getQueryString(prefix + "max"), maxResults);
        String likeString = request.getQueryString(prefix + "like");
        if (likeString != null)
            nameFilter = likeString;
    }

    public void setTotalResults(long totalResults)
    {
        this.totalResults = totalResults;
        this.totalPages = totalResults / maxResults;
    }

    public long getTotalResults()
    {
        return totalResults;
    }

    public long getTotalPages()
    {
        return totalPages;
    }

    public void setTotalPages(long totalPages)
    {
        this.totalPages = totalPages;
    }
}
