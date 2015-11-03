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
public class GridCoub extends GridImage
{

    private CoubOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[coub\\s(.*?)](.*?)\\[\\/coub]");

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = CoubOptionsFactory.getOptions(matcher.group(1));
        opts.setCaption(matcher.group(2));

        int to = opts.getTo();
        int from = opts.getFrom();
        boolean gutter = opts.getGutter();
        int width = GridUtil.gridWidth(from, to, gutter);
        marginLeft = GridUtil.leftMargin(from, to, gutter);
        String coubStyle = String.format("style='width: %dpx; margin-left: %dpx;' ", width, marginLeft);

        return String.format(
                "<iframe src='%s?muted=%s&autostart=%s&originalSize=%s&hideTopBar=true&startWithHD=%s' " +
                        "allowfullscreen='%s' frameborder='0' height='%d' %s></iframe>",
                "//coub.com/embed/" + opts.getSrc(), opts.getMuted(), opts.getAutostart(),
                opts.getOriginalSize(), opts.getStartWithHD(),
                opts.getAllowFullScreen(), opts.getHeight(),
                coubStyle);
    }

    @Override
    public String getTag()
    {
        return "coub";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    protected String getPostfix()
    {
        return String.format("<span class='content-image-caption' style='margin-left: %dpx;'>%s</span><br>",
                marginLeft, getOptions().getCaption());
    }

    @Override
    public ImageOptions getOptions()
    {
        return opts;
    }

}
