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
public class PointConverter extends JsonSerializer<Point>
{

    @Override
    public void serialize(Point point, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeNumber(point.getCoordinate().y); // lat
        jsonGenerator.writeNumber(point.getCoordinate().x); // lng
        jsonGenerator.writeEndArray();

    }
}
