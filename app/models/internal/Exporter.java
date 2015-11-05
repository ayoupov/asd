package models.internal;

import models.Church;
import models.MediaContent;
import models.MediaContentType;
import play.libs.Json;
import utils.ServerProperties;
import utils.serialize.Serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 05.11.2015
 * Time: 14:16
 */
public class Exporter
{
    public static String seedDir = ServerProperties.getValue("asd.seed.data.folder");

    public static class ListUtils
    {
        static public <E> String mkString(Iterable<E> iterable, Function<E, String> stringify, String delimiter)
        {
            int i = 0;
            StringBuilder s = new StringBuilder();
            for (E e : iterable) {
                if (i != 0) {
                    s.append(delimiter);
                }
                s.append(stringify.apply(e));
                i++;
            }
            return s.toString();
        }
    }

    public static void exportMediaContent() throws IOException
    {
        beginTransaction();
        List<MediaContent> articles = ContentManager.getMediaContent(MediaContentType.Article);
        String typeDir = "articles/";
        processMedia(articles, typeDir);
        commitTransaction();
        beginTransaction();
        List<MediaContent> stories = ContentManager.getMediaContent(MediaContentType.Story);
        processMedia(stories, "stories/");
        commitTransaction();
    }

    public static void processMedia(List<MediaContent> media, String typeDir) throws IOException
    {
        for (MediaContent a : media) {
            File dir = new File(seedDir + typeDir + a.getId());
            dir.mkdirs();
//            writeFile("text.txt", dir, a.getText());
            Json.setObjectMapper(Serializer.emptyMapper);
            writeFile("entity.json", dir, Json.toJson(a).toString());
            writeFile("dedicated.church", dir, a.getDedicatedChurch() == null ? "" : a.getDedicatedChurch().extID);
            Set<Church> churches = a.getChurches();
            writeFile("all.churches", dir, churches == null ? "" : ListUtils.mkString(churches, Church::getExtID, ", "));
        }
    }

    private static void writeFile(String filename, File dir, String content) throws IOException
    {
        File out = new File(dir, filename);
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(out), "UTF-8");
        osw.write(content);
        osw.close();
    }

}
