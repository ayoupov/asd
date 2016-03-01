package models.internal;

import com.feth.play.module.pa.user.AuthUser;
import models.*;
import models.address.Address;
import models.internal.email.EmailTemplate;
import models.internal.email.EmailUnsubscription;
import models.internal.identities.MockIdentity;
import models.internal.search.filters.ChurchFilter;
import models.internal.search.filters.ImageFilter;
import models.internal.search.filters.QueryFilter;
import models.internal.search.filters.UserFilter;
import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Query;
import org.hibernate.Session;
import play.Logger;
import play.mvc.Http;

import java.util.*;
import java.util.stream.Collectors;

import static utils.DataUtils.safeLong;
import static utils.HibernateUtils.*;

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
                try {
                    Long id = Long.parseLong(rawId);
                    MediaContent content = (MediaContent) session.get(MediaContent.class, id);
                    if (content != null) {
                        if (content.getApprovedDT() != null || skipApproval)
                            res.add(content); // todo: more verbose in case of unapproved request?
                    }
                } catch (Exception e) {
                    throw new RequestException(e);
                }
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
                        "order by mc.approvedDT DESC"
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

    public static Church getChurch(String extID)
    {
        return getChurch(extID, false);
    }

    public static Church getChurch(String extID, boolean skipApproval)
    {
        Session session = getSession();
        Church church = (Church) session.createQuery(
                "select c from Church c " +
                        "where (:sa = TRUE or c.approvedDT is not null) and " +
                        "c.extID = :id "
        ).setParameter("sa", skipApproval).setParameter("id", extID).setMaxResults(1).uniqueResult();
        return church;
    }

    public static List<Object> getChurchesShort()
    {
        Session session = getSession();
        List churches = session.createQuery("select distinct c.id, c.address.geometry " +
                "from Church c " +
                "where " +
                "c.approvedDT is not null " +
                "order by c.extID").list();
        return churches;
    }

    public static List<Church> getChurches()
    {
        Session session = getSession();
        List<Church> churches = session.createQuery(
                "select distinct c1 " +
                        "from Church c1 " +
                        "where " +
                        "c1.approvedDT is not null ").list();
        return churches;
    }

    public static List<Church> getChurches(ChurchFilter filter)
    {
        Session session = getSession();
        Query query = session.createQuery(
                "select distinct c, count(cs) as reqs " +
                        "from Church c, ChurchSuggestion cs where " +
                        "(c.name like :fname or c.extID like :fname) and " +
                        "(cs.relatedChurch = c or cs.relatedChurch is null) " +
                        "and cs.fixed = false and cs.ignored = false " +
                        "group by " +
                        "c.id " +
                        "order by " +
                        "(CASE WHEN c.approvedDT IS NULL THEN 1 ELSE 0 END) DESC, " +
                        "reqs desc, c.approvedDT desc, c.extID asc")
                .setParameter("fname", "%" + filter.getNameFilter() + "%")
                .setMaxResults(filter.getMaxResults())
                .setFirstResult(filter.getPage() * filter.getMaxResults());
        List churches = query.list();
//        Logger.debug("got churches: " + churches);
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
                        "order by (CASE WHEN mc.approvedDT IS NULL THEN 1 ELSE 0 END) DESC, " +
                        "mc.approvedDT DESC")
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
        Long res2 = (Long) session.createQuery(
                "select count(cs.id) from ChurchSuggestion cs, Church c " +
                        "where cs.fixed = FALSE and cs.ignored = FALSE and (cs.relatedChurch = c) and cs.type = :cst"
        ).setParameter("cst", ChurchSuggestionType.FIELD).uniqueResult();

        Long res3 = (Long) session.createQuery(
                "select count(cs.id) from ChurchSuggestion cs " +
                        "where cs.fixed = FALSE and cs.ignored = FALSE and cs.type = :cst"
        ).setParameter("cst", ChurchSuggestionType.NEW_CHURCH).uniqueResult();
        return res.intValue() + res2.intValue() + res3.intValue();
    }

    public static Integer getImageIssuesCount()
    {
        Session session = getSession();
        Long res = (Long) session.createQuery(
                "select count(*) " +
                        "from Image i where i.approvedTS is null ")
                .uniqueResult();
        return res.intValue();
    }

    public static Set<User> parseUserList(String[] strings)
    {
        Set<User> res = new LinkedHashSet<>();
        for (String s : strings) {
            long id = safeLong(s, -1);
            if (id > -1)
                res.add((User) getSession().get(User.class, id));
            else {
                Logger.warn("Warning! Creating an author: " + s);
                AuthUser internalUser = new MockIdentity(StringUtils.stripAccents(s.replaceAll("\\s", "")), s, "architektura7dnia.pl");
                User user;
                user = UserManager.findByAuthUserIdentity(internalUser);
                if (user == null) {
                    user = UserManager.createUser(internalUser, UserRole.Guest, UserStatus.Blocked);
                }
                saveOrUpdate(user);
                res.add(user);
            }
        }
        return res;
    }

    public static long getTotalChurches(ChurchFilter filter)
    {
        String nameFilter = filter.getNameFilter();
        if (nameFilter != null && !"".equals(nameFilter))
            return (long) getSession().createQuery(
                    "select distinct count(c) " +
                            "from Church c " +
                            "where " +
                            "c.name like :fname "
//                            "and c.approvedDT is not null "
            ).setParameter("fname", "%" + filter.getNameFilter() + "%")
                    .uniqueResult();
        else
            return getChurchCount(true);
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
//        res.put("dek", getDekCount());
        return res;
    }

//    private static Object getDekCount()
//    {
//        return getSession()
//                .createQuery("select dek.id, count(c.extID) from Dekanat dek, Church c " +
//                        "where c.address.dekanat = dek and c.approvedDT is not null " +
//                        "group by dek.id").setCacheable(true).list();
//    }

    private static Object getDioCount()
    {
//        return getSession().createSQLQuery("select id, count, centroid from dio_view")
//                .addScalar("id", StandardBasicTypes.STRING)
//                .addScalar("count", StandardBasicTypes.LONG)
//                .addScalar("centroid", GeometryType.INSTANCE)
//                .addSynchronizedQuerySpace("")
//                .setCacheable(true)
//                .list();
        return getSession().createQuery("select d.id, count(c.extID), d.centroid, d.name " +
                "from Diocese d, Church c " +
                "where c.address.diocese = d and c.approvedDT is not null " +
                "group by d.id")
                .setCacheable(true)
                .list();
    }

    //    @JsonSerialize(using = PointConverter.class)
    private static Object getMetroCount()
    {
        return getSession().createQuery("select m.id, count(c.extID), m.centroid, m.name " +
                "from Metropolie m, Church c " +
                "where c.address.diocese.metropolie = m and c.approvedDT is not null " +
                "group by m.id")
                .setCacheable(true)
                .list();
    }

    public static Image findImageByPath(String path)
    {
        if (path == null)
            return null;
        Logger.info("searching for an image with path: " + (path = path.replaceAll("//", "/")));
        return (Image) getSession().createQuery(
                "select i from Image i " +
                        "where i.path = :p")
                .setParameter("p", path).setMaxResults(1).uniqueResult();
    }

    public static Church getChurch(Http.Request request)
    {
        // todo: extract from path
        String[] churches = request.queryString().get("church");
        if (churches != null) {
            long id = safeLong(churches, 0);
            return (Church) getSession().get(Church.class, id);
        }
        return null;
    }

    public static List<Address> getEmptyAddresses(int howMany)
    {
        return getSession().createQuery("from Address where unfolded is null").setMaxResults(howMany).list();
    }

    public static Architect getArchitectByName(String name)
    {
        return (Architect) getSession().createQuery("from Architect a where a.name = :n")
                .setParameter("n", name)
                .uniqueResult();
    }

    public static MediaContent getMediaByIdAndAlternative(String id)
    {
        long simpleId = safeLong(id, 0);
        if (simpleId != 0)
            return (MediaContent) getSession().get(MediaContent.class, simpleId);
        return (MediaContent) getSession().createQuery("from MediaContent mc where mc.alt = :aid")
                .setParameter("aid", id).setMaxResults(1).setCacheable(true).uniqueResult();
    }

    public static List<Church> getUninternetedChurches()
    {
        return getSession().createQuery("from Church c where c.website is null").list();
    }

    public static List<MediaContent> getMediaContent(MediaContentType contentType)
    {
        return getSession().createQuery("select mc " +
                "from MediaContent mc where mc.contentType = :mct")
                .setParameter("mct", contentType).setCacheable(true).list();
    }

    public static List<EmailTemplate> getEmails()
    {
        return getSession().createQuery("from EmailTemplate e").list();
    }

    public static EmailTemplate emailByName(String name)
    {
        return (EmailTemplate) getSession().createQuery("from EmailTemplate e where name = :name").setString("name", name).uniqueResult();
    }

    public static List<MediaContent> getRelatedForArticles(MediaContent mcFor)
    {
        List<MediaContent> articles = getSession().createQuery(
                "from MediaContent mc where mc.contentType = :ct and mc.id != :thisid " +
                        "and mc.approvedDT is not null " +
                        "order by rand()")
                .setParameter("ct", MediaContentType.Article)
                .setParameter("thisid", mcFor.getId())
                .setMaxResults(3)
                .list();
        List<MediaContent> stories = getSession().createQuery(
                "from MediaContent mc where mc.contentType = :ct " +
                        "and mc.approvedDT is not null " +
                        "order by rand()")
                .setParameter("ct", MediaContentType.Story)
                .setMaxResults(8 - articles.size())
                .list();
        List<MediaContent> res = new ArrayList<>();
        res.addAll(articles);
        res.addAll(stories);
        return res;
    }

    public static List<MediaContent> getRelatedForStory(MediaContent mc, int size, Set<Long> alreadyDone)
    {
        alreadyDone.add(mc.getId());
        List<MediaContent> res = new ArrayList<>();
        List<MediaContent> storiesRelatedByDiocese =
                getSession().createQuery("from MediaContent mc " +
                        "where mc.contentType = :ct " +
                        "and mc.approvedDT is not null " +
                        "and mc.dedicatedChurch.address.diocese = :mcdio " +
                        "and mc.id not in (:ids) " +
                        "order by rand()")
                        .setParameter("ct", MediaContentType.Story)
                        .setParameter("mcdio", mc.getDedicatedChurch().getAddress().getDiocese())
                        .setParameterList("ids", alreadyDone)
                        .setMaxResults(size)
                        .list();
        res.addAll(storiesRelatedByDiocese);
        if (res.size() < size) {
            alreadyDone.addAll(storiesRelatedByDiocese.stream().map(MediaContent::getId).collect(Collectors.toSet()));
            List<MediaContent> storiesRelatedByMetropolie =
                    getSession().createQuery("from MediaContent mc " +
                            "where mc.contentType = :ct " +
                            "and mc.approvedDT is not null " +
                            "and mc.dedicatedChurch.address.diocese.metropolie = :mcmetro " +
                            "and mc.id not in (:ids) " +
                            "order by rand()")
                            .setParameter("ct", MediaContentType.Story)
                            .setParameter("mcmetro", mc.getDedicatedChurch().getAddress().getDiocese().getMetropolie())
                            .setParameterList("ids", alreadyDone)
                            .setMaxResults(size - res.size())
                            .list();
            res.addAll(storiesRelatedByMetropolie);
            if (res.size() < size) {
                alreadyDone.addAll(storiesRelatedByDiocese.stream().map(MediaContent::getId).collect(Collectors.toSet()));
                res.addAll(
                        getSession().createQuery("from MediaContent mc " +
                                "where mc.contentType = :ct " +
                                "and mc.approvedDT is not null " +
                                "and mc.id not in (:ids) " +
                                "order by rand()")
                                .setParameter("ct", MediaContentType.Story)
                                .setParameterList("ids", alreadyDone)
                                .setMaxResults(size - res.size())
                                .list()
                );
            }
        }
        return res;
    }

    public static EmailTemplate getEmailTemplateByName(String templateName)
    {
        return (EmailTemplate) getSession().createQuery("from EmailTemplate et where et.name=:etn")
                .setParameter("etn", templateName)
                .setCacheable(true)
                .uniqueResult();
    }

    public static EmailUnsubscription getUnsubscription(String hash)
    {
        return (EmailUnsubscription) getSession().createQuery("from EmailUnsubscription eu where eu.hash = :hash")
                .setParameter("hash", hash)
                .uniqueResult();
    }

    public static long getTotalFullImages()
    {
        return (long) getSession().createQuery(
                "select distinct count(i) " +
                        "from Image i "
        ).setCacheable(true).uniqueResult();
    }

    public static long getTotalImages(ImageFilter filter)
    {
        if (filter == null)
            return getTotalFullImages();
        String nameFilter = filter.getNameFilter();
        String churchFilter = filter.getChurchFilter();
        if (nameFilter != null && !"".equals(nameFilter))
            return (long) getSession().createQuery(
                    "select distinct count(i) " +
                            "from Image i " +
                            "where " +
                            "i.description like :fname"
            ).setParameter("fname", "%" + filter.getNameFilter() + "%")
                    .uniqueResult();
        else if (churchFilter != null && !"".equals(churchFilter)) {
            return (long) getSession().createQuery(
                    "select distinct count(i) " +
                            "from Image i, Church c " +
                            "where " +
                            "i in elements(c.images) and " +
                            "c.extID like :clike "

            ).setParameter("clike", "%" + churchFilter + "%")
                    .uniqueResult();
        } else
            return getTotalFullImages();
    }

    public static Map<Image, List<Church>> getImages(ImageFilter filter)
    {
        Session session = getSession();
        Query query;
        String churchFilter = filter.getChurchFilter();
        if (churchFilter != null && !"".equals(churchFilter))
            query = session.createQuery(
                    "select distinct i " +
                            "from Church c, Image i where " +
                            "i in elements(c.images) and " +
                            "c.extID like :clike " +
                            "order by " +
                            "(CASE WHEN i.approvedTS IS NULL THEN 1 ELSE 0 END) DESC," +
                            "i.approvedTS desc")
                    .setParameter("clike", "%" + filter.getChurchFilter() + "%")
                    .setMaxResults(filter.getMaxResults())
                    .setFirstResult(filter.getPage() * filter.getMaxResults());
        else
        query = session.createQuery(
                "select distinct i " +
                        "from Image i " +
//                        "where " +
//                        "i.description like :fname " +
                        "order by " +
                        "(CASE WHEN i.approvedTS IS NULL THEN 1 ELSE 0 END) DESC, " +
                        "i.approvedTS desc")
//                .setParameter("fname", "%" + filter.getNameFilter() + "%")
                .setMaxResults(filter.getMaxResults())
                .setFirstResult(filter.getPage() * filter.getMaxResults());
        Map<Image, List<Church>> res = new LinkedHashMap<>();
        List<Image> images = query.list();
        for (Image image : images)
        {
            ArrayList churches = new ArrayList();
            churches.addAll(getChurchesFromImage(image));
            res.put(image, churches);
        }
        return res;
    }

    private static Collection getChurchesFromImage(Image image)
    {
        return getSession().createQuery("select distinct c from Church c, Image i " +
                "where :image in elements(c.images)").setParameter("image", image).list();
    }

    public static Set<Architect> parseArchitectsList(String[] architectsRaw)
    {
        Set<Architect> res = new LinkedHashSet<>();
        for (String sRaw : architectsRaw) {
            String[] rawsplit = sRaw.split(",");
            for (String s : rawsplit) {
                long id = safeLong(s, -1);
                if (id > -1)
                    res.add((Architect) getSession().get(Architect.class, id));
                else {
                    if (!"".equals(s)) {
                        Logger.warn("Warning! Searching for an architect: " + s);
                        Architect architect = ContentManager.getArchitectByName(s);
                        if (architect == null) {
                            architect = new Architect();
                            architect.setName(s);
                            architect.setId((Long) save(architect));
                        }
                        res.add(architect);
                    }
                }
            }
        }
        return res;
    }

    public static List<UserFeedback> getFeedbacks()
    {
        return getSession().createQuery("from UserFeedback uf where uf.hidden = false ").list();
    }

    public static Long getTotalFeedbacks()
    {
        return (Long) getSession().createQuery("select count(*) from UserFeedback uf ").setCacheable(true).uniqueResult();
    }

    public static List<ChurchSuggestion> getSuggestedChurches()
    {
        return getSession().createQuery("from ChurchSuggestion cs " +
                        "where cs.fixed = FALSE and cs.ignored = FALSE and cs.type = :cst"
        ).setParameter("cst", ChurchSuggestionType.NEW_CHURCH).list();
    }

    public static List<String> getDioceseIds()
    {
        return getSession().createQuery("select d.id from Diocese d order by d.id asc").setCacheable(true).list();
    }

    public static List<Church> getChurchesWithImage(Image image)
    {
        return getSession().createQuery("from Church c where :i in elements(c.images)").setParameter("i", image).list();
    }
}
