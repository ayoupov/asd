package models.internal.media.content.strategy.impl;

import models.MediaContent;
import models.MediaContentType;
import models.internal.media.content.strategy.SequencerStrategy;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtils;

import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 19:07
 */
public class StarredArticleStrategy extends SequencerStrategy
{
    public int getMax()
    {
        Session session = getSession();
        Transaction tx = session.beginTransaction();

        if (max == null)
            max = (Long) session
                    .createQuery("select count(*) from MediaContent mc where mc.contentType = :ct")
                    .setParameter("ct", MediaContentType.Article)
                    .setCacheable(true)
                    .uniqueResult();
        tx.commit();
        return max.intValue();
    }

    public MediaContent next()
    {
        if (idIterator.hasNext()) {
            Long id = idIterator.next();
            return (MediaContent) HibernateUtils.get(MediaContent.class, id);
        } else return null;
    }

    public StarredArticleStrategy()
    {
        Session session = getSession();
        Transaction tx = session.beginTransaction();

        Query query = session
                .createQuery("select mc.id from MediaContent mc where mc.contentType = :ct  and mc.approvedDT is not null order by mc.starred desc, RAND()")
                .setParameter("ct", MediaContentType.Article);
        ids.addAll(query.list());
        tx.commit();
        idIterator = ids.iterator();
    }
}
