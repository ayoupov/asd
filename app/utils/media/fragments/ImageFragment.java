package utils.media.fragments;

import org.apache.commons.lang3.tuple.Pair;
import org.panaggelica.media.Fragment;
import org.panaggelica.media.FragmentDescription;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.DataUtils.safeInt;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 17:03
 */
@Fragment
public class ImageFragment extends ContentFragment
{

    private static final String TAG = "image";

    private static final String gridPrefix = "<div class='content-main'>";
    private static final String gridPostfix = "</div>";
    private static final String prefixPattern = "<div class='full-width %s'>";  // color goes here
    private static final String postfix = "</div>";

    public static final String TEXTSIZE = "textsize";
    public static final String FULLWIDTH = "fullwidth";
    public static final String MAINFULLWIDTH = "mainfullwidth";
    public static final String TEXTSIZEPLUS = "textsizeplus";
    private static final String CAPTION_MARGIN_STYLE_PATTERN = " style='margin-left: %dpx;' ";
    private String prfx = "", pstfx = "";
    private static final String IMAGE_PATTERN = "<img class='ui %s image' src='%s' %s/>";
    private static final String CAPTION_PATTERN = "<span class='content-image-caption' %s>%s</span>";
    private static final int marginLeftInit = 20;
    private static final int colWidth = 100;
    private static final int colGutterWidth = 20;
    private Integer colN = null, colM = null;
    private String url;
    private String caption;
    private String imageClass = null;
    private String background = null;
    private String imageStyle = null;
//    private String length;

    private static final List<String> majorOptions = Arrays.asList(TEXTSIZE, TEXTSIZEPLUS, MAINFULLWIDTH, FULLWIDTH);
    private static final String GREY = "grey";
    private static final String WHITE = "white";
    private static final List<String> colorOptions = Arrays.asList(GREY, WHITE);
    private String[] options;

    private static final ImageFragmentDescription description = new ImageFragmentDescription();
    private int marginLeft = 0;

    public ImageFragment(String[] options, String content)
    {
        this.options = options;
        String[] split;
        if (content.indexOf(",") > 0) {
            split = content.split(",");
            url = split[0];
            caption = split[1];
            imageStyle = String.format(" alt='%s' ", caption);
        } else
            this.url = content;
        initOptions(options);
    }

    private void initOptions(String[] options)
    {
        boolean needPrefixAdjustment = false;
        for (String option : options) {
            if (majorOptions.contains(option)) {
                switch (option) {
                    case TEXTSIZE:
                        prfx = gridPrefix + prefixPattern;
                        needPrefixAdjustment = true;
                        imageClass = "fit-main-text";
                        pstfx = postfix + gridPostfix;
                        break;
                    case TEXTSIZEPLUS:
                        prfx = gridPrefix + prefixPattern;
                        needPrefixAdjustment = true;
                        imageClass = "fit-main-text-plus";
                        pstfx = postfix + gridPostfix;
                        break;
                    case MAINFULLWIDTH:
                        prfx = gridPrefix + prefixPattern;
                        needPrefixAdjustment = true;
                        pstfx = postfix + gridPostfix;
                        break;
                    case FULLWIDTH:
                        prfx = prefixPattern;
                        needPrefixAdjustment = true;
                        pstfx = postfix;
                }
            } else if (option.matches("\\dto\\d")) {
//                System.out.println("option = " + option);
                colM = safeInt(option.substring(0, 1), 1);
                colN = safeInt(option.substring(3), 1);
//                System.out.println(colM + "to" + colN);
                prfx = gridPrefix;
                pstfx = gridPostfix;
                int width = (colN - colM) * (colWidth + colGutterWidth) - colGutterWidth;
                marginLeft = marginLeftInit + (colM - 1) * (colWidth + colGutterWidth);
                imageStyle += String.format(" style='width: %dpx; margin-left: %dpx;' ", width, marginLeft);
            } else {
                // background or
                if (colorOptions.contains(option)) {
                    if ("grey".equalsIgnoreCase(option))
                        background = "content-grey";
                    else if ("white".equalsIgnoreCase(option))
                        background = "content-white";
                }
            }

        }
        if (needPrefixAdjustment)
            prfx = String.format(prfx, (background == null) ? "" : "content-" + background);

    }

    public ImageFragment()
    {
    }

    @Override
    public String render()
    {
        String captionMargin = "";
        if (marginLeft > 0) {
            captionMargin = String.format(CAPTION_MARGIN_STYLE_PATTERN, marginLeft);
        }
        String captionPart = (caption != null ? String.format(CAPTION_PATTERN, captionMargin, caption) : "");
        return prfx + String.format(IMAGE_PATTERN,
                (imageClass == null ? "" : imageClass), url, (imageStyle == null ? "" : imageStyle)) +
                captionPart +
                pstfx;
    }

    @Override
    public ContentFragmentDescription getDescription()
    {
        return description;
    }

    @Override
    public String getTag()
    {
        return TAG;
    }

    @Override
    public boolean accepts(String tag)
    {
        return TAG.equals(tag);
    }

    @Override
    public ContentFragment newFragment(String[] options, String content)
    {
        return new ImageFragment(options, content);
    }


    @FragmentDescription
    public static class ImageFragmentDescription extends ContentFragmentDescription
    {
        private static final List<Pair<String, String>> options = new ArrayList<>();

        static {
            options.add(Pair.of(ImageFragment.TEXTSIZE, "fits image to main text (2-6 col)"));
            options.add(Pair.of(ImageFragment.TEXTSIZEPLUS, "fits image to wider than main text (2-7 col)"));
            options.add(Pair.of(ImageFragment.MAINFULLWIDTH, "fits image to grid (1-8 col)"));
            options.add(Pair.of("MtoN", "fits image from column M to N (m-n col)"));
            options.add(Pair.of(ImageFragment.FULLWIDTH, "fits image to window width"));

            options.add(Pair.of("content-COLOR", "optional to set color of image background (grey and white only)"));

        }

        @Override
        public String getTag()
        {
            return TAG;
        }

        @Override
        public List<Pair<String, String>> getOptions()
        {
            return options;
        }
    }

}
