package models.address;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 06.07.2015
 * Time: 17:54
 */
@Entity
public class Parish  implements Geometrified
{
    @Id
    private Long id;
//    private String id;

    @ManyToOne
    private Dekanat dekanat;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometry;

    private String name;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Geometry getGeometry()
    {
        return geometry;
    }

    public void setGeometry(Geometry geometry)
    {
        this.geometry = geometry;
    }

    public Dekanat getDekanat()
    {
        return dekanat;
    }

    public void setDekanat(Dekanat dekanat)
    {
        this.dekanat = dekanat;
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
