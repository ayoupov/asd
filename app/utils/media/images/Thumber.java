package utils.media.images;

import com.mortennobel.imagescaling.ResampleOp;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 27.08.2015
 * Time: 12:56
 */
public class Thumber
{
    private static final ResampleOp resample200x150 = new ResampleOp(200,150);

    public static String thumbName(File file)
    {
        String fileName = file.getName();
        String name = FilenameUtils.getBaseName(fileName) + "_thumb";
        String ext = FilenameUtils.getExtension(fileName);
        String path = FilenameUtils.getFullPath(file.getAbsolutePath());
        return path + "/" + name + "." + ext;
    }

    public static void rethumb(File file)
    {
        try {
            BufferedImage image = ImageIO.read(file);
            System.out.println("In file: " + file);
            BufferedImage thumbImage = resample200x150.filter(image, null);
            File output = new File(thumbName(file));
            if (ImageIO.write(thumbImage, "png", output))
                System.out.println("Thumb written: " + output);
            else
                System.out.println("Failed to write: " + output);
        } catch (Exception e) {
            System.out.println("error:" + e.getMessage());
        }
    }
}
