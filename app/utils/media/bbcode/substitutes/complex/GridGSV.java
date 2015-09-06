package utils.media.bbcode.substitutes.complex;

import utils.ServerProperties;
import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.options.GSVOptions;
import utils.media.bbcode.substitutes.options.GSVOptionsFactory;
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
public class GridGSV extends GridImage
{

    private GSVOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[gsv\\s(.*?)](.*?)\\[\\/gsv]");

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = GSVOptionsFactory.getOptions(matcher.group(1));
        opts.setCaption(matcher.group(2));

        int to = opts.getTo();
        int from = opts.getFrom();
        int width = (to - from + 1) * (colWidth + colGutterWidth) - colGutterWidth;
        marginLeft = (from - 2) * (colWidth + colGutterWidth);
        String gsvStyle = String.format("style='width: %dpx; margin-left: %dpx;' ", width, marginLeft);

        return String.format(
                "<iframe height='%d' frameborder='0' style='border:0'" +
                        " src='https://www.google.com/maps/embed/v1/streetview?key=%s&location=%s&heading=%d' " +
                        "allowfullscreen %s></iframe>",
                opts.getHeight(), ServerProperties.getValue("google.api.key"),
                opts.getSrc(), opts.getHeading(), gsvStyle);
    }

    @Override
    public String getTag()
    {
        return "gsv";
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
