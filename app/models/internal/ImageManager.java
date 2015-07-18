package models.internal;

import models.Image;

import static utils.HibernateUtils.getSession;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 18.07.2015
 * Time: 15:49
 */
public class ImageManager
{
    public static Image getById(long id)
    {
        Image image = (Image) getSession().get(Image.class, id);
        return image;
    }
}
