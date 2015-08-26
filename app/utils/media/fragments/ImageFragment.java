package utils.media.fragments;

import org.panaggelica.media.Fragment;
import utils.media.ContentFragment;
import utils.media.ContentFragmentDescription;

import java.util.Arrays;
import java.util.List;

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
    private static final String prefixPattern = "<div class='full-width %s'>";  // color goes here
    private static final String gridPostfix = "</div>";
    private static final String postfix = "</div>";
    public static final String TEXTSIZE = "textsize";
    public static final String FULLWIDTH = "fullwidth";
    public static final String MAINFULLWIDTH = "mainfullwidth";
    public static final String TEXTSIZEPLUS = "textsizeplus";
    private String prfx = "", pstfx = "", imgClass = "";
    private static final String imagePattern = "<img class='ui %s image' src='%s' />";
    //    private Integer colN = null, colM = null;
    private String url;
    private String imageStyle = null;
    private String background = null;
//    private String length;

    private static final List<String> majorOptions = Arrays.asList(TEXTSIZE, TEXTSIZEPLUS, MAINFULLWIDTH, FULLWIDTH);
    private static final String GREY = "grey";
    private static final String WHITE = "white";
    private static final List<String> colorOptions = Arrays.asList(GREY, WHITE);
    private String[] options;

    public ImageFragment(String[] options, String content)
    {
        this.options = options;
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
                        imageStyle = "fit-main-text";
                        pstfx = gridPostfix + postfix;
                        break;
                    case TEXTSIZEPLUS:
                        prfx = gridPrefix + prefixPattern;
                        needPrefixAdjustment = true;
                        imageStyle = "fit-main-text-plus";
                        pstfx = gridPostfix + postfix;
                        break;
                    case MAINFULLWIDTH :
                        prfx = gridPrefix + prefixPattern;
                        needPrefixAdjustment = true;
                        pstfx = gridPostfix + postfix;
                        break;
                    case FULLWIDTH :
                        prfx = prefixPattern;
                        needPrefixAdjustment = true;
                        pstfx = postfix;
                }
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
            prfx = String.format(prfx, (background == null)? "" : "content-" + background);

    }

    public ImageFragment()
    {
    }

    @Override
    public String render()
    {
        return prfx + String.format(imagePattern, (imageStyle == null ? "" : imageStyle), url) + pstfx;
    }

    @Override
    public ContentFragmentDescription getDescription()
    {
        return null;
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

}
