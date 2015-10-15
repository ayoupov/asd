package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import models.internal.GeographyManager;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ServerProperties;
import utils.map.TileBuilder;
import utils.serialize.Serializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static utils.HibernateUtils.beginTransaction;
import static utils.HibernateUtils.commitTransaction;
import static utils.ServerProperties.isInProduction;
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
    static String tilesPath = ServerProperties.getValue("asd.tiles.path");

    public static Result tile(String layer, Integer zoom, Integer x, Integer y, String ext) throws IOException
    {

        Json.setObjectMapper(Serializer.entityMapper);
        ObjectNode result = Json.newObject();
        boolean dekanatLayer = false;
        if ("c".equals(layer) || (dekanatLayer = "d".equals(layer))) {
            // serve static if exists, assuming production has nginx set
            if (dekanatLayer && !isInProduction()) {
                File dek = retrieveTile(layer, zoom, x, y);
                if (dek.exists())
                    return ok(dek, true);
            }
            beginTransaction();
            TileBuilder.BoundingBox bb = tile2boundingBox(x, y, zoom);
            Envelope envelope = new Envelope(bb.east, bb.west, bb.north, bb.south);
            Geometry g = factory.toGeometry(envelope);
            List farr;
            if (dekanatLayer)
                farr = getDekanatFeatures(g);
            else
                farr = getChurchFeatures(g);
            commitTransaction();
            result.put("type", "FeatureCollection");
            result.put("features", Json.toJson(farr));
            if (dekanatLayer)
                storeTile(layer, zoom, x, y, result);
        }
        response().setHeader("Cache-Control", "no-transform,public,max-age=3600,s-maxage=3600");
        return ok(result);
    }

    private static String tileDirName(String layer, Integer zoom, Integer x)
    {
        return String.format(tilesPath + "/%s/%d/%d/", layer, zoom, x);
    }

    private static File retrieveTile(String layer, Integer zoom, Integer x, Integer y)
    {
        return new File(tileDirName(layer, zoom, x), y + ".json");
    }

    private static void storeTile(String layer, Integer zoom, Integer x, Integer y, ObjectNode result) throws IOException
    {
        File dir = new File(tileDirName(layer, zoom, x));
        File tile = new File(dir, y + ".json");
        if (!tile.exists()) {
            dir.mkdirs();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tile), "UTF-8"));
            out.write(result.toString());
            out.close();
        }
    }

    private static List getChurchFeatures(Geometry g)
    {
        List features = GeographyManager.findChurchesByGeometry(g);
        List farr = new ArrayList();
        if (features != null && features.size() > 0) {
            for (Object f : features) {
                Object[] row = (Object[]) f;
                Long id = (Long) row[0];
                String extID = (String) row[1];
                String name = (String) row[2];
                ObjectNode props = Json.newObject();
                props.put("id", id);
                props.put("ext_id", extID);
                props.put("name", name);
                Geometry point = (Geometry) row[3];
                ObjectNode node = Json.newObject();
                node.put("type", "Feature");
                node.put("geometry", Json.toJson(point));
                node.put("properties", props);
                farr.add(node);
            }
        }
        return farr;
    }

    private static List getDekanatFeatures(Geometry g)
    {
        Json.setObjectMapper(Serializer.entityMapper);
        List features = GeographyManager.findDekanatsByGeometry(g);
        List farr = new ArrayList();
        if (features != null && features.size() > 0) {
            for (Object f : features) {
                Object[] row = (Object[]) f;
                Long id = (Long) row[0];
                String name = (String) row[1];
                ObjectNode props = Json.newObject();
                props.put("id", id);
                props.put("name", name);
                Geometry geometry = (Geometry) row[2];
                ObjectNode node = Json.newObject();
                node.put("type", "Feature");
                node.put("geometry", Json.toJson(geometry));
                node.put("properties", props);
                farr.add(node);
            }
        }
        return farr;
    }

    public static Result rebuild()
    {
        beginTransaction();
        try {
//            String whereTo = Assets.minifiedPath(".");
            String whereTo = "d:/prog/asd/public/tiles";
//            System.out.println("whereTo = " + whereTo);
            TileBuilder.buildTiles(whereTo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        commitTransaction();
        return ok("rebuild started");
    }

}
