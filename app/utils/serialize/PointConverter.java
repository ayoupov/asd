package utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 15.07.2015
 * Time: 2:22
 */
public class PointConverter extends JsonSerializer<Geometry>
{

    @Override
    public void serialize(Geometry geometry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
        Point point = (Point) geometry;
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("lat", point.getCoordinate().x);
        jsonGenerator.writeNumberField("lng", point.getCoordinate().y);
        jsonGenerator.writeEndObject();
    }
}
