package utils.serialize;

import com.bedatadriven.geojson.GeoJsonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Point;
import models.Church;
import models.MediaContent;
import play.Plugin;
import play.libs.Json;
import utils.serialize.converters.ChurchConverter;
import utils.serialize.converters.MediaContentConverter;
import utils.serialize.converters.PointConverter;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.10.2015
 * Time: 3:38
 */
public class Serializer extends Plugin
{

    public static ObjectMapper entityMapper;
    public static ObjectMapper emptyMapper;
    public static ObjectMapper pointMapper;

    static
    {
        emptyMapper = new ObjectMapper();

        pointMapper = new ObjectMapper();
        pointMapper.registerModule(new SimpleModule().addSerializer(Point.class, new PointConverter()));

        entityMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("EntitySerializerModule");
        module.addSerializer(Church.class, new ChurchConverter());
        module.addSerializer(MediaContent.class, new MediaContentConverter());
        entityMapper.registerModule(module);
        entityMapper.registerModule(new GeoJsonModule());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        System.out.println("Serializer: entityMapper added");
        Json.setObjectMapper(entityMapper);
    }
}
