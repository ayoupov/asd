package utils.serialize;

import org.hibernate.search.bridge.StringBridge;
import play.Logger;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.11.2015
 * Time: 3:06
 */
public class CollectionToCSVBridge implements StringBridge
{
    public String objectToString(Object value)
    {
        try {
            if (value != null) {
                StringBuffer buf = new StringBuffer();

                Collection<?> col = (Collection<?>) value;
                Iterator<?> it = col.iterator();
                while (it.hasNext()) {
                    String next = it.next().toString();
                    buf.append(next);
                    if (it.hasNext())
                        buf.append(", ");
                }
                return buf.toString();
            }
        } catch (Exception e)
        {
            Logger.error("while search, inside: ", e);
        }
        return null;
    }
}
