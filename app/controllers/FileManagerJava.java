package controllers;

import models.internal.UserManager;
import models.user.User;
import org.apache.commons.io.FilenameUtils;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.ServerProperties;
import utils.media.images.Thumber;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 27.08.2015
 * Time: 14:11
 */
@Security.Authenticated(Secured.class)
public class FileManagerJava extends Controller
{

    private static final FilenameFilter originalFileFilter = new OriginalFileFilter();

    public static Result list()
    {
        beginTransaction();
        User user = UserManager.getLocalUser(session());
        String userHash;
        if (user != null)
            userHash = user.getHash();
        else userHash = User.anonymousHash();
        commitTransaction();
        String origpath = FilenameUtils.normalize(ServerProperties.getValue("asd.upload.path") + "/" + userHash);
        String outpath = ServerProperties.getValue("asd.upload.relative.path") + "/" + userHash;
        File[] files = new File(origpath).listFiles(originalFileFilter);
        Set<Map<String, String>> fileArr = new LinkedHashSet<>();
        if (files != null && files.length > 0) {
            for (File file : files) {
                Map<String, String> fileMap = new HashMap<>();
                fileMap.put("path", outpath + "/" + file.getName());
                String thumbName = Thumber.thumbName(file, Thumber.ThumbType.EDITORIAL);
                File thumbFile = new File(thumbName);
                if (thumbFile.exists())
                    fileMap.put("thumb", outpath + "/" + thumbFile.getName());
                fileArr.add(fileMap);
            }
        }
        return ok(Json.toJson(fileArr));
    }

    static class OriginalFileFilter implements FilenameFilter
    {

        @Override
        public boolean accept(File dir, String name)
        {
            boolean accept = true;
            for (String ends : Thumber.THUMB_ENDS) {
                accept &= !name.contains(ends +".");
            }
            return accept;
        }
    }
}


