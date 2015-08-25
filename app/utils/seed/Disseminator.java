package utils.seed;

import models.MediaContent;
import models.MediaContentType;
import models.internal.UserManager;
import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import utils.HibernateUtils;
import utils.seed.geo.DekanatProcessor;
import utils.seed.geo.DioceseProcessor;
import utils.seed.geo.MetropolieProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
import static utils.HibernateUtils.save;
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
        beginTransaction();
        seedUsers();
        commitTransaction();
        beginTransaction();
        MetropolieProcessor mp = new MetropolieProcessor();
//        seedGeography("d:/prog/asd/res/gis/cleaned/metropolies_wgs84", mp);
        seedGeography("d:/prog/asd/res/gis/cut/metropolies_10percent", mp);
        commitTransaction();
        beginTransaction();
        DioceseProcessor dp = new DioceseProcessor();
//        seedGeography("d:/prog/asd/res/gis/cleaned/diecezje_wgs84", dp);
        seedGeography("d:/prog/asd/res/gis/cut/diecezje_wgs84_10percent", dp);
        commitTransaction();
        beginTransaction();
        DekanatProcessor dekp = new DekanatProcessor();
//        seedGeography("d:/prog/asd/res/gis/cleaned/dekanaty_wgs84", dekp);
        seedGeography("d:/prog/asd/res/gis/cut/dekanaty_wgs84_10percent", dekp);
        commitTransaction();
        beginTransaction();
        seedChurches("res/data/churches.csv");
        commitTransaction();
        beginTransaction();
        seedMockContent();
        commitTransaction();
    }

    private static void seedMockContent()
    {
        User user = UserManager.getAutoUser();
        List<User> authors = new ArrayList<>();
        User articleUser1 = UserManager.getBySocialId("asd:article1");
        User articleUser2 = UserManager.getBySocialId("asd:article2");
        User storyUser1 = UserManager.getBySocialId("asd:story1");
        User storyUser2 = UserManager.getBySocialId("asd:story2");
        authors.add(articleUser1);
        authors.add(articleUser2);
        for (int i = 0; i < 20; i++) {
            String text = "Test article text #" + i;
            String lead = "Test article lead #" + i;
            String title = "Testity article test title (" + i + ")";

            MediaContent mc = new MediaContent(MediaContentType.Article, text, lead, title, (i > 5 && i < 13), authors, user);
            HibernateUtils.save(mc);
        }
        authors= new ArrayList<>();
        authors.add(storyUser1);
        authors.add(storyUser2);
        for (int i = 0; i < 20; i++) {
            String text = "Test story text #" + i;
            String lead = "Test story lead #" + i;
            String title = "Testity story test title (" + i + ")";

            MediaContent mc = new MediaContent(MediaContentType.Story, text, lead, title, (i > 5 && i < 13), authors, user);
            HibernateUtils.save(mc);
        }
    }

    private static void seedUsers()
    {
        UserManager.getAutoUser();
        User storyUser1 = new User("Story writer 1", UserRole.User, UserStatus.Active, "asd:story1");
        save(storyUser1);
        User storyUser2 = new User("Story writer 2", UserRole.User, UserStatus.Active, "asd:story2");
        save(storyUser2);
        User articleUser1 = new User("Article writer 1", UserRole.User, UserStatus.Active, "asd:article1");
        save(articleUser1);
        User articleUser2 = new User("Article writer 2", UserRole.User, UserStatus.Active, "asd:article2");
        save(articleUser2);

    }

}
