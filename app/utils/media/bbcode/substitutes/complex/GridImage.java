package utils.media.bbcode.substitutes.complex;

import utils.ServerProperties;
import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.options.ImageOptions;
import utils.media.bbcode.substitutes.ComplexSubstitute;
import utils.media.bbcode.substitutes.options.ImageOptionsFactory;
import utils.media.bbcode.substitutes.SubstituteAnnotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 4:44
 */
@SubstituteAnnotation
public class GridImage extends ComplexSubstitute
{

    private ImageOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[image\\s(.*?)](.*?)\\[\\/image]");
    protected static final int colWidth = 100;
    protected static final int colGutterWidth = 20;
    protected int marginLeft = 0;
    protected int width = 0;

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = ImageOptionsFactory.getOptions(matcher.group(1));
        opts.setCaption(matcher.group(2));

        int to = opts.getTo();
        int from = opts.getFrom();
        boolean gutter = opts.getGutter();
        width = GridUtil.gridWidth(from, to, gutter);
        marginLeft = GridUtil.leftMargin(from, to, gutter);
//        int width = (to - from + 1) * (colWidth + colGutterWidth) - colGutterWidth;
//        marginLeft = (from - 2) * (colWidth + colGutterWidth);
//        int width = (to - from + 1) * (colWidth + colGutterWidth) + colGutterWidth;
//        marginLeft = (from - 2) * (colWidth + colGutterWidth) - colGutterWidth;
        String imageStyle = String.format("style='width: %dpx; margin-left: %dpx;' ", width, marginLeft);

        String src = opts.getSrc() == null ? ServerProperties.getValue("asd.editor.nosrc.image") : opts.getSrc();
        return String.format(
                "<img src='%s' data-image='%s' alt='%s' data-description='%s' class='content-image' %s>",
                src, src,
                opts.getCaption(), opts.getCaption(), imageStyle);
    }

    @Override
    public String getTag()
    {
        return "image";
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
        return String.format("<br><div class='content-image-caption' style='margin-left: %dpx;width: %dpx'>%s</div><br>",
                marginLeft, width, getOptions().getCaption());
    }

    public ImageOptions getOptions()
    {
        return opts;
    }
}
