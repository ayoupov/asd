package models.address;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.07.2015
 * Time: 13:02
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Metropolie implements Geometrified
{
    @Id
    private Long id;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometry;

    @Type(type = "org.hibernate.spatial.GeometryType")
    protected Point centroid;

    private String name;

    public Metropolie(Long id, Geometry geometry, Point centroid, String name)
    {
        this.id = id;
        this.geometry = geometry;
        this.name = name;
        this.centroid = centroid;
    }

    public Metropolie()
    {
    }

    public String getName()
    {
        return name;
    }

    public Geometry getGeometry()
    {
        return geometry;
    }

    public void setGeometry(Geometry geometry)
    {
        this.geometry = geometry;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "Metropolie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
