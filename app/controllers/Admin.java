package controllers;

import models.Church;
import models.MediaContent;
import models.MediaContentType;
import models.internal.ContentManager;
import models.internal.SessionCache;
import models.internal.search.SearchManager;

import models.internal.search.filters.*;
import models.user.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.map.AdditiveProcessor;
import utils.map.BadIdsSieve;
import utils.map.Processor;
import utils.map.Snapshoter;
import views.html.admin;

import java.io.IOException;
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
public class Admin extends Controller
{

    public static Result reindex() throws InterruptedException
    {
        SearchManager.reindex();
        return ok("reindexed");
    }

    public static Result sieve() throws IOException, InterruptedException
    {
        BadIdsSieve.main(null);
        return ok("sieved");
    }

    public static Result parse() throws IOException, InterruptedException
    {
        Processor.main(new String[]{"d:/prog/asd/res/doc.kml"});
        return ok("parsed");
    }

    public static Result snapshotify() throws IOException, InterruptedException
    {
//        Snapshoter.snap("d:/prog/asd/res/doc.kml", "d:/prog/asd/res/snapshots");
        Snapshoter.snap("d:/prog/asd/res/doc.kml", "C:\\Users\\ayoupov\\Google Диск\\snapshots");
        return ok("snapshots in progress");
    }

    public static Result parseAdd() throws IOException, InterruptedException
    {
        AdditiveProcessor.main(new String[]{"d:/prog/asd/res/data/churches.csv"});
        return ok("parsed additionally");
    }

    public static Result index()
    {
        beginTransaction();
        
        UserFilter userFilter = (UserFilter) SessionCache.get(session(), "userFilter");
        if (userFilter == null)
            userFilter = new UserFilter(request());
        else 
            userFilter.apply(request(), "users");
        SessionCache.put(session(), "userFilter", userFilter);
        
        ArticleFilter articleFilter = (ArticleFilter) SessionCache.get(session(), "articleFilter");
        if (articleFilter == null)
            articleFilter = new ArticleFilter(request());
        else 
            articleFilter.apply(request(), "articles");
        SessionCache.put(session(), "articleFilter", articleFilter);
        
        StoryFilter storyFilter = (StoryFilter) SessionCache.get(session(), "storyFilter");
        if (storyFilter == null)
            storyFilter = new StoryFilter(request());
        else 
            storyFilter.apply(request(), "stories");
        SessionCache.put(session(), "storyFilter", storyFilter);
        
        ChurchFilter churchFilter = (ChurchFilter) SessionCache.get(session(), "churchFilter");
        if (churchFilter == null)
            churchFilter = new ChurchFilter(request());
        else 
            churchFilter.apply(request(), "churches");
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

    public static Result topotest() throws IOException
    {
        TestTopoJsonProcessor.test();
        return ok("did it");
    }
}
