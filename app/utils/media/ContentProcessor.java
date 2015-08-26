package utils.media;

import org.reflections.Reflections;
import utils.media.fragments.UnformattedFragment;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 16:30
 */
public class ContentProcessor
{

    private static final LinkedHashSet<Class<ContentFragment>> registeredFragmentClasses = new LinkedHashSet<>();
    private static final LinkedHashSet<ContentFragment> registeredFragments = new LinkedHashSet<>();
    private static final LinkedHashSet<ContentFragmentDescription> registeredDescriptions = new LinkedHashSet<>();

    private static final String FRAGMENT = "((\\[([\\w+\\s]+)\\])(.+)(\\[\\/(\\w+)\\]))";
    private static final Pattern FRAGMENT_REGEX = Pattern.compile(FRAGMENT);

    static {
        init();
    }

    private static void init()
    {
        Reflections reflections = new Reflections("utils.media.fragments");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(org.panaggelica.media.Fragment.class);

        for (Class<?> clazz : classes) {
            Class cl = clazz;
            registeredFragmentClasses.add(cl);
            try {
                ContentFragment fragment = (ContentFragment) cl.newInstance();
                registeredFragments.add(fragment);
                System.out.println("Registered fragment: " + clazz + " [" + fragment.getTag() + "]");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to register: " + clazz);
            }
        }

        Set<Class<?>> descClasses = reflections.getTypesAnnotatedWith(org.panaggelica.media.FragmentDescription.class);

        for (Class<?> clazz : descClasses) {
            Class cl = clazz;
            try {
                registeredDescriptions.add((ContentFragmentDescription) cl.newInstance());
                System.out.println("Registered fragment description: " + clazz);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to register description: " + clazz);
            }
        }
    }

    public static Set<ContentFragmentDescription> getDescriptions()
    {
        return registeredDescriptions;
    }

    public static List<ContentFragment> parse(String text) throws ContentProcessorException
    {
        List<ContentFragment> fragments = new ArrayList<>();
        // 1. extract fragments && instantiate it
        // grasp all fragments and assume that all text not in fragments is unformatted (main content)
        Matcher matcher = FRAGMENT_REGEX.matcher(text);
        int idx = 0;
//        while (matcher.find(idx)) {
        while (matcher.find()) {
            String unformatted = text.substring(idx, matcher.start());
            if (!"".equals(unformatted)) {
                unformatted = unformatted.trim();
                if (!"".equals(unformatted))
                    fragments.add(new UnformattedFragment(unformatted));
            }
            System.out.println("unformatted (" + idx + ")= " + unformatted);
            idx = matcher.end();
//            System.out.println("idx = " + idx);
            String found = matcher.group();
            // instantiate fragment based on found
            System.out.println("found = " + found);
            String[] tagWithOptions = matcher.group(3).split(" ");
            String tag = tagWithOptions[0];
            String[] options = Arrays.copyOfRange(tagWithOptions, 1, tagWithOptions.length);
            String content = matcher.group(4);
            String closingTag = matcher.group(6);
            if (!tag.equalsIgnoreCase(closingTag))
                throw new ContentProcessorException("tags mismatch: [" + tag + "] vs [/" + closingTag + "]");
            ContentFragment cf = null;
            for (ContentFragment frag : registeredFragments)
            {
                if (frag.accepts(tag))
                {
                    cf = frag.newFragment(options, content);
                }
            }
            if (cf == null)
                throw new ContentProcessorException("Unknown tag [" + tag  + "]");
            fragments.add(cf);
        }
        return fragments;
    }
}
