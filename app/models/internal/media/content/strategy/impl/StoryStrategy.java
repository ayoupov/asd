package models.internal.media.content.strategy.impl;

import models.MediaContent;
import models.MediaContentType;
import models.internal.media.content.strategy.SequencerStrategy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 19:07
 */
public abstract class StoryStrategy extends SequencerStrategy
{

    public int getMax()
    {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        if (max == null)
            max = (Long) session
                    .createQuery("select count(*) from MediaContent mc where mc.contentType = :ct")
                    .setParameter("ct", MediaContentType.Story)
                    .setCacheable(true)
                    .uniqueResult();
        tx.commit();
        return max.intValue();
    }

    public MediaContent get()
    {
        if (idIterator.hasNext()) {
            Long id = idIterator.next();
            return (MediaContent) getSession().get(MediaContent.class, id);
        } else return null;
    }
}
