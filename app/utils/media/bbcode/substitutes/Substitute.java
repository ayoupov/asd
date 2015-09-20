package utils.media.bbcode.substitutes;

import utils.media.bbcode.BBCodeParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:29
 */
public interface Substitute
{
    String getTag();

    Pattern getPattern();

    String getReplacement();

    String process(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state);

    boolean isSimple();

    SubstitutePriority getPriority();
}
