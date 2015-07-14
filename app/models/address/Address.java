package models.address;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import models.internal.GeographyManager;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:43
 */
@Entity
@Embeddable
public class Address
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "org.hibernate.spatial.GeometryType")
    protected Geometry geometry;

    @Analyzer(definition = "polish_def_analyzer")
    @OneToOne
    private Parish parish;

    @Field
    private String unfolded;

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

    public Parish getParish()
    {
        return parish;
    }

    public void setParish(Parish parish)
    {
        this.parish = parish;
    }

    public String getUnfolded()
    {
        return unfolded;
    }

    public void setUnfolded(String unfolded)
    {
        this.unfolded = unfolded;
    }

    public Address(Double lat, Double lng)
    {
        this(lat,lng, null);
    }

    public Address(Double lat, Double lng, String unfolded)
    {
        Point point = new Point(new CoordinateArraySequence
                (new Coordinate[]{new Coordinate(lat, lng)}), new GeometryFactory());
        Geometry geom = point; // todo: change to envelope?
        Address checked = GeographyManager.check(geom);
        if (checked == null) {
            this.geometry = point;
            checked = GeographyManager.add(point, unfolded);
        }
        this.geometry = checked.geometry;
        this.unfolded = checked.unfolded;
        this.parish = checked.parish;
    }

    public Address()
    {

    }

    public String constructChurchId()
    {
        String dioId = "";
        long parishId = 0;
        if (parish != null) {
            parishId = parish.getId();
            Dekanat dekanat = parish.getDekanat();
            if (dekanat != null) {
                Diocese diocese = dekanat.getDiocese();
                if (diocese != null) {
                    dioId = diocese.getId();
                } else dioId = "XX";
            }
        }
        int getParishCount = GeographyManager.getChurchesInParish(parish);
        return dioId + "-" + String.format("%03d", parishId) + "-" + String.format("%03d", getParishCount + 1);
    }

    @Override
    public String toString()
    {
        return "Address{" +
                "id=" + id +
                ", unfolded='" + unfolded + '\'' +
                '}';
    }
}
