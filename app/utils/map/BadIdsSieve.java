package utils.map;

import play.Logger;

import java.io.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 28.07.2015
 * Time: 15:26
 */
public class BadIdsSieve
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("d:/prog/asd/res/data/churches.csv"), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/prog/asd/res/data/churches_bad_id.csv"), "UTF-8"));
        BufferedWriter writerGood = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/prog/asd/res/data/churches_good_id.csv"), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            // skip header
            String[] row = line.split("\\|");
            if ("\"ID\"".equals(row[0])) {
                writer.write(line);
                writerGood.write(line);
                writer.newLine();
                writerGood.newLine();
                continue;
            }
            String extId = row[1];
            if (extId == null || "".equals(extId) ||
                    (!extId.matches("\"[A-Z]{2}[-–_][0-9]{3}\"") &&
                    !extId.matches("\"[A-Z]{2}[-–_][0-9]{3}[-–_][0-9]{2}\""))) {
                Logger.debug("bad id:", extId);
                writer.write(line);
                writer.newLine();
            } else {
                String goodId = beautify(row[1]);
                for (int i = 0; i < row.length; i++) {
                    writerGood.write((i == 1) ? goodId : row[i]);
                    writerGood.write("|");
                }
                writerGood.newLine();
            }
        }
        reader.close();
        writerGood.close();

        HashMap<String, String> count = new HashMap<>();
        reader = new BufferedReader(new InputStreamReader(new FileInputStream("d:/prog/asd/res/data/churches_good_id.csv"), "UTF-8"));
        while ((line = reader.readLine()) != null) {
            String[] row = line.split("\\|");
            String goodId = row[1];
            String existing = count.get(goodId);
            if (existing != null) {
//                System.out.println(line);
                count.put(goodId, line);
                writer.write(existing);
                writer.newLine();
                writer.write(line);
                writer.newLine();
            } else
                count.put(goodId, line);
        }
        writer.close();
    }

    public static String beautify(String s)
    {
        return s.replaceAll("[-–_]", "-").replace(" ", "").trim();
    }
}
