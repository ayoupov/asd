package utils.serialize.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Point;
import play.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 15.07.2015
 * Time: 2:22
 */
public class SynonymConverter extends JsonSerializer<Set<String>>
{

    @Override
    public void serialize(Set<String> synonyms, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
        jsonGenerator.writeStartArray();
        if (synonyms != null)
            synonyms.stream().forEach(s -> {
                try {
                    jsonGenerator.writeString(s);
                } catch (IOException e) {
                    Logger.error("while serializing synonyms: ", e);
                }
            });
        jsonGenerator.writeEndArray();

    }
}
