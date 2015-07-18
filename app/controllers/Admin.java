package controllers;

import models.internal.search.SearchManager;

import play.mvc.Controller;
import play.mvc.Result;

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
}
