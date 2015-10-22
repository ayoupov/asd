package utils.seed;

import models.Architect;
import models.Church;
import models.address.Address;
import models.internal.ContentManager;
import play.Logger;
import utils.map.Processor;

import java.io.*;
import java.util.*;

import static utils.DataUtils.safeInt;
import static utils.HibernateUtils.save;
import static utils.HibernateUtils.saveOrUpdate;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 15:25
 */
public class ChurchSeeds
{
    //    private final static boolean debug = true;
    private final static boolean debug = false;
    private static HashSet<String> skipIDs;

    static class ChurchDatabaseEntry
    {
        private Set<Architect> architects;
        private Integer startYear, endYear;
        private String id;
        private boolean eligible;
        private String synonyms;

        public String getSynonyms()
        {
            return synonyms;
        }

        public boolean isEligible()
        {
            return eligible;
        }

        public Set<Architect> getArchitects()
        {
            return architects;
        }

        public Integer getStartYear()
        {
            return startYear;
        }

        public Integer getEndYear()
        {
            return endYear;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public ChurchDatabaseEntry(String line)
        {
            String[] split = line.split("\\t", -1);
            if (split.length > 10) {
                String rawSecPart = unwrap(split[2]).trim();
                String secPart = (!"".equals(rawSecPart) ? ("-" + rawSecPart) : "");
                String firstPart = unwrap(split[1]);
                while (firstPart.length() < 3)
                    firstPart = "0" + firstPart;
                id = unwrap(split[0]) + "-" + firstPart + secPart;

                eligible = "1".equals(unwrap(split[5])) && !skipIDs.contains(id);

                synonyms = unwrap(split[7]);
                if ("".equals(synonyms))
                    synonyms = null;

                startYear = safeInt(unwrap(split[12]), 0);
                endYear = safeInt(unwrap(split[13]), 0);

                Set<String> architectsRaw = new LinkedHashSet<>();
                for (int i = 19; i < 24; i++) {
                    String architect = split[i];
                    if (architect != null) {
                        architect = unwrap(architect.trim());
                        if (!"".equals(architect)
                                && !"bd".equalsIgnoreCase(architect)
                                && !"✗".equalsIgnoreCase(architect)
                                && !"-".equals(architect))
                            architectsRaw.add(architect);
                    }
                }

                architects = new LinkedHashSet<>();
                for (String architectRaw : architectsRaw) {
                    Architect architect = getArchitect(architectRaw);
                    if (architect == null) {
                        architect = new Architect();
//                        architect.setId(arccount);
                        architect.setName(architectRaw);
                        architect.setId((Long) save(architect));
                    }
                    architects.add(architect);
                }

                Logger.info("parsed: " + this);
            }
        }

        private Architect getArchitect(String name)
        {
            return ContentManager.getArchitectByName(name);
        }

        @Override
        public String toString()
        {
            return "ChurchDatabaseEntry{" +
                    "id='" + id + '\'' +
                    ", architects=" + architects +
                    ", startYear=" + startYear +
                    ", endYear=" + endYear +
                    ", eligible=" + eligible +
                    ", synonyms='" + synonyms + '\'' +
                    '}';
        }
    }

    public static void seedChurchesExt(String path) throws IOException
    {
        if (!new File(Processor.dataDir + "churches_no_geocode.csv").exists()) {
            Processor.noGeocode(Processor.dataDir + "doc.kml");
            Logger.warn("Take care of XP-208!");
        }
//        seedChurches(path, null);
        seedChurchesWithData(path, Processor.dataDir + "database.csv");
    }

    private static void seedChurchesWithData(String churchesFromKML, String txtDatabase)
    {
        // 1. gather db
        Map<String, ChurchDatabaseEntry> data = new HashMap<>();
        skipIDs = new HashSet<>();
        String line;
        int dbParseFail = 0;
        boolean proceed = true;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Processor.dataDir + "church_seed_skip_ids.txt"), "UTF-8"))) {
            while ((line = br.readLine()) != null) {
                skipIDs.add(line);
            }
        } catch (Exception e) {
            Logger.error("failed to acquire ids to skip: " + e.getMessage());
            proceed = false;
        }
        if (proceed)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(txtDatabase), "UTF-8"))) {
                int i = 0; // line num
                while ((line = br.readLine()) != null && dbParseFail < 20) {
                    try {
                        i++;
                        if (i >= 3) {
                            ChurchDatabaseEntry entry = new ChurchDatabaseEntry(line);
                            if (entry.isEligible())
                                data.put(entry.getId(), entry);
                        }
                    } catch (Exception e) {
                        dbParseFail++;
                        Logger.error("dbparsefail: line(" + i + ") = " + line);
                        throw e;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        int success = 0, failed = 0;
        Set<String> dataNotFound = new LinkedHashSet<>();
        // 2. do the usual routine + include data from database
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(churchesFromKML), "UTF-8"))) {
            int i = 0; // line num
            while ((line = br.readLine()) != null && (!debug || i < 50) && failed < 20) {
                try {
                    i++;
                    if (i == 1)
                        continue;
                    // take only good for now, so no empty ids
                    String[] split = line.split("\\|");
                    // struct: 0 - id, 1 - "ext_-id", 2 - "name", 3 - "lat", 4 - "lng", 5 - "address (unfolded)"
                    String extId = beautify(unwrap(split[1]));
                    if (extId.length() < 6) {
                        Logger.error("Failed to seed church: ", line);
                        failed++;
                    }
                    if (skipIDs.contains(extId))
                    {
                        Logger.info("Skipping " + extId);
                        skipIDs.remove(extId);
                        continue;
                    }
                    String unfolded = null;
                    try {
                        if (split.length > 5)
                            unfolded = (!"".equals(split[5])) ? unwrap(split[5]) : null;
                    } catch (Exception e) {
                        unfolded = null;
                    }
                    Address address = new Address(Double.parseDouble(unwrap(split[3])),
                            Double.parseDouble(unwrap(split[4])),
                            unfolded
                    );
                    saveOrUpdate(address);
                    Church church = new Church(extId,
                            unwrap(split[2]),
                            address
                    );
                    ChurchDatabaseEntry entry = data.get(extId);
                    if (entry == null) {
                        dataNotFound.add(extId);
                    } else {
                        church.setArchitects(entry.getArchitects());
                        for (Architect architect : entry.getArchitects()) {
                            Set<Church> churches = architect.getChurches();
                            if (churches == null)
                                churches = new HashSet<>();
                            churches.add(church);
                            architect.setChurches(churches);
                        }
                        church.setConstructionStart(entry.getStartYear() == 0 ? null : entry.getStartYear());
                        church.setConstructionEnd(entry.getEndYear() == 0 ? null : entry.getEndYear());
                        Set<String> synonymSet = createSynonymSet(entry.getSynonyms());
                        if (synonymSet != null)
                            church.getSynset().addAll(synonymSet);
                        data.remove(extId);
                    }
                    saveOrUpdate(church);
                    success++;
                } catch (Exception e) {
                    Logger.error("Failed to seed church: ", line);
                    e.printStackTrace();
                    failed++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.info(String.format("Seeded %s, success : %d, failed : %d", "churches", success, failed));
        Logger.info("Meanwhile, database reports of " + dbParseFail + " parse fails.");
        Logger.info("data misses: (" + dataNotFound.size() + "): " + dataNotFound);
        Logger.info("db entries left: (" + data.size() + "):" + data);
    }

    private static Set<String> createSynonymSet(String synonyms)
    {
        Set<String> res = null;
        if (synonyms == null || "".equals(synonyms.trim()))
            return res;

        String[] split = synonyms.split(",;");
        for (String s : split) {
            res = new LinkedHashSet<>();
            res.add(s);
        }
        return res;
    }

    public static void seedChurches(String path) throws IOException
    {
        seedChurches(path, null);
    }

    public static void seedChurches(String path, String onlyOneID) throws IOException
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
                        Logger.error("Failed to seed church: ", line);
                        failed++;
                    }
                    if (onlyOneID != null && !extId.equals(onlyOneID))
                        continue;
                    String unfolded = null;
                    try {
                        if (split.length > 5)
                            unfolded = (!"".equals(split[5])) ? unwrap(split[5]) : null;
                    } catch (Exception e) {
                        unfolded = null;
                    }
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
                    Logger.error("Failed to seed church: ", line);
                    e.printStackTrace();
                    failed++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.info(String.format("Seeded %s, success : %d, failed : %d", "churches", success, failed));
    }

    private static String beautify(String extId)
    {
        String id = unwrap(extId);
        return id.replaceAll("_", "-");
    }

    private static String unwrap(String s)
    {
        if (s == null || "".equals(s))
            return s;
        int idx = s.indexOf("\"");
        if (idx == -1)
            return s;
        String res = s.substring(0, s.lastIndexOf("\""));
        return res.substring(1).trim();
    }

    public static void main(String[] args)
    {
        System.out.println(unwrap("\"famrgmisnv\""));
    }

}
