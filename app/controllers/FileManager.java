package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 15.07.2015
 * Time: 16:36
 */
public class FileManager extends Controller
{
    private static final Set<Pair<String, String>> ALLOWED_ACTIONS = new HashSet<>();

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String LIST = "list";
    public static final String UPLOAD = "upload";
    public static final String DELETE = "delete";
    public static final String RENAME = "rename";
    public static final String ICON = "icon";
    public static final String IMAGE_TYPE = "Image";
    public static final String OTHER_TYPE = "Other";
    private static final Set<String> IMAGE_TYPES = new HashSet<>();

    static {
        ALLOWED_ACTIONS.add(Pair.of(GET, LIST));
        ALLOWED_ACTIONS.add(Pair.of(POST, UPLOAD));
        ALLOWED_ACTIONS.add(Pair.of(POST, DELETE));
        ALLOWED_ACTIONS.add(Pair.of(POST, RENAME));
        ALLOWED_ACTIONS.add(Pair.of(GET, ICON));

        IMAGE_TYPES.add("png");
        IMAGE_TYPES.add("jpg");
        IMAGE_TYPES.add("jpeg");
    }

    public static Result files() throws IOException
    {
        // 1. detect action and reroute
        String action = request().getQueryString("action");
        String method = request().method();
        if (action == null || !allowed(method, action))
            return badRequest();
        switch (action) {
            case LIST:
                return list();
//            case UPLOAD:
//                return upload();
//            case DELETE:
//                return delete();
//            case RENAME:
//                return rename();
//            case ICON:
//                return icon();
            default:
                return badRequest();
        }
    }

    private static Result list() throws IOException
    {
        ObjectNode res = Json.newObject();
        /*
            path current path to list (if subdirectories is supported)
            start the start offset
            limit amount of files to return
            sort the sort column:
                name file name
                size file size
                type file type
                mtime file modified time
            search search terms to filter by
         */
        String path = request().getQueryString("path");
        int start = safeInt(request().getQueryString("start"), 0);
        int limit = safeInt(request().getQueryString("limit"), 10);
        FileSort sort = FileSort.fromString(request().getQueryString("sort"));
        String nameFilter = request().getQueryString("search");

        Triple<Integer, Integer, List<Path>> triple = getFiles(path, start, limit, sort, nameFilter);

        /*
            start same as request
            limit same as request
            total total files in the current directory
            filteredTotal total files in the current directory after applying filters
            tags list of available tags to filter by
            directories list of subdirectories in the current directory
            files list of files in the current directory
                name file name
                attributes custom attributes
                type file type
                size file size (in bytes)
                mtime file modified time (seconds since epoch)
                tags list of tags for the file
         */

        res.put("start", start);
        res.put("limit", limit);
        res.put("total", triple.getLeft());
        res.put("filteredTotal", triple.getMiddle());
        res.put("tags", Json.newObject().arrayNode().add(IMAGE_TYPE).add(OTHER_TYPE));
        res.put("directories", ""); // todo: apply subdir listing
        ArrayNode files = Json.newObject().arrayNode();
        for (Path p : triple.getRight()) {
            ObjectNode node = Json.newObject();
            File f = p.toFile();
            String name = f.getName();
            // skipping custom attributes
            String type = getExtension(p);
            long size = Files.size(p);
            long mtime = Files.getLastModifiedTime(p).toMillis() / 1000;
            String tag = getTag(type);
            node.put("tags", Json.newObject().arrayNode().add(tag));
            node.put("name", name);
            node.put("type", type);
            node.put("size", size);
            node.put("mtime", mtime);
        }
        res.put("files", files);
        return ok(res);
    }

    private static String getTag(String type)
    {
        if (IMAGE_TYPES.contains(type.toLowerCase()))
            return IMAGE_TYPE;
        else
            return OTHER_TYPE;
    }

    private static Triple<Integer, Integer, List<Path>> getFiles(String path, int start, int limit, FileSort sort, String nameFilter) throws IOException
    {
        int total = 0;
        int filteredTotal = 0;
        List<Path> files = new ArrayList<>();
        Path dir = FileSystems.getDefault().getPath(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path p : stream) {
                total++;
                // apply filters
                if (nameFilter == null || "".equals(nameFilter) ||
                        (p.toFile().getName().toLowerCase().contains(nameFilter.toLowerCase())))
                {
                    files.add(p);
                    filteredTotal++;
                }
            }
        }

        Comparator<Path> comparator = new MTimeComparator();
        switch (sort) {
            case NAME:
                comparator = new NameComparator();
                break;
            case SIZE:
                comparator = new SizeComparator();
                break;
            case TYPE:
                comparator = new TypeComparator();
                break;
            case MTIME:
                comparator = new MTimeComparator();
                break;
        }

        Collections.sort(files, comparator);
        Path[] paths = files.toArray(new Path[files.size()]);
        // apply start/limit
        int to = Math.min(files.size(), start + limit);
        return Triple.of(total, filteredTotal, Arrays.asList(Arrays.copyOfRange(paths, start, to)));
    }

    private static int safeInt(String val, int def)
    {
        int res = def;
        try {
            res = Integer.parseInt(val);
        } catch (Exception e) {
        }
        return res;
    }

    private static boolean allowed(String method, String action)
    {
        boolean allowed = false;
        for (Pair pair : ALLOWED_ACTIONS) {
            allowed = pair.equals(Pair.of(method, action));
            if (allowed)
                break;
        }
        return allowed;
    }

    private enum FileSort
    {
        NAME, SIZE, TYPE, MTIME;

        public static FileSort fromString(String queryString)
        {
            switch (queryString) {
                case ("name"):
                    return NAME;
                case ("size"):
                    return SIZE;
                case ("type"):
                    return TYPE;
                case ("mtime"):
                default:
                    return MTIME;
            }
        }
    }

    private static class SizeComparator implements Comparator<Path>
    {

        @Override
        public int compare(Path o1, Path o2)
        {
            try {

                return (int) (Files.size(o1) - Files.size(o2));
            } catch (IOException e) {
                // handle exception
            }
            return -1;
        }
    }

    private static class MTimeComparator implements Comparator<Path>
    {
        @Override
        public int compare(Path o1, Path o2)
        {
            try {
                return Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));
            } catch (IOException e) {
                // handle exception
            }
            return -1;
        }

    }

    private static class NameComparator implements Comparator<Path>
    {
        @Override
        public int compare(Path o1, Path o2)
        {
            // ascending order (descending order would be: name2.compareTo(name1))
            return o1.getFileName().compareTo(o2.getFileName());
        }
    }

    private static class TypeComparator implements Comparator<Path>
    {
        @Override
        public int compare(Path o1, Path o2)
        {
            // ascending order (descending order would be: name2.compareTo(name1))
            String ext1 = getExtension(o1);
            String ext2 = getExtension(o2);
            return ext1.compareToIgnoreCase(ext2);
        }

    }

    private static String getExtension(Path path)
    {
        String fname1 = path.toFile().getName();
        int i1 = fname1.lastIndexOf('.');
        String ext1 = (i1 >= 0) ? fname1.substring(i1 + 1) : "";
        return ext1;
    }

}
