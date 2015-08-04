package models.internal.search;

import models.Church;
import models.MediaContent;
import models.MediaContentType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 23:34
 */
public class SearchManager
{

    public static void reindex() throws InterruptedException
    {
        Session session = getSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        fullTextSession.createIndexer().startAndWait();
        System.out.println(fullTextSession.getSearchFactory().getStatistics().indexedEntitiesCount().toString());
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException
    {
        reindex();
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        String searchedFor = "meczennika";
        Session session = getSession();
        Transaction tr = session.beginTransaction();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                .buildQueryBuilder()
                .forEntity( Church.class )
                .get();
        Query luceneQuery = queryBuilder.keyword().onFields("name", "address.unfolded").matching(searchedFor).createQuery();
        List result = fullTextSession.createFullTextQuery(luceneQuery).list();
        for (Object o : result) {
            String pysch = "res: " + o;
            out.println(pysch);
        }
        out.println("total: " + result.size());
        tr.commit();
    }

    public static List<Church> searchChurches(String q)
    {
        Session session = getSession();
        Transaction tr = session.beginTransaction();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                .buildQueryBuilder()
                .forEntity( Church.class )
                .get();
        Query luceneQuery = queryBuilder.keyword().onFields("name", "address.unfolded").matching(q).createQuery();

        FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery);
        query.setMaxResults(10);
        List<Church> result = query.list();
        tr.commit();
        return result;
    }

    // todo: return highlighted excerpts!
    // todo: implement filtering (@see: http://docs.jboss.org/hibernate/search/3.3/reference/en-US/html/search-query.html#d0e4463)
    public static List<MediaContent> searchMediaContent(String q, MediaContentType type)
    {
        Session session = getSession();
        Transaction tr = session.beginTransaction();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
                .buildQueryBuilder()
                .forEntity( MediaContent.class )
                .get();
        Query luceneQuery = queryBuilder.bool()
                .must(queryBuilder.keyword().onField("contentType").matching(type).createQuery())
                .must(queryBuilder.keyword().onFields("text", "lead", "caption").matching(q).createQuery())
                .createQuery();
        FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery);
        query.setMaxResults(10);
        List<MediaContent> result = query.list();
        tr.commit();
        return result;
    }
}
