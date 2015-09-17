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
            int thumbWidthMax = 215;
            int thumbHeightMax = 215;
            ResampleOp resampleOp;

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();
            float imageRatio = ((float)imageHeight) / ((float) imageWidth);
            if (imageRatio < 1.0) // case height is less than width
                resampleOp = new ResampleOp(thumbWidthMax, (int) (thumbHeightMax * imageRatio));
            else
            // 200 / imageWidth = scaledratio
                resampleOp = new ResampleOp((int) (thumbWidthMax / imageRatio), thumbHeightMax);
            BufferedImage thumbImage = resampleOp.filter(image, null);
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
