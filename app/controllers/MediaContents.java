package controllers;

import models.Church;
import models.MediaContent;
import models.MediaContentType;
import play.mvc.Controller;
import play.mvc.Result;

import static utils.HibernateUtils.get;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 14.07.2015
 * Time: 22:08
 */
public class MediaContents extends Controller
{

    public static Result byTypeAndId(MediaContentType ctype, Long id)
    {
        MediaContent content = (MediaContent) get(MediaContent.class, id);
        if (content != null)
            return ok("Got request " + request() + "!" + "Found: " + content);
        else
            return notFound(String.format("MediaContent with id {%s}", id));
    }

}
