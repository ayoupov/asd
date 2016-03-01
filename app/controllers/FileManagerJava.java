package controllers;

import models.Image;
import models.MediaContent;
import models.internal.ContentManager;
import models.internal.UserManager;
import models.user.User;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTimeComparator;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.ServerProperties;
import utils.media.images.Thumber;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

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
            Arrays.sort(files, FileTimestampComparator.getInstance());
            for (File file : files) {
                Map<String, String> fileMap = new HashMap<>();
                fileMap.put("path", outpath + "/" + file.getName());
                fileMap.put("display", file.getName());
                fileMap.put("lm", "" + file.lastModified());
                String thumbName = Thumber.thumbName(file, Thumber.ThumbType.EDITORIAL);
                File thumbFile = new File(thumbName);
                if (thumbFile.exists())
                    fileMap.put("thumb", outpath + "/" + thumbFile.getName());
                fileArr.add(fileMap);
            }
        }
        return ok(Json.toJson(fileArr));
    }

    public static Result listStory(long id)
    {
        Set<Map<String, String>> fileArr = new LinkedHashSet<>();
        beginTransaction();
//        User admin = UserManager.getLocalUser(session());
        MediaContent mc = ContentManager.getMediaByIdAndAlternative(id + "");
        if (mc != null && mc.getDedicatedChurch() != null) {
            User author = mc.getAuthors().iterator().next();

            List<Image> churchImages = mc.getDedicatedChurch().getImages();
            List<File> churchFiles = new ArrayList<>();
            if (churchImages != null)
                churchImages.stream().filter(i -> i.getApprovedTS() != null).map(i -> putInMap(fileArr, i));
            String authorHash = author.getHash();
            commitTransaction();
            String origpath = FilenameUtils.normalize(ServerProperties.getValue("asd.upload.path") + "/" + authorHash);
            String outpath = ServerProperties.getValue("asd.upload.relative.path") + "/" + authorHash;
            File[] files = new File(origpath).listFiles(originalFileFilter);


            if (files != null && files.length > 0) {
                Arrays.sort(files, FileTimestampComparator.getInstance());
                for (File file : files) {
                    Map<String, String> fileMap = new HashMap<>();
                    fileMap.put("path", outpath + "/" + file.getName());
                    fileMap.put("display", file.getName());
                    fileMap.put("lm", "" + file.lastModified());
                    String thumbName = Thumber.thumbName(file, Thumber.ThumbType.EDITORIAL);
                    File thumbFile = new File(thumbName);
                    if (thumbFile.exists())
                        fileMap.put("thumb", outpath + "/" + thumbFile.getName());
                    fileArr.add(fileMap);
                }
            }
            return ok(Json.toJson(fileArr));
        } else
            return notFound();
    }

    private static Void putInMap(Set<Map<String, String>> fileArr, Image i)
    {
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("path", i.getPath());
        String[] split = i.getPath().split("/");
        fileMap.put("display", split[split.length - 1]);
        fileMap.put("lm", i.getUploadedTS().getTime() + "");
        fileMap.put("desc", i.getDescription());
        fileArr.add(fileMap);
        return null;
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

    private static class FileTimestampComparator implements Comparator<File>
    {
        public static Comparator<? super File> getInstance()
        {
            return new FileTimestampComparator();
        }

        @Override
        public int compare(File o1, File o2)
        {
            return (int) (o2.lastModified() - o1.lastModified());
        }

        @Override
        public Comparator<File> reversed()
        {
            return null;
        }

        @Override
        public Comparator<File> thenComparing(Comparator<? super File> other)
        {
            return null;
        }

        @Override
        public <U> Comparator<File> thenComparing(Function<? super File, ? extends U> keyExtractor, Comparator<? super U> keyComparator)
        {
            return null;
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<File> thenComparing(Function<? super File, ? extends U> keyExtractor)
        {
            return null;
        }

        @Override
        public Comparator<File> thenComparingInt(ToIntFunction<? super File> keyExtractor)
        {
            return null;
        }

        @Override
        public Comparator<File> thenComparingLong(ToLongFunction<? super File> keyExtractor)
        {
            return null;
        }

        @Override
        public Comparator<File> thenComparingDouble(ToDoubleFunction<? super File> keyExtractor)
        {
            return null;
        }
    }
}


