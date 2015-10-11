package models.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import models.internal.GeographyManager;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import utils.serialize.converters.PointConverter;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:43
 */
@Entity
@Table(name = "address")
@Embeddable
public class Address
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Type(type = "org.hibernate.spatial.GeometryType")
    @JsonSerialize(using = PointConverter.class)
    protected Geometry geometry;

    @Analyzer(definition = "polish_def_analyzer")
    @OneToOne
    @JsonIgnore
    private Dekanat dekanat;

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

    public Dekanat getDekanat()
    {
        return dekanat;
    }

    public void setDekanat(Dekanat dekanat)
    {
        this.dekanat = dekanat;
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
        this(lat, lng, null);
    }

    public Address(Double lat, Double lng, String unfolded)
    {
        Point point = new Point(new CoordinateArraySequence
                (new Coordinate[]{new Coordinate(lng, lat)}), new GeometryFactory());
        Geometry geom = point;
        Address checked = GeographyManager.check(geom);
        if (checked == null) {
            this.geometry = point;
            checked = GeographyManager.add(point, unfolded);
        }
        this.geometry = checked.geometry;
        this.unfolded = checked.unfolded;
        this.dekanat = checked.dekanat;
    }

    public Address()
    {

    }

    public String constructChurchId()
    {
        String dioId = "";
        Diocese diocese = dekanat.getDiocese();
        if (diocese != null) {
            dioId = diocese.getId();
        } else dioId = "??";
        int getDekanatCount = GeographyManager.getChurchesInDekanat(dekanat);
        return dioId + "-" + String.format("%03d", dekanat.getId()) + "-" + String.format("%03d", getDekanatCount + 1);
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
