package utils.serialize;

import com.bedatadriven.geojson.GeoJsonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Point;
import models.Church;
import models.MediaContent;
import play.Logger;
import play.Plugin;
import utils.serialize.converters.ChurchConverter;
import utils.serialize.converters.MainPageChurchConverter;
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

    public static ObjectMapper emptyMapper;
    public static ObjectWriter defaultWriter;

    public static ObjectMapper searchMapper;
    public static ObjectMapper pointMapper;

    public static ObjectMapper shallowChurchMapper;
    public static ObjectWriter shallowChurchWriter;

    static
    {
        emptyMapper = new ObjectMapper();

        defaultWriter = emptyMapper.writer();

        shallowChurchMapper = new ObjectMapper();
        shallowChurchMapper.registerModule(new SimpleModule().addSerializer(Church.class, new MainPageChurchConverter()));
        shallowChurchMapper.registerModule(new SimpleModule().addSerializer(Point.class, new PointConverter()));
        shallowChurchWriter = shallowChurchMapper.writer();

        pointMapper = new ObjectMapper();
        pointMapper.registerModule(new SimpleModule().addSerializer(Point.class, new PointConverter()));

        searchMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("EntitySerializerModule");
        module.addSerializer(Church.class, new ChurchConverter());
        module.addSerializer(MediaContent.class, new MediaContentConverter());
        searchMapper.registerModule(module);
        searchMapper.registerModule(new GeoJsonModule());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Logger.info("Serializer: mappers initialized");
//        Json.setObjectMapper(searchMapper);
    }
}
