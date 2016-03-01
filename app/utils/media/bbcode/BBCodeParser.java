package utils.media.bbcode;

import org.reflections.Reflections;
import play.Logger;
import utils.media.bbcode.substitutes.Substitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.SubstitutePriority;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.ServerProperties.isInProduction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.09.2015
 * Time: 23:16
 */
public class BBCodeParser
{

//    private static Set<Substitute> substitutes = new HashSet<>();

    private static List<SubstitutePriority> order = new ArrayList<>();
    private static Map<SubstitutePriority, Set<Substitute>> prioritizedSubstitutes = new HashMap<>();

    static {
        order.add(SubstitutePriority.ULTRA_HIGH);
        order.add(SubstitutePriority.HIGH);
        order.add(SubstitutePriority.MEDIUM);
        order.add(SubstitutePriority.LOW);
        init();
    }

    private static void init()
    {
        Reflections reflections = new Reflections("utils.media.bbcode");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SubstituteAnnotation.class);

        for (Class<?> clazz : classes) {
            try {
                Substitute substitute = (Substitute) clazz.newInstance();
                SubstitutePriority priority = substitute.getPriority();
                Set<Substitute> subs = prioritizedSubstitutes.get(priority);
                if (subs == null)
                    subs = new HashSet<Substitute>();
                subs.add(substitute);
                prioritizedSubstitutes.put(priority, subs);
//                System.out.println("Registered substitute: " + clazz + " [" + substitute.getTag() + "]");
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("Failed to register: " + clazz);
            }
        }
        // debug print
        for (SubstitutePriority priority : order) {
            Set<String> tags = prioritizedSubstitutes.get(priority).stream().map(Substitute::getTag).collect(Collectors.toSet());
            Logger.info("Registered bbtags " + priority + " : " + tags);
        }
    }

    public static String parse(String input)
    {
        String result = input;
        BBCodeRenderState state = new BBCodeRenderState();
        for (SubstitutePriority priority : order) {
            Set<Substitute> substitutes = prioritizedSubstitutes.get(priority);
            for (Substitute substitute : substitutes) {
                if (!isInProduction())
                    Logger.debug("substitute = " + substitute.getTag());
                Pattern pattern = substitute.getPattern();
                String replacement = substitute.getReplacement();
                Matcher matcher = pattern.matcher(result);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    String found = matcher.group().trim();
                    if (!"".equals(found) && !"\n".equals(found) && !isInProduction())
//                        Logger.debug("Processing: " + found);
                        Logger.info("Processing: " + found);
                    if (!substitute.isSimple())
                        replacement = substitute.process(sb, matcher, state);
                    matcher.appendReplacement(sb, replacement);
                }
                matcher.appendTail(sb);
                result = sb.toString();
            }
        }
        applyState(result, state);
        return result;
    }

    private static void applyState(String result, BBCodeRenderState state)
    {

    }


    public static class BBCodeRenderState
    {
        private int currentId = 0;

        public int nextId()
        {
            currentId++;
            return currentId;
        }
    }
}
