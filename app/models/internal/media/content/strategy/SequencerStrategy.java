package models.internal.media.content.strategy;

import models.MediaContent;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 17:49
 */
public abstract class SequencerStrategy
{

    protected Set<Long> ids = new LinkedHashSet<>();
    protected Iterator<Long> idIterator;
    protected Long max = null;

    public abstract int getMax();

    public abstract MediaContent get();
}
