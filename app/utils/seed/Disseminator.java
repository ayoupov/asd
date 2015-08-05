package utils.seed;

import models.MediaContent;
import models.MediaContentType;
import models.internal.UserManager;
import models.user.User;
import utils.HibernateUtils;
import utils.seed.geo.DekanatProcessor;
import utils.seed.geo.DioceseProcessor;
import utils.seed.geo.MetropolieProcessor;

import java.io.IOException;

import static utils.HibernateUtils.getSession;
import static utils.seed.ChurchSeeds.seedChurches;
import static utils.seed.GeographySeeds.seedGeography;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 15:04
 */
public class Disseminator
{
    public static void main(String[] args) throws IOException
    {
//        seedUsers();
//        MetropolieProcessor mp = new MetropolieProcessor();
//        seedGeography("d:/prog/asd/res/gis/cleaned/metropolies_wgs84", mp);
//        DioceseProcessor dp = new DioceseProcessor();
//        seedGeography("d:/prog/asd/res/gis/cleaned/diecezje_wgs84", dp);
//        DekanatProcessor dekp = new DekanatProcessor();
//        seedGeography("d:/prog/asd/res/gis/cleaned/dekanaty_wgs84", dekp);
//        seedChurches("res/data/churches.csv");
        seedContent();
    }

    private static void seedContent()
    {
        User user = UserManager.getAutoUser();
        for (int i = 0; i < 20; i++)
        {
            String text = "Test article text #" + i;
            String lead = "Test article lead #" + i;
            String title = "Testity article test title (" + i + ")";

            MediaContent mc = new MediaContent(MediaContentType.Article,text, lead, title, (i > 5 && i < 13), user);
            HibernateUtils.save(mc);
        }
        for (int i = 0; i < 20; i++)
        {
            String text = "Test story text #" + i;
            String lead = "Test story lead #" + i;
            String title = "Testity story test title (" + i + ")";

            MediaContent mc = new MediaContent(MediaContentType.Story, text, lead, title, (i > 5 && i < 13), user);
            HibernateUtils.save(mc);
        }
    }

    private static void seedUsers()
    {
        UserManager.getAutoUser();
    }

}
