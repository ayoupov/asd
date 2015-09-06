package utils.media.bbcode;

import org.reflections.Reflections;
import utils.media.bbcode.substitutes.Substitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.09.2015
 * Time: 23:16
 */
public class BBCodeParser
{

    private static Set<Substitute> substitutes = new HashSet<>();

    static {
        init();
    }

    private static void init()
    {
        Reflections reflections = new Reflections("utils.media.bbcode");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SubstituteAnnotation.class);

        for (Class<?> clazz : classes) {
            try {
                Substitute substitute = (Substitute) clazz.newInstance();
                substitutes.add(substitute);
                substitute.getTag();
                System.out.println("Registered substitute: " + clazz + " [" + substitute.getTag() + "]");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to register: " + clazz);
            }
        }

    }

    public static String parse(String input)
    {
        String result = input;
        BBCodeRenderState state = new BBCodeRenderState();
        for (Substitute substitute : substitutes) {
            System.out.println("substitute = " + substitute.getTag());
            Pattern pattern = substitute.getPattern();
            String replacement = substitute.getReplacement();
            Matcher matcher = pattern.matcher(result);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String found = matcher.group().trim();
                if (!"".equals(found) && !"\n".equals(found))
                    System.out.println("Processing: " + found);
                if (!substitute.isSimple())
                    replacement = substitute.process(sb, matcher, state);
                matcher.appendReplacement(sb, replacement);
            }
            matcher.appendTail(sb);
            result = sb.toString();
        }
        applyState(result, state);
        return result;
    }

    private static void applyState(String result, BBCodeRenderState state)
    {

    }


    public static class BBCodeRenderState
    {
    }
}
