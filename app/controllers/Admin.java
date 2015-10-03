package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CharMatcher;
import models.Church;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.SessionCache;
import models.internal.UserManager;
import models.internal.search.SearchManager;
import models.internal.search.filters.ArticleFilter;
import models.internal.search.filters.ChurchFilter;
import models.internal.search.filters.StoryFilter;
import models.internal.search.filters.UserFilter;
import models.user.User;
import models.user.UserRole;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.ServerProperties;
import utils.map.AdditiveProcessor;
import utils.map.BadIdsSieve;
import utils.map.Processor;
import utils.map.Snapshoter;
import utils.media.bbcode.BBCodeTest;
import utils.seed.Disseminator;
import views.html.admin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 15.07.2015
 * Time: 16:36
 */
//@Security.Authenticated(Secured.class)
public class Admin extends Controller
{
    private static final String dataDir = ServerProperties.getValue("asd.seed.data.folder");
    private static final String snapshotPath = ServerProperties.getValue("asd.snapshoter.path");

    public static boolean roleCheck()
    {
        beginTransaction();
        User user = UserManager.getLocalUser(session());
        commitTransaction();
        System.out.println("user = " + user);
        return user != null && (user.getRole() == UserRole.Administrator || user.getRole() == UserRole.Moderator);
    }

    public static Result reindex() throws InterruptedException
    {
        if (roleCheck()) {
            SearchManager.reindex();
            return ok("reindexed");
        }
        return forbidden();
    }

    public static Result sieve() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            BadIdsSieve.main(null);
            return ok("sieved");
        }
        return forbidden();
    }

    public static Result parse() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            Processor.main(new String[]{dataDir + "doc.kml"});
            return ok("parsed");
        }
        return forbidden("nope");
    }

    public static Result parseNoGeocode() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            Processor.noGeocode(dataDir + "doc.kml");
            return ok("parsed");
        }
        return forbidden("nope");
    }

    public static Result snapshotify() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            Snapshoter.snap(dataDir + "doc.kml", snapshotPath);
            return ok("snapshots in progress");
        }
        return forbidden();
    }

    public static Result snapshotifySome() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            Snapshoter.snapSome(dataDir + "doc.kml", snapshotPath, dataDir + "churches_snapshot_reexport.txt");
            return ok("some snapshots are in progress");
        }
        return forbidden();
    }

    public static Result parseAdd() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            AdditiveProcessor.main(new String[]{dataDir + "churches.csv"});
            return ok("parsed additionally");
        }
        return forbidden();
    }

    public static Result index()
    {
        if (roleCheck()) {
            beginTransaction();

            UserFilter userFilter = (UserFilter) SessionCache.get(session(), "userFilter");
            if (userFilter == null)
                userFilter = new UserFilter(request());
            else
                userFilter.apply(request(), "users");
            long totalUsers = ContentManager.getTotalUsers();
            userFilter.setTotalResults(totalUsers);

            SessionCache.put(session(), "userFilter", userFilter);

            ArticleFilter articleFilter = (ArticleFilter) SessionCache.get(session(), "articleFilter");
            if (articleFilter == null)
                articleFilter = new ArticleFilter(request());
            else
                articleFilter.apply(request(), "articles");
            long totalArticles = ContentManager.getTotalMediaContent(MediaContentType.Article);
            articleFilter.setTotalResults(totalArticles);
            SessionCache.put(session(), "articleFilter", articleFilter);

            StoryFilter storyFilter = (StoryFilter) SessionCache.get(session(), "storyFilter");
            if (storyFilter == null)
                storyFilter = new StoryFilter(request());
            else
                storyFilter.apply(request(), "stories");
            long totalStories = ContentManager.getTotalMediaContent(MediaContentType.Story);
            storyFilter.setTotalResults(totalStories);
            SessionCache.put(session(), "storyFilter", storyFilter);

            ChurchFilter churchFilter = (ChurchFilter) SessionCache.get(session(), "churchFilter");
            if (churchFilter == null)
                churchFilter = new ChurchFilter(request());
            else
                churchFilter.apply(request(), "churches");
            long totalChurches = ContentManager.getTotalChurches(churchFilter);
            churchFilter.setTotalResults(totalChurches);
            SessionCache.put(session(), "churchFilter", churchFilter);

            List<User> users = ContentManager.getUsers(userFilter);
            List<MediaContent> articles = ContentManager.getMediaContent(articleFilter, MediaContentType.Article);
            List<MediaContent> stories = ContentManager.getMediaContent(storyFilter, MediaContentType.Story);
            List<Church> churches = ContentManager.getChurches(churchFilter);

            Map<String, Integer> issues = new HashMap<>();
            issues.put("users", ContentManager.getUserIssuesCount());
            issues.put("articles", ContentManager.getArticleIssuesCount());
            issues.put("stories", ContentManager.getStoryIssuesCount());
            issues.put("churches", ContentManager.getChurchIssuesCount());

            Html content = admin.render(users, articles, stories, churches, issues, session());
            commitTransaction();
            return ok(content);
        }
        return forbidden();
    }

    public static Result checkParse2()
    {
        if (roleCheck()) {
            try {
                BBCodeTest.main(null);
            } catch (Exception e) {
                return forbidden(e.getMessage());
            }
            return ok("parsed");
        }
        return forbidden();
    }

    public static Result seed(String part)
    {
        if (roleCheck()) {
            try {
                switch (part) {
                    case "users":
                        Disseminator.userSeed();
                        break;
                    case "churches":
                        Disseminator.churchSeed();
                        break;
                    case "geo":
                        Disseminator.geoSeed();
                        break;
                    case "content":
                        Disseminator.contentSeed();
                        break;
                    case "all":
                    default:
                        Disseminator.fullSeed();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ok("seed started");
        }
        return forbidden();
    }

    public static Result temp()
    {
        return ok(views.html.temp.render());
    }

    public static Result getChurches()
    {
        beginTransaction();
        JsonNode jsonNode = Json.toJson(ContentManager.getChurches());
        commitTransaction();
        return ok(jsonNode);
    }

}
