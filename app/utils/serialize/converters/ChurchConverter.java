package utils.serialize.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import controllers.routes;
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
    public void serialize(Church church, JsonGenerator json, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
//        {
//            "title": "Result Title",
//                "url": "/optional/url/on/click",
//                "image": "optional-image.jpg",
//                "price": "Optional Price",
//                "description": "Optional Description"
//        }
        json.writeStartObject();
        json.writeStringField("title", church.getName());
        json.writeStringField("url", "javascript:navigateFromSearch('"+church.getExtID() +"');");
//        json.writeStringField("url", String.valueOf(routes.Application.index(church.getExtID())));
        json.writeStringField("description", church.getAddress().getUnfolded());
        json.writeEndObject();
        // check validity??
    }
}
