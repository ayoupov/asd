package utils;

import play.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: ayoupov
 */
public class ServerProperties
{
    private static Properties full;
    private static ServerProperties instance = new ServerProperties();
    public static final String APP_NAME = "asd";
    public static final String PROD_APP_NAME = "asd.prod";

    private static boolean productionMode;

    private void init() throws IOException
    {
//        System.out.println("System.getProperties() = " + System.getProperties());
//        System.out.println(System.getProperty("production.mode"));
        productionMode = System.getProperty("production.mode") != null;
        Logger.info("productionMode = " + productionMode);
        String propsToLoad = ((productionMode) ? PROD_APP_NAME : APP_NAME) + ".properties";
        Logger.info("propsToLoad = " + propsToLoad);
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsToLoad);
        full = new Properties();
        full.load(in);
        in.close();
    }

    public static boolean isInProduction()
    {
        return productionMode;
    }

    public static ServerProperties getInstance()
    {
        return instance;
    }

    private ServerProperties()
    {
        try {
            init();
        } catch (Exception e) {
            Logger.error("Unable to load " + APP_NAME + ".properties! Expect bad behavior.");
        }
    }

    public static Properties getProperties()
    {
        return full;
    }

    public static String getValue(String key)
    {
        return full.getProperty(key);
    }

    public static int getIntValue(String key, int defaultValue)
    {
        int res = defaultValue;
        try {
            String value = getValue(key);
            if (value != null)
                res = Integer.parseInt(value);
        } catch
                (Exception e) {
        }
        return res;
    }

}
