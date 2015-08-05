package models.internal.media.content.strategy.impl;

import models.MediaContentType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 19:07
 */
public class StarredStoryStrategy extends StoryStrategy
{
    public StarredStoryStrategy()
    {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        Query query = session
                .createQuery("select mc.id from MediaContent mc where mc.contentType = :ct and mc.approvedDT is not null order by mc.starred desc, RAND()")
                .setParameter("ct", MediaContentType.Story);
        ids.addAll(query.list());
        tx.commit();
        idIterator = ids.iterator();
    }
}
