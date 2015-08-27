package controllers;

import org.apache.commons.io.FilenameUtils;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ServerProperties;
import utils.media.images.Thumber;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 27.08.2015
 * Time: 14:11
 */
public class FileManagerJava extends Controller
{

    private static final FilenameFilter originalFileFilter = new OriginalFileFilter();

    public static Result list(String path)
    {
        // possible listing of system files ???
        String origpath = FilenameUtils.normalize(ServerProperties.getValue("asd.upload.path") + path);
        String outpath = ServerProperties.getValue("asd.upload.relative.path") + path;
        File[] files = new File(origpath).listFiles(originalFileFilter);
        Set<Map<String, String>> fileArr = new LinkedHashSet<>();
        if (files != null && files.length > 0) {
            for (File file : files) {
                Map<String, String> fileMap = new HashMap<>();
                fileMap.put("path", outpath + "/" + file.getName());
                String thumbName = Thumber.thumbName(file);
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
            return !name.contains("_thumb.");
        }
    }
}


