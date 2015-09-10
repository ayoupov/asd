package utils.media.bbcode.substitutes.complex;

import utils.ServerProperties;
import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.ComplexSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.options.ImageOptions;
import utils.media.bbcode.substitutes.options.ImageOptionsFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 4:44
 */
@SubstituteAnnotation
public class GridGallery extends ComplexSubstitute
{

    private ImageOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[gallery\\s(.*?)](.*?)\\[\\/gallery]", Pattern.DOTALL);
    protected int marginLeft = 0;

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = ImageOptionsFactory.getOptions(matcher.group(1));

        int to = opts.getTo();
        int from = opts.getFrom();
        int width = GridUtil.gridWidth(from, to);
        marginLeft = GridUtil.leftMargin(from, to);
        String opt = "";
        if (opts.heightSet())
            opt += String.format("height: %spx;", opts.getHeight());
        String imageStyle = String.format("style='width: %dpx; margin-left: %dpx; %s' ", width, marginLeft, opt);

        return String.format( "<div id='gallery_%d' class='content-gallery'  %s>%s", state.nextId(), imageStyle, matcher.group(2));
    }

    @Override
    public String getTag()
    {
        return "gallery";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    protected String getPrefix()
    {
        return "";
    }

    @Override
    protected String getPostfix()
    {
        return "</div>";
    }

    public ImageOptions getOptions()
    {
        return opts;
    }
}
