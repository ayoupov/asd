package utils.serialize;

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
public class ChurchConverter extends com.fasterxml.jackson.databind.JsonSerializer<Church>
{
    @Override
    public void serialize(Church church, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {

    }
}
