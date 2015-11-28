package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Church;
import models.Image;
import models.MediaContent;
import models.MediaContentType;
import models.internal.*;
import models.internal.search.SearchManager;
import models.internal.search.filters.*;
import models.user.User;
import models.user.UserRole;
import play.Logger;
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
import models.internal.email.EmailTemplate;
import views.html.admin;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static models.internal.UserManager.getLocalUser;
import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
import static utils.ServerProperties.isInProduction;

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
        User user = getLocalUser(session());
        commitTransaction();
        if (!isInProduction())
            Logger.info("user = " + user);
        return user != null && (user.getRole() == UserRole.Administrator || user.getRole() == UserRole.Moderator);
    }

    public static Result reindex() throws InterruptedException
    {
        if (roleCheck()) {
            beginTransaction();
            SearchManager.reindex();
            commitTransaction();
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

            User adminUser = getLocalUser(session());

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

            ImageFilter imageFilter = (ImageFilter) SessionCache.get(session(), "imageFilter");
            if (imageFilter == null)
                imageFilter = new ImageFilter(request());
            else {
                imageFilter.apply(request(), "images");
                imageFilter.applyExtra(request());
            }
            long totalImages = ContentManager.getTotalImages(imageFilter);
            imageFilter.setTotalResults(totalImages);
            SessionCache.put(session(), "imageFilter", imageFilter);

            List<User> users = ContentManager.getUsers(userFilter);
            List<MediaContent> articles = ContentManager.getMediaContent(articleFilter, MediaContentType.Article);
            List<MediaContent> stories = ContentManager.getMediaContent(storyFilter, MediaContentType.Story);
            List churchObj = ContentManager.getChurches(churchFilter);
            List<Church> churches = (List<Church>) churchObj.stream().map(row -> ((Object[])row)[0]).collect(Collectors.toList());
            churches = churches.stream().sorted(ChurchIssueComparator.instance()).collect(Collectors.toList());

            List<Image> images = ContentManager.getImages(imageFilter);
            List<EmailTemplate> emails = ContentManager.getEmails();
            List<UserFeedback> feedbacks = ContentManager.getFeedbacks();

            List<ChurchSuggestion> newChurches = ContentManager.getSuggestedChurches();
            List<String> dioIds = ContentManager.getDioceseIds();

            Long totalFeedbacks = ContentManager.getTotalFeedbacks();
            if (totalFeedbacks == null)
                totalFeedbacks = 0l;


            Map<String, Integer> issues = new HashMap<>();
            issues.put("users", ContentManager.getUserIssuesCount());
            issues.put("articles", ContentManager.getArticleIssuesCount());
            issues.put("stories", ContentManager.getStoryIssuesCount());
            issues.put("churches", ContentManager.getChurchIssuesCount());
            issues.put("images", ContentManager.getImageIssuesCount());
            issues.put("feedbacks", feedbacks != null ? feedbacks.size() : 0);

            Map<String, Long> totals = new HashMap<>();
            totals.put("articles", totalArticles);
            totals.put("stories", totalStories);
            Long totalFullChurches = ContentManager.getChurchCount(true);
            Long totalFullImages = ContentManager.getTotalImages(null);

            totals.put("churches", totalFullChurches);
            totals.put("images", totalFullImages);
            totals.put("feedbacks", totalFeedbacks);

            Html content = admin.render(
                    adminUser,
                    users, articles, stories, churches, images, feedbacks,
                    emails,
                    newChurches, dioIds,
                    issues, totals, session());
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
                    case "churches":
                        Disseminator.churchSeed();
                        break;
                    case "geo":
                        Disseminator.geoSeed();
                        break;
                    case "content":
                        Disseminator.contentSeed();
                        break;
                    case "churchimages" :
                        Disseminator.imageSeed();
                        break;
                    case "websites" :
                        Disseminator.websitesSeed();
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

    public static Result exportMediaContent() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            Exporter.exportMediaContent();
            return ok("mediacontent exported");
        }
        return forbidden();
    }

    public static Result importMediaContent() throws IOException, InterruptedException
    {
        if (roleCheck()) {
            beginTransaction();
            Importer.importMediaContent();
            commitTransaction();
            return ok("mediacontent imported");
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

    private static class ChurchIssueComparator implements Comparator<Church>
    {
        private static Comparator<Church> instance = new ChurchIssueComparator();

        private ChurchIssueComparator(){

        }

        public static Comparator<Church> instance()
        {
            return instance;
        }

        @Override
        public int compare(Church o1, Church o2)
        {
            long s1 = (o1.getRequests() == null) ? 0 : o1.getRequests().stream().filter(r -> !r.isFixed() && !r.isIgnored()).count();
            long s2 = (o2.getRequests() == null) ? 0 : o2.getRequests().stream().filter(r -> !r.isFixed() && !r.isIgnored()).count();
            return (int) (s2-s1);
        }

        @Override
        public Comparator<Church> reversed()
        {
            return null;
        }
    }
}
