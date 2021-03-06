package utils.media.bbcode.substitutes.complex;

import utils.ServerProperties;
import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.FullwidthSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.options.ImageOptionsFactory;
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
public class FullwidthImage extends FullwidthSubstitute
{

    private ImageOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[image=fullwidth(.*?)](.*?)\\[\\/image]");

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = ImageOptionsFactory.getOptions(matcher.group(1));
        opts.setCaption(matcher.group(2));
        String src = opts.getSrc() == null ? ServerProperties.getValue("asd.editor.nosrc.image") : opts.getSrc();

        return String.format(
                "<img src='%s' data-image='%s' class='ui image full-width content-image' alt='%s'>",
                src, src, opts.getCaption());
    }

    @Override
    public String getTag()
    {
        return "fwimage";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    protected String getPostfix()
    {
        String caption = getOptions().getCaption();
        String postfix = super.getPostfix();
        if (caption != null && !"".equals(caption.trim()))
            return postfix +
                    String.format("<div class='content-image-caption'  style='margin-left: 0;'>%s</div><br>", caption);
//                    String.format("<span class='content-image-caption'  style='margin-left: -120px;'>%s</span><br>", caption);
        else
            return postfix;
    }

    public ImageOptions getOptions()
    {
        return opts;
    }
}
