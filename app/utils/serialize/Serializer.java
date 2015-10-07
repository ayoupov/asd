package utils.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Point;
import models.Church;
import play.libs.Json;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.10.2015
 * Time: 3:38
 */
public class Serializer
{
    static ObjectMapper entityMapper;

    static
    {
        entityMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("EntitySerializerModule");
        module.addSerializer(Church.class, new ChurchConverter());
        entityMapper.registerModule(module);

    }

    public static String shallowChurch(Church church)
    {
        Json.setObjectMapper(entityMapper);
        return Json.toJson(church).toString();
    }
}
