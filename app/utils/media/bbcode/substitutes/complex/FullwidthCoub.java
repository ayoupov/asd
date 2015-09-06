package utils.media.bbcode.substitutes.complex;

import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.options.CoubOptions;
import utils.media.bbcode.substitutes.options.CoubOptionsFactory;
import utils.media.bbcode.substitutes.options.ImageOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 4:44
 */
@SubstituteAnnotation
public class FullwidthCoub extends FullwidthImage
{

    private CoubOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[coub=fullwidth(.*?)](.*?)\\[\\/coub]");

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = CoubOptionsFactory.getOptions(matcher.group(1));
        System.out.println("opts = " + opts);
        opts.setCaption(matcher.group(2));

        return String.format(
                "<iframe src='%s?muted=%s&autostart=%s&originalSize=%s&hideTopBar=true&startWithHD=%s' " +
                        "allowfullscreen='%s' frameborder='0' width='100%%' height='%d'></iframe>",
                "//coub.com/embed/" + opts.getSrc(), opts.getMuted(), opts.getAutostart(),
                opts.getOriginalSize(), opts.getStartWithHD(),
                opts.getAllowFullScreen(), opts.getHeight());
    }

    @Override
    public String getTag()
    {
        return "fwcoub";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    public ImageOptions getOptions()
    {
        return opts;
    }
}
