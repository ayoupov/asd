package controllers;

import models.internal.search.SearchManager;

import play.mvc.Controller;
import play.mvc.Result;
import utils.map.AdditiveProcessor;
import utils.map.BadIdsSieve;
import utils.map.Processor;
import utils.map.Snapshoter;

import java.io.IOException;

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
}
