package utils.seed.geo;

import com.vividsolutions.jts.geom.Geometry;
import models.address.Metropolie;
import org.opengis.feature.simple.SimpleFeature;
import utils.HibernateUtils;
import utils.lang.PolishSupport;

import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 15:31
 */
public class MetropolieProcessor implements ShapeProcessor
{
    @Override
    public boolean process(SimpleFeature feature)
    {
        Geometry geometry = ((Geometry) (feature).getDefaultGeometry());

        Map<String, Long> metropolias = StaticRegionalDataProvider.getMetropolias();
        Long idFromShape = (Long) feature.getAttribute("id");
        boolean res = false;
        for (Map.Entry<String, Long> entry : metropolias.entrySet()) {
            String name = entry.getKey();
            Long id = entry.getValue();
//            String nameFromFeature = feature.getAttribute("jpt_nazwa_").toString();
            if (Objects.equals(id, idFromShape)) {
                Metropolie metropolie = new Metropolie(id, geometry, name);
                HibernateUtils.saveOrUpdate(metropolie);
                res = true;
                break;
            }
        }
        return res;
    }
}
