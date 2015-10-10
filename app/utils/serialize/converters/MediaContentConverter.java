package utils.serialize.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import controllers.routes;
import models.MediaContent;
import models.user.User;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.10.2015
 * Time: 22:41
 */
public class MediaContentConverter extends com.fasterxml.jackson.databind.JsonSerializer<MediaContent>
{
    @Override
    public void serialize(MediaContent content, JsonGenerator json, SerializerProvider serializerProvider) throws IOException, JsonProcessingException
    {
//        {
//            "title": "Result Title",
//                "url": "/optional/url/on/click",
//                "image": "optional-image.jpg",
//                "price": "Optional Price",
//                "description": "Optional Description"
//        }
        json.writeStartObject();
        json.writeStringField("title", content.getTitle());
        json.writeStringField("url", String.valueOf(routes.MediaContents.byTypeAndId(content.getContentType().toString(), content.getId(), "html")));
        json.writeStringField("image", content.getCoverThumb().path);
        String authors = content.getAuthors().stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));
        json.writeStringField("description", "by " + authors);
        json.writeEndObject();
    }
}
