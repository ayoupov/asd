package utils;

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


  private void init() throws IOException
  {
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_NAME + ".properties");
    full = new Properties();
    full.load(in);
    in.close();
  }

  public static ServerProperties getInstance()
  {
    return instance;
  }

  private ServerProperties()
  {
    try
    {
      init();
    } catch (Exception e)
    {
      System.out.println("Unable to load " + APP_NAME + ".properties! Expect bad behavior.");
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
    try
    {
      String value = getValue(key);
      if (value != null)
        res = Integer.parseInt(value);
    } catch
        (Exception e)
    {
    }
    return res;
  }

}
