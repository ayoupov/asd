package models.address;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 08.07.2015
 * Time: 19:40
 */
public interface Geometrified
{
    Geometry getGeometry();

    String getName();
}
