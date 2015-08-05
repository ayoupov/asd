package utils.seed;

import models.Church;
import models.address.Address;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static utils.HibernateUtils.saveOrUpdate;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 15:25
 */
public class ChurchSeeds
{
    private final static boolean debug = false;
//    private final static boolean debug = true;

    public static void seedChurches(String path) throws IOException
    {
        String line;
        int success = 0, failed = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {

            int i = 0; // line num
            while ((line = br.readLine()) != null && (!debug || i < 10)) {
                try {
                    i++;
                    if (i == 1)
                        continue;
                    // take only good for now, so no empty ids
                    String[] split = line.split("\\|");
                    // struct: 0 - id, 1 - "ext_-id", 2 - "name", 3 - "lat", 4 - "lng", 5 - "address (unfolded)"
                    String extId = beautify(split[1]);
                    if (extId.length() < 6) {
                        System.out.println("Failed to seed church:");
                        System.out.println("line = " + line);
                        failed++;
                    }
                    String unfolded = (!"".equals(split[5])) ? unwrap(split[5]) : null;
                    Address address = new Address(Double.parseDouble(unwrap(split[3])),
                            Double.parseDouble(unwrap(split[4])),
                            unfolded
                    );
                    saveOrUpdate(address);
                    Church church = new Church(extId,
                            unwrap(split[2]),
                            address
                    );
                    saveOrUpdate(church);
                    success++;
                } catch (Exception e) {
                    System.out.println("Failed to seed church:");
                    System.out.println("line = " + line);
                    e.printStackTrace();
                    failed++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(String.format("Seeded %s, success : %d, failed : %d", "churches", success, failed));
    }

    private static String beautify(String extId)
    {
        String id = unwrap(extId);
        return id.replaceAll("_", "-");
    }

    private static String unwrap(String s)
    {
        if ("".equals(s))
            return "";
        String res = s.substring(0, s.lastIndexOf("\""));
        return res.substring(1);
    }

    public static void main(String[] args)
    {
        System.out.println(unwrap("\"famrgmisnv\""));
    }
}
