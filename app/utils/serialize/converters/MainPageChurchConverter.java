package utils.serialize.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Church;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.10.2015
 * Time: 3:46
 */
public class MainPageChurchConverter extends com.fasterxml.jackson.databind.JsonSerializer<Church>
{
    @Override
    public void serialize(Church church, JsonGenerator json, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
        json.writeStartObject();
        json.writeStringField("extID", church.getExtID());
        json.writeObjectField("geometry", church.getAddress().getGeometry());
        json.writeEndObject();
    }
}
