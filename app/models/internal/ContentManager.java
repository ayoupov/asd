package models.internal;

import models.Church;
import models.MediaContent;
import models.MediaContentType;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

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
}
