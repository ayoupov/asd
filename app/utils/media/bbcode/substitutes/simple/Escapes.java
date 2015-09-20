package utils.media.bbcode.substitutes.simple;

import utils.media.bbcode.substitutes.AbstractSimpleSubstitute;
import utils.media.bbcode.substitutes.SubstituteAnnotation;
import utils.media.bbcode.substitutes.SubstitutePriority;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 04.09.2015
 * Time: 1:49
 */

public class Escapes
{

    @SubstituteAnnotation
    public static class CharQuote extends AbstractSimpleSubstitute
    {

        private Pattern pattern = Pattern.compile("\\\"");
        private String replacement = "&quot;";

        @Override
        public String getTag()
        {
            return "\"";
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

        @Override
        public SubstitutePriority getPriority()
        {
            return SubstitutePriority.HIGH;
        }
    }

    @SubstituteAnnotation
    public static class CharGt extends AbstractSimpleSubstitute
    {

        private Pattern pattern = Pattern.compile(">");
        private String replacement = "&gt;";

        @Override
        public String getTag()
        {
            return ">";
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

        @Override
        public SubstitutePriority getPriority()
        {
            return SubstitutePriority.HIGH;
        }
    }
    @SubstituteAnnotation
    public static class CharLt extends AbstractSimpleSubstitute
    {

        private Pattern pattern = Pattern.compile("<");
        private String replacement = "&lt;";

        @Override
        public String getTag()
        {
            return "<";
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

        @Override
        public SubstitutePriority getPriority()
        {
            return SubstitutePriority.HIGH;
        }
    }
//    @SubstituteAnnotation
//    public static class CharAmp extends AbstractSimpleSubstitute
//    {
//
//        private Pattern pattern = Pattern.compile("&");
//        private String replacement = "&amp;";
//
//        @Override
//        public String getTag()
//        {
//            return "&";
//        }
//
//        @Override
//        public Pattern getPattern()
//        {
//            return pattern;
//        }
//
//        @Override
//        public String getReplacement()
//        {
//            return replacement;
//        }
//
//        @Override
//        public SubstitutePriority getPriority()
//        {
//            return SubstitutePriority.HIGH;
//        }
//    }
}
