package utils.media.fragments;

import org.apache.commons.lang3.tuple.Pair;
import org.panaggelica.media.FragmentDescription;
import utils.media.ContentFragmentDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 19:42
 */
@FragmentDescription
public class ImageFragmentDescription extends ContentFragmentDescription
{
    private static final List<Pair<String, String>> options = new ArrayList<>();

    static {
        options.add(Pair.of(ImageFragment.TEXTSIZE, "fits image to main text (2-6 col)"));
        options.add(Pair.of(ImageFragment.TEXTSIZEPLUS, "fits image to wider than main text (2-7 col)"));
        options.add(Pair.of(ImageFragment.MAINFULLWIDTH, "fits image to grid (1-8 col)"));
//        options.add(Pair.of("NtoM", "fits image from column N to M (n-m col)"));
        options.add(Pair.of(ImageFragment.FULLWIDTH, "fits image to window width"));

        options.add(Pair.of("content-COLOR", "optional to set color of image background (grey and white only)"));
    }

    @Override
    public String getTag()
    {
        return "image";
    }

    @Override
    public List<Pair<String, String>> getOptions()
    {
        return options;
    }
}
