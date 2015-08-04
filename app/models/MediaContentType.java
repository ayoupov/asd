package models;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:55
 */
public enum MediaContentType
{
    Article,
    Story;

    public static MediaContentType fromString(String ctype)
    {
        if ("article".equalsIgnoreCase(ctype))
            return Article;
        if ("story".equalsIgnoreCase(ctype))
            return Story;
        return null;
    }
}
