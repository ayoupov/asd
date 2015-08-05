package models.internal.media.content;

import models.MediaContent;
import models.MediaContentType;
import models.internal.media.content.strategy.SequencerStrategy;
import models.internal.media.content.strategy.SequencerStrategyFactory;
import models.internal.media.content.strategy.SequencerStrategyMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 17:28
 */
public class Sequencer
{
    private final String key;
    private final MediaContentType contentType;
    private SequencerStrategy mcss;
    private int maxItems, index;

    public Sequencer(String key, MediaContentType contentType, SequencerStrategyMode mode)
    {
        this.key = key;
        this.contentType = contentType;
        this.mcss = SequencerStrategyFactory.get(contentType, mode);
        maxItems = mcss.getMax();
        index = 0;
    }

    public String getKey()
    {
        return key;
    }

    public MediaContentType getContentType()
    {
        return contentType;
    }

    public List<MediaContent> get(Integer quantity)
    {
        // if there is less than asked
        if (quantity > left())
            quantity = left();
        List<MediaContent> lmc = new ArrayList<>();
        for (int i = 0; i < quantity; i++,index++)
        {
            MediaContent mc = mcss.next();
            lmc.add(mc);
        }
        return lmc;
    }

    public int left()
    {
        return maxItems - index;
    }
}
