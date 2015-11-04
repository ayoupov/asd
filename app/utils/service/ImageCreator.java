package utils.service;

import models.Image;
import models.user.User;
import play.Logger;
import utils.ServerProperties;
import utils.media.images.Thumber;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.11.2015
 * Time: 20:15
 */
public class ImageCreator
{

    public static Image createImageFromUpload(User user, File file, String filename, String description)
    {
        String where = ServerProperties.getValue("asd.upload.path") + "/" + user.getHash();
        String webWhere = ServerProperties.getValue("asd.upload.relative.path") + "/" + user.getHash();
        String path = where + "/" + filename;
        String webPath = webWhere + "/" + filename;
        File outFile = new File(path);
        File parent = new File(where);
        if (!parent.exists())
            parent.mkdirs();
        if (outFile.exists())
            outFile.delete();
        if (file.renameTo(outFile)) {
            Thumber.rethumb(outFile, Thumber.ThumbType.EDITORIAL, Thumber.ThumbType.HOVER, Thumber.ThumbType.ISOTOPE);
            boolean setReadableSuccess = outFile.setReadable(true, false);
            System.out.println("creator: description = " + description);
            System.out.println("creator: webPath = " + webPath);
            Image image = new Image(description, webPath);
            image.setUploadedBy(user);
            return image;
        } else {
            String warning = "didn't move file " + filename + " to " + outFile;
            Logger.warn(warning);
            return null;
        }
    }

}
