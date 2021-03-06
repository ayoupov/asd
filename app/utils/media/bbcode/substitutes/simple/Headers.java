package utils.media.bbcode.substitutes.simple;

import utils.media.bbcode.substitutes.AbstractSimpleSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:49
 */

public class Headers
{

    @SubstituteAnnotation
    public static class H1 extends AbstractSimpleSubstitute
    {

        private Pattern pattern = Pattern.compile("\\[header](.+?)\\[\\/header]", Pattern.DOTALL);
        private String replacement = "<h1>$1</h1>";

        @Override
        public String getTag()
        {
            return "header";
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
    public static class H2 extends AbstractSimpleSubstitute
    {

        private Pattern pattern = Pattern.compile("\\[header2](.+?)\\[\\/header2]", Pattern.DOTALL);
        private String replacement = "<h2>$1</h2>";

        @Override
        public String getTag()
        {
            return "header2";
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
    public static class H3 extends AbstractSimpleSubstitute
    {

        private Pattern pattern = Pattern.compile("\\[header3](.+?)\\[\\/header3]", Pattern.DOTALL);
        private String replacement = "<h3>$1</h3>";

        @Override
        public String getTag()
        {
            return "header3";
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
