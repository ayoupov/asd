package models.internal.media.content.strategy.impl;

import models.MediaContent;
import models.internal.media.content.strategy.SequencerStrategy;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 17:58
 */
public class NotImplementedStrategy extends SequencerStrategy
{
    @Override
    public int getMax()
    {
        return 0;
    }

    @Override
    public MediaContent next()
    {
        return null;
    }
}
