package models.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.07.2015
 * Time: 18:27
 */
@Entity
@Table(name = "diocese")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Diocese implements Geometrified
{

    @Id
    private String id;

    private String name;

    @Type(type = "org.hibernate.spatial.GeometryType")
    protected Geometry geometry;

    @Type(type = "org.hibernate.spatial.GeometryType")
    protected Point centroid;

    @OneToOne
    @JsonIgnore
    private Metropolie metropolie;

    private boolean archidiocese;

    public Diocese(String id, String name, Geometry geometry, Point centroid, Metropolie metropolie)
    {
        this.id = id;
        this.name = name;
        this.geometry = geometry;
        this.centroid = centroid;
        this.metropolie = metropolie;
    }

    public Diocese()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Geometry getGeometry()
    {
        return geometry;
    }

    public void setGeometry(Geometry geometry)
    {
        this.geometry = geometry;
    }

    public Metropolie getMetropolie()
    {
        return metropolie;
    }

    public void setMetropolie(Metropolie metropolie)
    {
        this.metropolie = metropolie;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isArchidiocese()
    {
        return archidiocese;
    }

    public void setArchidiocese(boolean archidiocese)
    {
        this.archidiocese = archidiocese;
    }

    @Override
    public String toString()
    {
        return "Diocese{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", metropolie=" + metropolie +
                ", archidiocese=" + archidiocese +
                '}';
    }
}
