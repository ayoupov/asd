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
import play.Logger;
import utils.serialize.converters.PointConverter;

import javax.persistence.*;
import java.util.List;

import static models.internal.GeographyManager.findDioceses;
import static utils.DataUtils.safeInt;

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
    private Diocese diocese;

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

//    public Dekanat getDekanat()
//    {
//        return dekanat;
//    }
//
//    public void setDekanat(Dekanat dekanat)
//    {
//        this.dekanat = dekanat;
//    }


    public Diocese getDiocese()
    {
        return diocese;
    }

    public void setDiocese(Diocese diocese)
    {
        this.diocese = diocese;
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
        updateAddress(lat, lng, unfolded);
    }

    public Address()
    {

    }

    @Override
    public String toString()
    {
        return "Address{" +
                "id=" + id +
                ", unfolded='" + unfolded + '\'' +
                '}';
    }

    public void updateAddress(Double lat, Double lng, String unfolded)
    {
        if (lat != null && lng != null) {
            Point point = new Point(new CoordinateArraySequence
                    (new Coordinate[]{new Coordinate(lng, lat)}), new GeometryFactory());
            this.geometry = point;
            List<Diocese> dioceses = findDioceses(point);
            if (dioceses.size() > 1) {
                Logger.warn(String.format("Alarma! : %s {%s} is in %d dekanats! ",
                        unfolded, point.toString(), dioceses.size()));
                for (Diocese d : dioceses) {
                    Logger.warn("which are: ", d);
                }
            }
            this.diocese = dioceses.get(0);
        } else
        {
            Logger.error("lat || lng is null!!!");
        }
        this.unfolded = unfolded;

    }
}
