package utils.lang;

import java.text.Collator;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 15:09
 */
public class PolishSupport
{
    static Collator collator = Collator.getInstance(Locale.forLanguageTag("pl"));

    static {
        collator.setStrength(Collator.SECONDARY);
    }

    public static boolean similar(String s1, String s2)
    {
        String t1 = strip(s1);
        String t2 = strip(s2);
        return collator.compare(t1, t2) == 0;
    }

    public static String strip(String s)
    {
        return s.replaceAll("\\s", "");
    }
}
