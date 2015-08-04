package models.internal.media.content.strategy;

import models.MediaContentType;
import models.internal.media.content.strategy.impl.DateStoryStrategy;
import models.internal.media.content.strategy.impl.NotImplementedStrategy;
import models.internal.media.content.strategy.impl.StarredArticleStrategy;
import models.internal.media.content.strategy.impl.StarredStoryStrategy;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.08.2015
 * Time: 17:50
 */
public class SequencerStrategyFactory
{
    private static SequencerStrategy notImplemented = new NotImplementedStrategy();

    public static SequencerStrategy get(MediaContentType contentType, SequencerStrategyMode mode)
    {
        switch (contentType)
        {
            case Article: return new StarredArticleStrategy();
            case Story: switch (mode){
                case Simple: return new DateStoryStrategy();
                case Starred: return new StarredStoryStrategy();
                default: return notImplemented;
            }
            default: return notImplemented;
        }
    }

}
