package models.internal;

import play.cache.Cache;
import play.mvc.Http;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 20.08.2015
 * Time: 15:12
 */
public class SessionCache
{
    private static Map<String, ?> sessionedCache;

    public static Object get(Http.Session session, String key)
    {
        // Generate a unique ID
        String uuid = session.get("uuid");
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
            session.put("uuid", uuid);
        }

        // Access the cache
        Object data = Cache.get(uuid + key);
        return data;
    }

    public static void put(Http.Session session, String key, Object o)
    {
        String uuid = session.get("uuid");
//        System.out.println("put: uuid = " + uuid);
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
            session.put("uuid", uuid);
        }

        Cache.set(uuid + key, o);
//        System.out.println("put : uuid + key, o : " + (uuid + key) + ":" +  o);
    }
}
