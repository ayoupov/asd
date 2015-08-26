package utils.media;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 26.08.2015
 * Time: 17:17
 */
public class TestContentParse
{
    public static void main(String[] args) throws ContentProcessorException
    {
        try {
            System.out.println(ContentProcessor.getDescriptions());
            String text = "Test unformatted\nAndmore[lead]Some lead goes here[/lead]\nusual simple text\n[image fullwidth]images/Poland-6[/image]";
            List<ContentFragment> parsed = ContentProcessor.parse(text);
            System.out.println(parsed);
            for (ContentFragment fr : parsed)
            {
                System.out.println(fr.render());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
