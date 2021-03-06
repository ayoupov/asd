package utils.seed;

import models.Church;
import models.Image;
import models.internal.ContentManager;
import models.user.User;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import utils.ServerProperties;
import utils.media.images.Thumber;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static models.internal.UserManager.getAutoUser;
import static org.apache.commons.io.FileUtils.copyFile;
import static utils.HibernateUtils.saveOrUpdate;
import static utils.map.BadIdsSieve.beautify;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 21.10.2015
 * Time: 19:03
 */
public class ChurchImageSeeds
{
    private static FilenameFilter txtFilenameFilter = (dir, name) -> name.endsWith(".txt");

    public static void seedChurchImages(File dir) throws IOException
    {
        int dirCount = 0;
        int churchSuccess = 0;
        int fileFail = 0;
        int churchCount = 0;
        User user = getAutoUser();
        File[] dirs = dir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        for (File churchDir : dirs) {
            dirCount++;
            String extID = beautify(churchDir.getName());
            Church church = ContentManager.getChurch(extID);
            if (church != null) {
                Set<String> processedFiles = new HashSet<>();
                List<Image> churchImages = church.getImages();
                if (churchImages == null)
                    churchImages = new ArrayList<>();
                int imageCount = churchImages.size();
                int thisChurchFileFails = 0;
                churchCount++;
                // instead of description.txt, take first .txt file
                File description = takeFirstTxt(churchDir);
                if (description != null) {
                    String line;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(description), "UTF-8"))) {
                        while ((line = br.readLine()) != null) {
                            try {
                                if ("".equals(line.trim()))
                                    continue;
                                String[] split = line.split(",", 2);
                                File imageFile = new File(churchDir, split[0]);
                                if (!imageFile.exists() || imageFile.isDirectory())
                                    throw new Exception("no file found: " + imageFile);
                                String desc = "";
                                if (split.length > 1)
                                    desc = split[1];
                                churchImages.add(processImage(user, church, imageFile, desc));
                                processedFiles.add(imageFile.getName());
                            } catch (Exception e) {
                                Logger.error("while parsing: ", e);
                                thisChurchFileFails++;
                            }
                        }
                    }
                } else Logger.warn("No description file found on " + extID);
                File[] images = churchDir.listFiles(JpegFileFilter.instance());
                for (File image : images)
                {
                    if (!processedFiles.contains(image.getName())) {
                        churchImages.add(processImage(user, church, image, null));
                        processedFiles.add(image.getName());
                    }
                }
                if (churchImages.size() > imageCount) {
                    church.setImages(churchImages);
                    saveOrUpdate(church);
                }
                if (thisChurchFileFails == 0)
                    churchSuccess++;
                Logger.info(String.format("%s : added %d images", extID, processedFiles.size()));
            } else
            {
                Logger.error("Church not found: " + extID);
            }
        }
        Logger.info("Seeded images: ");
        Logger.info(String.format("Dirs/Churches/ChurchSuccess/FileFails: %d/%d/%d/%d", dirCount, churchCount, churchSuccess, fileFail));
    }

    public static Image processImage(User user, Church church, File imageFile, String desc) throws IOException
    {
        Pair<File, String> whereFileAndImagePath = createPathAndCopy(imageFile, church.getExtID());
        Image image = new Image(whereFileAndImagePath.getRight(), user, desc == null ? "" : desc);
        Thumber.rethumb(whereFileAndImagePath.getLeft(), Thumber.ThumbType.EDITORIAL);
        return image;
    }

    private static File takeFirstTxt(File churchDir)
    {
        File[] files = churchDir.listFiles(txtFilenameFilter);
        if (files != null && files.length > 0)
            return files[0];
        return null;
    }

    private static String userHash = User.anonymousHash();

    public static String where()
    {
        return ServerProperties.getValue("asd.upload.path") + "/" + userHash;
    }

    public static String webWhere()
    {
        return ServerProperties.getValue("asd.upload.relative.path") + "/" + userHash;
    }

    private static Pair<File, String> createPathAndCopy(File imageFile, String extID) throws IOException
    {
        String updatedFilename = extID + "_" + imageFile.getName().replace(' ','_');
        File destFile = new File(where() + "/" + updatedFilename);
        copyFile(imageFile, destFile);
        return Pair.of(destFile, webWhere() + "/" + updatedFilename);
    }

    private static class JpegFileFilter implements FilenameFilter
    {
        private static JpegFileFilter instance = new JpegFileFilter();

        public static FilenameFilter instance()
        {
            return instance;
        }

        @Override
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".jpg");
        }
    }
}
