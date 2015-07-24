package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.map.TileBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.07.2015
 * Time: 15:52
 */
public class Tiles extends Controller
{
    public static Result tile(String layer, Integer zoom, Integer x, Integer y, String ext)
    {
        ObjectNode result = Json.newObject();
//        String filePath = Assets.minifiedPath(layer + "/" + zoom + "/" + x + "/" + y + "." + ext);
        return ok(result);
    }

    public static Result rebuild()
    {
        try {
//            String whereTo = Assets.minifiedPath(".");
            String whereTo = "d:/prog/asd/app/assets/tiles";
            System.out.println("whereTo = " + whereTo);
            TileBuilder.buildTiles(whereTo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ok("rebuild started");
    }
}
