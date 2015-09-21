package controllers;

import com.bedatadriven.geojson.GeoJsonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.*;
import models.internal.GeographyManager;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.map.TileBuilder;
import utils.seed.Disseminator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
import static utils.map.TileBuilder.tile2boundingBox;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.07.2015
 * Time: 15:52
 */
public class Tiles extends Controller
{
    static GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Result tile(String layer, Integer zoom, Integer x, Integer y, String ext) throws IOException
    {
        Json.setObjectMapper(new ObjectMapper().registerModule(new GeoJsonModule()));
        ObjectNode result = Json.newObject();
        beginTransaction();

//        Envelope env = TileBuilder.tile2Env(x, y, zoom);
        if ("c".equals(layer)) {
//            System.out.println("Getting churches from " + x + "/" + y + "/" + zoom);
//            System.out.println("env = " + env);
            TileBuilder.BoundingBox bb = tile2boundingBox(x, y, zoom);
            Envelope envelope = new Envelope(bb.east, bb.west, bb.north, bb.south);
//            System.out.println("envelope = " + envelope);
            Geometry g = factory.toGeometry(envelope);
//            System.out.println("g = " + g);
            List features = GeographyManager.findChurchesByGeometry(g);
            List farr = new ArrayList();
            if (features != null && features.size() > 0) {
                for (Object f : features)
                {
                    Object[] row = (Object[]) f;
                    Long id = (Long) row[0];
                    String extID = (String) row[1];
                    String name = (String) row[2];
                    ObjectNode props = Json.newObject();
                    props.put("id", id);
                    props.put("ext_id", extID);
                    props.put("name", name);
                    Geometry point = (Geometry) row[3];
//                    props.put("point", Json.toJson(point));
                    ObjectNode node = Json.newObject();
                    node.put("type", "Feature");
                    node.put("geometry", Json.toJson(point));
                    node.put("properties", props);
                    farr.add(node);
                }
            }
            result.put("type", "FeatureCollection");
            result.put("features", Json.toJson(farr));
        }
        commitTransaction();
        response().setHeader("Cache-Control", "no-transform,public,max-age=3600,s-maxage=3600");
        return ok(result);
    }

    public static Result rebuild()
    {
        beginTransaction();
        try {
//            String whereTo = Assets.minifiedPath(".");
            String whereTo = "d:/prog/asd/public/tiles";
            System.out.println("whereTo = " + whereTo);
            TileBuilder.buildTiles(whereTo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        commitTransaction();
        return ok("rebuild started");
    }

}
