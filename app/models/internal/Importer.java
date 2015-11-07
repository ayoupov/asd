package models.internal;

import models.Church;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.user.User;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import play.Logger;
import play.libs.Json;
import utils.ServerProperties;
import utils.serialize.Serializer;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static utils.HibernateUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 05.11.2015
 * Time: 15:24
 */
public class Importer
{
    public static String seedDir = ServerProperties.getValue("asd.seed.data.folder");

    public static void importMediaContent() throws IOException
    {
        File articlesDir = new File(seedDir, "articles");
        MediaContentType type = MediaContentType.Article;
        Logger.info("Looking for articles: " + articlesDir);
        if (articlesDir.exists() && articlesDir.isDirectory())
        {
            processDir(articlesDir, type);
        }

        File storiesDir = new File(seedDir, "stories");
        type = MediaContentType.Story;
        Logger.info("Looking for stories: " + storiesDir);
        if (storiesDir.exists() && storiesDir.isDirectory())
        {
            processDir(storiesDir, type);
        }
    }

    public static void processDir(File dir, MediaContentType type) throws IOException
    {
        File[] files = dir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        for (File thisArticleDir: files)
        {
            Long id = Long.decode(thisArticleDir.getName());
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(thisArticleDir, "entity.json"))));
            String line = br.readLine();
            if (line != null)
            {
                Json.setObjectMapper(Serializer.emptyChangeDateMapper);
                MediaContent mc = Json.fromJson(Json.parse(line), MediaContent.class);
                Logger.info("import: [" + mc.getId() + "] " + mc);
                mc.setContentType(type);
                mc.setId(id);
                Image cover = mc.getCover();
                if (cover != null)
                {
                    cover.setId(null);
                    cover.setId((Long) save(cover));
                    mc.setCover(cover);
                }

//                User user = mc.getAddedBy();
//                if (user != null)
//                {
//                    user = ContentManager.getUserById(user.getId());
//                }

                br.close();
                br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(thisArticleDir, "all.churches"))));
                line = br.readLine();
                if (line != null && !"".equals(line))
                    mc.setChurches(parseChurches(line, mc));
                br.close();
                br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(thisArticleDir, "dedicated.church"))));
                line = br.readLine();
                if (line != null && !"".equals(line))
                    mc.setDedicatedChurch(parseChurches(line, mc).iterator().next());
                br.close();

                // todo: connect churches

                save(mc);
            }
        }
    }

    private static Set<Church> parseChurches(String line, MediaContent mc)
    {
        LinkedHashSet<Church> res = new LinkedHashSet<>();
        String[] split = line.split(",");
        for (String id : split)
        {
            Church c = ContentManager.getChurch(id.trim());
            res.add(c);
            if (c != null) {
                res.add(c);
                Set<MediaContent> media = c.getMedia();
                if (media == null)
                    media = new LinkedHashSet<>();
                media.add(mc);
                c.setMedia(media);
                save(c);
            }
        }
        return res;
    }
}
