package utils.seed;

import models.Church;
import models.internal.ContentManager;
import models.internal.Importer;
import play.Logger;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Photo;
import se.walkercrou.places.Place;
import utils.ServerProperties;
import utils.seed.geo.DioceseProcessor;
import utils.seed.geo.MetropolieProcessor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static utils.HibernateUtils.*;
import static utils.seed.ChurchImageSeeds.seedChurchImages;
import static utils.seed.ChurchSeeds.seedChurchesExt;
import static utils.seed.GeographySeeds.seedGeography;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 15:04
 */
public class Disseminator
{
    private static final String dataDir = ServerProperties.getValue("asd.seed.data.folder");

    public static void fullSeed() throws IOException
    {
//        userSeed();
        geoSeed();
        churchSeedExt();
//        websitesSeed();
        contentSeed();
        imageSeed();
    }

    public static void websitesSeed()
    {
        GooglePlaces client = new GooglePlaces(ServerProperties.getValue("google.api.key"));
        int radius = 200;
        int total = 0;
        beginTransaction();
        List<Church> churches = ContentManager.getUninternetedChurches();
        Logger.info(churches.size() + " of uninterneted churches");
        for (Church church : churches) {
            List<Place> places = client.getNearbyPlaces(
                    church.address.getGeometry().getCoordinate().y,
                    church.address.getGeometry().getCoordinate().x,
                    radius, 1);
            Place place;
            Logger.info(church.getExtID() + " : " + places.size() + " : " + places);
            if (places.size() > 0 && (place = places.get(0)) != null) {
                Place detailed = place.getDetails();
                if (detailed != null) {
                    String website = detailed.getWebsite();
                    if (website != null) {
                        church.setWebsite(website);
                        saveOrUpdate(church);
                        total++;
                        Logger.info(String.format("found site for %s : %s", church.getExtID(), website));
                    }
                    List<Photo> photos = detailed.getPhotos();
                    if (photos != null && photos.size() > 0) {
                        Logger.info("But found photos, eg: ", photos.get(0).getReference());
                    }
                }
            }
        }
        Logger.info("Total websites found: " + total);
        commitTransaction();
    }

    public static void imageSeed() throws IOException
    {
        File imageDirFile = new File(dataDir + "asd_church_images");
        if (!imageDirFile.exists())
            throw new IOException("no data found: " + imageDirFile);
        beginTransaction();
        seedChurchImages(imageDirFile);
        commitTransaction();
    }

    private static void churchSeedExt() throws IOException
    {
        beginTransaction();
        seedChurchesExt(dataDir + "churches_no_geocode.csv");
        commitTransaction();
    }

    public static void contentSeed() throws IOException
    {
        beginTransaction();
//        seedMockContent();
        Importer.importMediaContent();
        commitTransaction();
    }

    public static void churchSeed() throws IOException
    {
        beginTransaction();
//        seedChurches(dataDir + "churches.csv");
        seedChurchesExt(dataDir + "churches_no_geocode.csv");
        commitTransaction();
    }

    public static void geoSeed() throws IOException
    {
        beginTransaction();
        MetropolieProcessor mp = new MetropolieProcessor();
        seedGeography(dataDir + "gis/metropolies_10percent", mp);
        commitTransaction();
        beginTransaction();
        DioceseProcessor dp = new DioceseProcessor();
        seedGeography(dataDir + "gis/diecezje_wgs84_10percent", dp);
        commitTransaction();
//        beginTransaction();
//        DekanatProcessor dekp = new DekanatProcessor();
//        seedGeography(dataDir + "gis/dekanaty_wgs84_10percent", dekp);
//        seedGeography(dataDir + "gis/dekanaty_wgs84_6percent", dekp);
//        commitTransaction();
    }

}
