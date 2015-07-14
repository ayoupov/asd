package utils.seed.geo;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 20:21
 */
public interface ShapeProcessor
{
    boolean process(SimpleFeature feature);
}
