package utils.seed.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import models.address.Dekanat;
import models.address.Diocese;
import models.internal.GeographyManager;
import org.opengis.feature.simple.SimpleFeature;
import utils.HibernateUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 20:28
 */
public class DekanatProcessor implements ShapeProcessor
{
    @Override
    public boolean process(SimpleFeature feature)
    {
        Geometry geometry = ((Geometry) (feature).getDefaultGeometry());
        String name = feature.getAttribute("jpt_nazwa_").toString();
        Diocese diocese = findDiocese(geometry);
        Dekanat dekanat = new Dekanat(diocese, name, geometry);
        HibernateUtils.saveOrUpdate(dekanat);
        return true;
    }

    private Diocese findDiocese(Geometry geometry)
    {
        return (Diocese) GeographyManager.findByPoint(Diocese.class, geometry.getCentroid());
    }
}
