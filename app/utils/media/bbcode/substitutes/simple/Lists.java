package utils.media.bbcode.substitutes.simple;

import utils.media.bbcode.substitutes.AbstractSimpleSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 2:19
 */
public class Lists
{
    /**
     * Created with IntelliJ IDEA.
     * User: ayoupov
     * Date: 04.09.2015
     * Time: 1:49
     */
    @SubstituteAnnotation
    public static class OLN extends AbstractSimpleSubstitute
    {
        private static Pattern pattern = Pattern.compile("\\[list=1](.+?)\\[\\/list]", Pattern.DOTALL);
        private String replacement = "<ol>$1</ol>";

        @Override
        public String getTag()
        {
            return "list=1";
        }

        @Override
        public Pattern getPattern()
        {
            return pattern;
        }

        @Override
        public String getReplacement()
        {
            return replacement;
        }
    }
    @SubstituteAnnotation
    public static class OLA extends AbstractSimpleSubstitute
    {
        private static Pattern pattern = Pattern.compile("\\[list=a](.+?)\\[\\/list]", Pattern.DOTALL);
        private String replacement = "<ol type='a'>$1</ol>";

        @Override
        public String getTag()
        {
            return "list=a";
        }

        @Override
        public Pattern getPattern()
        {
            return pattern;
        }

        @Override
        public String getReplacement()
        {
            return replacement;
        }
    }
    @SubstituteAnnotation
    public static class UL extends AbstractSimpleSubstitute
    {
        private static Pattern pattern = Pattern.compile("\\[list](.+?)\\[\\/list]", Pattern.DOTALL);
        private String replacement = "<ul>$1</ul>";

        @Override
        public String getTag()
        {
            return "list";
        }

        @Override
        public Pattern getPattern()
        {
            return pattern;
        }

        @Override
        public String getReplacement()
        {
            return replacement;
        }
    } @SubstituteAnnotation
    public static class LI extends AbstractSimpleSubstitute
    {
        private static Pattern pattern = Pattern.compile("\\[item](.+?)\\[\\/item]", Pattern.DOTALL);
        private String replacement = "<li>$1</li>";

        @Override
        public String getTag()
        {
            return "item";
        }

        @Override
        public Pattern getPattern()
        {
            return pattern;
        }

        @Override
        public String getReplacement()
        {
            return replacement;
        }
    }
}
