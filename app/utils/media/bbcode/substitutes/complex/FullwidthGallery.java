package utils.media.bbcode.substitutes.complex;

import utils.media.bbcode.BBCodeParser;
import utils.media.bbcode.substitutes.FullwidthSubstitute;
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
public class FullwidthGallery extends FullwidthSubstitute
{

    private ImageOptions opts;
    private static final Pattern pattern = Pattern.compile("\\[gallery=fullwidth\\s*?(.*?)](.*?)\\[\\/gallery]", Pattern.DOTALL);

    @Override
    protected String getProcessed(StringBuffer sb, Matcher matcher, BBCodeParser.BBCodeRenderState state)
    {
        opts = ImageOptionsFactory.getOptions(matcher.group(1));
        String opt = "";
        if (opts.heightSet())
            opt += String.format("style='height: %d;'", opts.getHeight());

        return String.format("<div id='gallery_%d' class='content-gallery full-width' %s>%s</div>", state.nextId(), opt, matcher.group(2));
    }

    @Override
    public String getTag()
    {
        return "fwgallery";
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

//    @Override
//    protected String getPostfix()
//    {
//        return "</div>";
//    }

    public ImageOptions getOptions()
    {
        return opts;
    }
}
