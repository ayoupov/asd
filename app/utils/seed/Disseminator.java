package utils.seed;

import models.MediaContent;
import models.MediaContentType;
import models.internal.UserManager;
import models.user.User;
import models.user.UserRole;
import models.user.UserStatus;
import utils.HibernateUtils;
import utils.ServerProperties;
import utils.seed.geo.DekanatProcessor;
import utils.seed.geo.DioceseProcessor;
import utils.seed.geo.MetropolieProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.HibernateUtils.*;
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
        String dataDir = ServerProperties.getValue("asd.seed.data.folder");
//        beginTransaction();
//        seedUsers();
//        commitTransaction();
//        beginTransaction();
//        MetropolieProcessor mp = new MetropolieProcessor();
//        seedGeography(dataDir + "gis/metropolies_10percent", mp);
//        commitTransaction();
//        beginTransaction();
//        DioceseProcessor dp = new DioceseProcessor();
//        seedGeography(dataDir + "gis/diecezje_wgs84_10percent", dp);
//        commitTransaction();
//        beginTransaction();
//        DekanatProcessor dekp = new DekanatProcessor();
//        seedGeography(dataDir + "gis/dekanaty_wgs84_10percent", dekp);
//        commitTransaction();
        beginTransaction();
        seedChurches(dataDir + "churches.csv");
        commitTransaction();
        beginTransaction();
        seedMockContent();
        commitTransaction();
    }

    private static void seedMockContent()
    {
        User user = UserManager.getAutoUser();
        List<User> authors = new ArrayList<>();
        User articleUser1 = UserManager.getBySocialId("asd:article1");
        User articleUser2 = UserManager.getBySocialId("asd:article2");
        User storyUser1 = UserManager.getBySocialId("asd:story1");
        User storyUser2 = UserManager.getBySocialId("asd:story2");
        authors.add(articleUser1);
        authors.add(articleUser2);
        String t = "[padder/]Powstanie kombinatu metalurgicznego i miasta nowa Huta pod Krakowem zostało postanowione przez rząd PRL 17    maja 1947. Miasto miało wyrażać ducha myśli socjalistycznej - w planie widać wyraźne inspiracje    Magnitogorskiem - ówczesnym radzieckim wzorcem ośrodka przemysłowego. Symetryczne, osiowe założenie    kompozycyjne wypełnione zostało prostymi, klasycznymi budynkami, parkami i socjalistycznymi ośrodkami    kultury -  miejsca na kościół w planie  oczywiście  nie było.[image textsizeplus]/assets/images/Poland-1.png[/image]Abstrakcyjna idea stworzenia idealnego robotniczego miasta, szybko zderzyła się z tradycyjną polską    rzeczywistością. Przez pierwsze 10 lat przez Nową Hutę przewinęło się około 200 tysięcy ludzi - większość    przyjechała z przesiąkniętej obyczajem wsi. Brak kościoła był dojmujący - dla wielu zajmował przecież    centralne miejsce w życiu. Był on bowiem odwiecznym miejscem mszy, spowiedzi i innych zwyczajów    definiujących rytm życia. Niezbudowanie w Hucie kościoła miało wkrótce stać się problemem, którego skali nie    przewidział nikt.<br>[image mainfullwidth]/assets/images/Poland-1.png[/image]W 1957, w zawierusze związanej z końcem stalinizmu, miasto uzyskało od władz zgodę na budowę kościoła.    Natychmiast w miejscu planowanej budowy postawiono i wyświęcono krzyż. [excerpt]Milicja użyła broni palnej,\u0003aresztowano ok 500 osób,\u0003a skazano 87[/excerpt]Zaledwie dwa lata później  zgodę    cofnięto, pieniądze na budowę skonfiskowano a komitet budowy kościoła rozwiązano. Miarka przebrała się 26. kwietnia 1960 roku, gdy krakowski Komitet Miejski PZPR podjął decyzję o likwidacji krzyża.  Przerodziło się    to w protest, a następnie w regularne walki uliczne, podczas których zdemolowano siedzibę władz    administracyjnych dzielnicy, milicja użyła broni palnej, aresztowano ok 500 osób, a skazano 87. Krzyż    ostatecznie pozostał, ale zamiast kościoła zbudowano równie potrzebną szkołę - obrazek doskonale ilustrujący    stan zawieszenia broni pomiędzy państwem i Kościołem.[image]/assets/images/world4.png[/image]\n" +
                "[quote]Wchodziłem na dach jednego z bloków mieszkalnych\u0003i patrzyłem oniemiały –[/quote]\n" +
                "[i]ze wszystkich stron szli ludzie\u0003z łopatami, wiadrami, taczkami, również osoby starsze i dzieci kłamstwa\u0003i hipokryzję Kościoła[/i][/quote]";
        String l = "Najbardziej wyrazistym przykładem Architektury Siódmego Dnia jest kościół Arka Pana w    Nowej Hucie. Jak w doskonałym scenariuszu filmowym, w historii powstania kościoła łączą się silne wątki:    czytelna walka o symbole, zaangażowanie najznakomitszych osobistości, krwawy przebieg konfliktu i jakość    architektoniczna wzniesionej w efekcie świątyni.";
        String ttl = "Kamień z kosmosu";
        boolean starred = true;
        MediaContent goodArticle = new MediaContent(MediaContentType.Article, t, l, ttl, starred, authors, user);
        HibernateUtils.save(goodArticle);

        for (int i = 0; i < 10; i++) {
            String text = "Test article text #" + i;
            String lead = "Test article lead #" + i;
            String title = "Testity article test title (" + i + ")";

            MediaContent mc = new MediaContent(MediaContentType.Article, text, lead, title, (i > 5), authors, user);
            HibernateUtils.save(mc);
        }
        authors = new ArrayList<>();
        authors.add(storyUser1);
        authors.add(storyUser2);
        for (int i = 0; i < 10; i++) {
            String text = "Test story text #" + i;
            String lead = "Test story lead #" + i;
            String title = "Testity story test title (" + i + ")";

            MediaContent mc = new MediaContent(MediaContentType.Story, text, lead, title, (i > 5), authors, user);
            HibernateUtils.save(mc);
        }
    }

    private static void seedUsers()
    {
        UserManager.getAutoUser();
        User storyUser1 = new User("Story writer 1", UserRole.User, UserStatus.Active, "asd:story1");
        save(storyUser1);
        User storyUser2 = new User("Story writer 2", UserRole.User, UserStatus.Active, "asd:story2");
        save(storyUser2);
        User articleUser1 = new User("Article writer 1", UserRole.User, UserStatus.Active, "asd:article1");
        save(articleUser1);
        User articleUser2 = new User("Article writer 2", UserRole.User, UserStatus.Active, "asd:article2");
        save(articleUser2);

    }

}
