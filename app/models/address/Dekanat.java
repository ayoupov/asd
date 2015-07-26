package models.address;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.07.2015
 * Time: 17:55
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Dekanat implements Geometrified
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Diocese diocese;

    private String name;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometry;

    public Dekanat(Diocese diocese, String name, Geometry geometry)
    {
        this.diocese = diocese;
        this.name = name;
        this.geometry = geometry;
    }

    public Dekanat()
    {
    }

    public Geometry getGeometry()
    {
        return geometry;
    }

    public void setGeometry(Geometry geometry)
    {
        this.geometry = geometry;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Diocese getDiocese()
    {
        return diocese;
    }

    public void setDiocese(Diocese diocese)
    {
        this.diocese = diocese;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
