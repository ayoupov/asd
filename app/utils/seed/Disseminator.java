package utils.seed;

import models.internal.UserManager;
import utils.seed.geo.DekanatProcessor;
import utils.seed.geo.DioceseProcessor;
import utils.seed.geo.MetropolieProcessor;

import java.io.IOException;

import static utils.seed.ChurchSeeds.seedChurches;
import static utils.seed.GeographySeeds.seedGeography;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 15:04
 */
public class Disseminator
{
    public static void main(String[] args) throws IOException
    {
        seedUsers();
        MetropolieProcessor mp = new MetropolieProcessor();
        seedGeography("d:/prog/asd/res/gis/cleaned/metropolies_wgs84", mp);
        DioceseProcessor dp = new DioceseProcessor();
        seedGeography("d:/prog/asd/res/gis/cleaned/diecezje_wgs84", dp);
        DekanatProcessor dekp = new DekanatProcessor();
        seedGeography("d:/prog/asd/res/gis/cleaned/dekanaty_wgs84", dekp);
        seedChurches("res/data/churches.csv");
    }

    private static void seedUsers()
    {
        UserManager.getAutoUser();
    }

}
