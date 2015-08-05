package models.internal;

import models.MediaContent;
import models.MediaContentType;
import org.apache.commons.lang3.tuple.Triple;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
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

    public static List<Triple<Long, Date, Boolean>> getSummary(MediaContentType contentType)
    {
        return getSummary(contentType, false);
    }

    public static List<Triple<Long, Date, Boolean>> getSummary(MediaContentType contentType, boolean skipApproval)
    {
        Session session = getSession();
        List<Triple<Long, Date, Boolean>> res = session.createQuery(
                "select mc.id, mc.approvedDT , mc.starred from MediaContent mc " +
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
                "select count(*) from Church c " +
                        "where (:sa = TRUE or c.approvedDT is not null) " +
                        "order by c.approvedDT desc"
        )
                .setParameter("sa", skipApproval)
                .setCacheable(true).uniqueResult();
        return res;
    }

    public static long getChurchCount()
    {
        return getChurchCount(false);
    }
}
