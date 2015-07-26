package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import models.address.Geometrified;
import models.internal.GeographyManager;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.map.TileBuilder;
import utils.seed.Disseminator;

import java.io.IOException;
import java.util.List;

import static json.geojson.objects.Bounding.tile2boundingBox;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.07.2015
 * Time: 15:52
 */
public class Tiles extends Controller
{
    public static Result tile(String layer, Integer zoom, Integer x, Integer y, String ext) throws IOException
    {
        ObjectNode result = Json.newObject();
//        TileBuilder.BoundingBox bb = tile2boundingBox(x, y, zoom);
//        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
//        Envelope envelope = new Envelope(bb.east, bb.west, bb.north, bb.south);
//        Geometry g = gf.toGeometry(envelope);
////                            Geometry g = EnvelopeAdapter.toPolygon(envelope, 3857);
////                            System.out.println("going to extract " + layer +" from: " + g);
//        CoordinateReferenceSystem sourceCRS = null, targetCRS = null;
//        Geometry targetGeometry = null;
//        try {
//            sourceCRS = CRS.decode("EPSG:4326", true);
//            targetCRS = CRS.decode("EPSG:3857");
//            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
//            targetGeometry = JTS.transform(g, transform);
////                                System.out.println("checking against: " + targetGeometry);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (zoom == 2) {
//            System.out.println("transformed " + g + " into " + targetGeometry);
//        }
//
////                            Envelope e = org.geotools.referencing.CRS.transform(envelope, crs);
////                            Geometry check = gf.toGeometry(e);
////                            System.out.println("with internal: " + check.getEnvelopeInternal());
////                            List<Geometrified> features = GeographyManager.findByBBox(clazz, g);
//        List<Geometrified> features = GeographyManager.findByBBox(clazz, targetGeometry);
//        if (features != null && features.size() > 0) {
//            GeometryJSON geojson = new GeometryJSON();
//            for (Geometrified geometrified : features) {
//                Geometry geometry = geometrified.getGeometry();
//                geojson.write(geometry, result);
//            }
//        }
        return ok(result);
    }

    public static Result rebuild()
    {
        try {
//            String whereTo = Assets.minifiedPath(".");
            String whereTo = "d:/prog/asd/public/tiles";
            System.out.println("whereTo = " + whereTo);
            TileBuilder.buildTiles(whereTo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ok("rebuild started");
    }

    public static Result seed()
    {
        try {
            Disseminator.main(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok("seed started");
    }
}
