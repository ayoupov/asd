package models.internal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.feth.play.module.pa.user.AuthUser;
import models.Church;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.internal.search.filters.ChurchFilter;
import models.internal.search.filters.QueryFilter;
import models.internal.search.filters.UserFilter;
import models.user.User;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.spatial.GeometryType;
import org.hibernate.spatial.dialect.mysql.MySQLSpatial56Dialect;
import org.hibernate.type.StandardBasicTypes;
import play.mvc.Http;
import utils.serialize.PointConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.DataUtils.safeLong;
import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 05.08.2015
 * Time: 14:13
 */
public class ContentManager
{
    public static List<MediaContent> getByIds(String ids) throws RequestException
    {
        return getByIds(ids, false);
    }

    public static List<MediaContent> getByIds(String ids, boolean skipApproval) throws RequestException
    {
        List<MediaContent> res = new ArrayList<>();
        Session session = getSession();
        try {
            String[] split = ids.split(",");
            for (String rawId : split) {
                Long id = Long.parseLong(rawId);
                MediaContent content = (MediaContent) session.get(MediaContent.class, id);
                if (content.approvedDT != null || skipApproval)
                    res.add(content); // todo: more verbose in case of unapproved request?
            }
        } catch (Exception e) {
            throw new RequestException(e);
        }
        return res;
    }

    public static List<Pair<Long, Boolean>> getSummary(MediaContentType contentType)
    {
        return getSummary(contentType, false);
    }

    public static List<Pair<Long, Boolean>> getSummary(MediaContentType contentType, boolean skipApproval)
    {
        Session session = getSession();
        List<Pair<Long, Boolean>> res = session.createQuery(
                "select mc.id, mc.starred from MediaContent mc " +
                        "where mc.contentType = :ct and (:sa = TRUE or mc.approvedDT is not null) " +
                        "order by mc.approvedDT desc"
        )
                .setParameter("ct", contentType)
                .setParameter("sa", skipApproval)
                .setCacheable(true).list();
        return res;
    }

    public static long getChurchCount(boolean skipApproval)
    {
        Session session = getSession();
        Long res = (Long) session.createQuery(
                "select distinct count(c.extID) from Church c " +
                        "where (:sa = TRUE or c.approvedDT is not null) " +
                        "order by c.approvedDT desc "
        )
                .setParameter("sa", skipApproval)
                .setCacheable(true).uniqueResult();
        return res;
    }

    public static long getChurchCount()
    {
        return getChurchCount(false);
    }

    public static Church getChurch(String id)
    {
        return getChurch(id, false);
    }

    public static Church getChurch(String id, boolean skipApproval)
    {
        Session session = getSession();
        Church church = (Church) session.createQuery(
                "select c from Church c " +
                        "where (:sa = TRUE or c.approvedDT is not null) and " +
                        "c.extID = :id " +
                        "order by c.version desc"
        ).setParameter("sa", skipApproval).setParameter("id", id).setMaxResults(1).uniqueResult();
        return church;
    }

    public static List<Church> getChurchVersions(String id)
    {
        Session session = getSession();
        List<Church> churches = session.createQuery(
                "select c from Church c " +
                        "where c.extID = :id " +
                        "order by c.version asc"
        ).setParameter("id", id).list();
        return churches;
    }

    public static List<Object> getChurchesShort()
    {
        Session session = getSession();
        List churches = session.createQuery("select distinct c1.id, c1.address.geometry " +
                "from Church c1, Church c2 " +
                "where " +
                "c1.extID = c2.extID and " +
                "c1.version >= c2.version and " +
                "c1.approvedDT is not null " +
                "order by c1.version desc, c1.extID").list();
        return churches;
    }

    public static List<Church> getChurches()
    {
        Session session = getSession();
        List<Church> churches = session.createQuery(
                "select distinct c1 " +
                        "from Church c1, Church c2 " +
                        "where " +
                        "c1.extID = c2.extID and " +
                        "c1.version >= c2.version and " +
                        "c1.approvedDT is not null " +
                        "order by c1.version desc, c1.extID").list();
        return churches;
    }

    public static List<Church> getChurches(ChurchFilter filter)
    {
        Session session = getSession();
        Query query = session.createQuery(
                "select distinct c " +
                        "from Church c where " +
                        "c.name like :fname " +
                        "order by c.version desc, c.approvedDT")
                .setParameter("fname", "%" + filter.getNameFilter() + "%")
                .setMaxResults(filter.getMaxResults())
                .setFirstResult(filter.getPage() * filter.getMaxResults());
//        System.out.println("query = " + query + " : " + filter);
        List<Church> churches = query.list();
        return churches;
    }

    public static Long articlesByUser(User user)
    {
        return contentByUser(user, MediaContentType.Article);
    }

    public static Long storiesByUser(User user)
    {
        return contentByUser(user, MediaContentType.Story);
    }

    private static Long contentByUser(User user, MediaContentType mct)
    {
        Session session = getSession();
        Long count = (Long) session.createQuery("select count(*) from MediaContent mc " +
                "where mc.contentType = :mct and mc.addedBy = :u").
                setParameter("mct", mct).setParameter("u", user).uniqueResult();
        return count;
    }

    public static Long churchesByUser(User user)
    {
        Session session = getSession();
        Long count = (Long) session.createQuery("select count(*) from Church c " +
                "where c.addedBy = :u").setParameter("u", user).uniqueResult();
        return count;
    }

    public static List<User> getUsers(UserFilter filter)
    {
        Session session = getSession();
        List<User> users = session.createQuery(
                "select distinct u " +
                        "from User u where " +
                        "u.name like :fname " +
                        "order by u.id")
                .setParameter("fname", "%" + filter.getNameFilter() + "%")
                .setMaxResults(filter.getMaxResults())
                .setFirstResult(filter.getPage() * filter.getMaxResults())
                .list();
        return users;
    }

    public static List<MediaContent> getMediaContent(QueryFilter filter, MediaContentType mct)
    {
        Session session = getSession();
        List<MediaContent> content = session.createQuery(
                "select distinct mc " +
                        "from MediaContent mc where " +
                        "mc.title like :fname and mc.contentType = :mct " +
                        "order by mc.approvedDT")
                .setParameter("fname", "%" + filter.getNameFilter() + "%")
                .setParameter("mct", mct)
                .setMaxResults(filter.getMaxResults())
                .setFirstResult(filter.getPage() * filter.getMaxResults())
                .list();
        return content;
    }

    public static Integer getUserIssuesCount()
    {
        Session session = getSession();
        Long res = (Long) session.createQuery(
                "select count(*) " +
                        "from User u ")
                .uniqueResult();
        return res.intValue();
    }

    public static Integer getArticleIssuesCount()
    {
        Session session = getSession();
        Long res = (Long) session.createQuery(
                "select count(*) " +
                        "from MediaContent mc where mc.contentType = :mct and mc.approvedDT is null ")
                .setParameter("mct", MediaContentType.Article)
                .uniqueResult();
        return res.intValue();
    }

    public static Integer getStoryIssuesCount()
    {
        Session session = getSession();
        Long res = (Long) session.createQuery(
                "select count(*) " +
                        "from MediaContent mc where mc.contentType = :mct and mc.approvedDT is null ")
                .setParameter("mct", MediaContentType.Story)
                .uniqueResult();
        return res.intValue();
    }

    public static Integer getChurchIssuesCount()
    {
        Session session = getSession();
        Long res = (Long) session.createQuery(
                "select count(*) " +
                        "from Church c where c.approvedDT is null ")
                .uniqueResult();
        return res.intValue();
    }

    public static List<User> parseUserList(String[] strings)
    {
        List<User> res = new ArrayList<>();
        for (String s : strings) {
            long id = safeLong(s, -1);
            if (id > -1)
                res.add((User) getSession().get(User.class, id));
            else
                System.out.println("Warning! Bad authors detected!");
        }
        return res;
    }

    public static long getTotalChurches(ChurchFilter filter)
    {
        String nameFilter = filter.getNameFilter();
        if (nameFilter != null && !"".equals(nameFilter))
            return (long) getSession().createQuery(
                    "select distinct count(c1) " +
                            "from Church c1, Church c2 " +
                            "where " +
                            "c1.extID = c2.extID and " +
                            "c1.version >= c2.version and " +
                            "c1.name like :fname and " +
                            "c1.approvedDT is not null " +
                            "order by c1.version desc, c1.extID"
            ).setParameter("fname", "%" + filter.getNameFilter() + "%")
                    .uniqueResult();
        else
            return (long) getSession().createQuery(
                    "select distinct count(c1) " +
                            "from Church c1, Church c2 " +
                            "where " +
                            "c1.extID = c2.extID and " +
                            "c1.version >= c2.version and " +
                            "c1.approvedDT is not null " +
                            "order by c1.version desc, c1.extID"
            ).setCacheable(true).uniqueResult();
    }

    public static long getTotalUsers()
    {
        return (long) getSession().createQuery("select count(*) from User u").setCacheable(true).uniqueResult();
    }

    public static long getTotalMediaContent(MediaContentType mct)
    {
        return (long) getSession().createQuery(
                "select count(*)" +
                        "from MediaContent mc where " +
                        "mc.contentType = :mct ")
                .setParameter("mct", mct)
                .setCacheable(true)
                .uniqueResult();
    }

    public static Map<String, Object> getChurchCountSummary()
    {
        Map<String, Object> res = new HashMap<>();
        res.put("metro", getMetroCount());
        res.put("dio", getDioCount());
        res.put("dek", getDekCount());
        return res;
    }

    private static Object getDekCount()
    {
        return getSession()
                .createQuery("select dek.id, count(c.extID) from Dekanat dek, Church c " +
                        "where c.address.dekanat = dek and c.approvedDT is not null " +
                        "group by dek.id").setCacheable(true).list();
    }

    private static Object getDioCount()
    {
//        return getSession().createSQLQuery("select id, count, centroid from dio_view")
//                .addScalar("id", StandardBasicTypes.STRING)
//                .addScalar("count", StandardBasicTypes.LONG)
//                .addScalar("centroid", GeometryType.INSTANCE)
//                .addSynchronizedQuerySpace("")
//                .setCacheable(true)
//                .list();
        return getSession().createQuery("select d.id, count(c.extID), d.centroid " +
                "from Diocese d, Church c " +
                "where c.address.dekanat.diocese = d and c.approvedDT is not null " +
                "group by d.id")
                .setCacheable(true)
                .list();
    }

//    @JsonSerialize(using = PointConverter.class)
    private static Object getMetroCount()
    {
        return getSession().createQuery("select m.id, count(c.extID), m.centroid " +
                "from Metropolie m, Church c " +
                "where c.address.dekanat.diocese.metropolie = m and c.approvedDT is not null " +
                "group by m.id")
                .setCacheable(true)
                .list();
    }

    public static Image findImageByPath(String path)
    {
        System.out.println("searching for an image with path: " + path);
        if (path == null)
            return null;
        return (Image) getSession().createQuery(
                "select i from Image i " +
                        "where i.path = :p")
                .setParameter("p", path).uniqueResult();
    }

    public static Church getChurch(Http.Request request)
    {
        // todo: extract from path
        String[] churches = request.queryString().get("church");
        if (churches != null)
        {
            long id = safeLong(churches, 0);
            return (Church) getSession().get(Church.class, id);
        }
        return null;
    }

//    public static List<User> parseUserList(Set<Long> userList )
//    {
//        Session session = getSession();
//        List<User> users = session.createQuery("from Users u where u.id in (:list)").setParameter("list", userList).list();
//        return users;
//    }
//
//    public static List<User> parseUserList(String[] strings)
//    {
//        String idList = StringUtils.join(strings, ",");
//        return parseUserList(idList);
//    }
}
