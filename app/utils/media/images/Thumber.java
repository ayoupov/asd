package utils.media.images;

import com.mortennobel.imagescaling.ResampleOp;
import controllers.MediaContents;
import org.apache.commons.io.FilenameUtils;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 27.08.2015
 * Time: 12:56
 */
public class Thumber
{

    public static final String THUMB_ED = "_thumb_ed";
    public static final String THUMB_IS = "_thumb_is";
    public static final String THUMB_HV = "_thumb_hv";

    public static Set<String> THUMB_ENDS = new HashSet<>();

    static {
        THUMB_ENDS.add(THUMB_ED);
        THUMB_ENDS.add(THUMB_IS);
        THUMB_ENDS.add(THUMB_HV);
    }

    public enum ThumbType
    {
        EDITORIAL, ISOTOPE, HOVER
    }

    public static String thumbNameWeb(File file, ThumbType type)
    {
        String fileName = file.getName();
        String name = FilenameUtils.getBaseName(fileName);
        switch (type) {
            case EDITORIAL:
                name += THUMB_ED;
                break;
            case ISOTOPE:
                name += THUMB_IS;
                break;
            case HOVER:
                name += THUMB_HV;
                break;
        }
        String ext = FilenameUtils.getExtension(fileName);
        String path = file.getParentFile().getName();
        return MediaContents.relativeUploadPath + "/" + path + "/" + name + "." + ext;
    }

    public static String thumbName(File file, ThumbType type)
    {
        System.out.println("file = " + file);
        String fileName = file.getName();
        String name = FilenameUtils.getBaseName(fileName);
        switch (type) {
            case EDITORIAL:
                name += THUMB_ED;
                break;
            case ISOTOPE:
                name += THUMB_IS;
                break;
            case HOVER:
                name += THUMB_HV;
                break;
        }
        String ext = FilenameUtils.getExtension(fileName);
        String path = file.getParentFile().getPath();
        return path + "/" + name + "." + ext;
    }

    public static void rethumb(File file)
    {
        try {
            BufferedImage image = ImageIO.read(file);
            Logger.info("In file: " + file);

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();
            float imageRatio = ((float) imageHeight) / ((float) imageWidth);

            editorialThumb(file, image, imageRatio);

            isotopeThumb(file, image, imageWidth, imageHeight);

            hoverThumb(file, image, imageWidth, imageHeight);

        } catch (Exception e) {
            System.out.println("error:" + e.getMessage());
        }
    }

    private static void editorialThumb(File file, BufferedImage image, float imageRatio) throws IOException
    {
        int thumbWidthMax = 215;
        int thumbHeightMax = 215;
        ResampleOp resampleOp;

        if (imageRatio < 1.0) // case height is less than width
            resampleOp = new ResampleOp(thumbWidthMax, (int) (thumbHeightMax * imageRatio));
        else
            resampleOp = new ResampleOp((int) (thumbWidthMax / imageRatio), thumbHeightMax);
        BufferedImage thumbImage = resampleOp.filter(image, null);
        File output = new File(thumbName(file, ThumbType.EDITORIAL));
        if (ImageIO.write(thumbImage, "png", output))
            Logger.info("Thumb written: ", output);
        else
            Logger.error("Failed to write: ", output);

    }

    private static void isotopeThumb(File file, BufferedImage image, int imageWidth, int imageHeight) throws IOException
    {
        float thumbHeightMax = 135;
        ResampleOp resampleOp;

        float imageRatio = thumbHeightMax / imageHeight;
        int destWidth = (int) (imageWidth * imageRatio);
        resampleOp = new ResampleOp(destWidth, (int) thumbHeightMax);
        BufferedImage thumbImage = resampleOp.filter(image, null);
        File output = new File(thumbName(file, ThumbType.ISOTOPE));
        if (ImageIO.write(thumbImage, "png", output))
            Logger.info("Thumb written: ", ThumbType.ISOTOPE, output, destWidth, thumbHeightMax);
        else
            Logger.error("Failed to write: ", ThumbType.ISOTOPE, output, destWidth, thumbHeightMax);

    }

    private static void hoverThumb(File file, BufferedImage image, int imageWidth, int imageHeight) throws IOException
    {
        float thumbHeightMax = 330;
        ResampleOp resampleOp;

        float imageRatio = thumbHeightMax / imageHeight;
        int destWidth = (int) (imageWidth * imageRatio);
        resampleOp = new ResampleOp(destWidth, (int) thumbHeightMax);
        BufferedImage thumbImage = resampleOp.filter(image, null);
        File output = new File(thumbName(file, ThumbType.HOVER));
        if (ImageIO.write(thumbImage, "png", output))
            Logger.info("Thumb written: ", ThumbType.HOVER, output, destWidth, thumbHeightMax);
        else
            Logger.error("Failed to write: ", ThumbType.HOVER, output, destWidth, thumbHeightMax);

    }
}
