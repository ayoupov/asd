package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 19.11.2015
 * Time: 22:06
 */
@Entity
@Table(name="gsv")
public class GSV
{
    @Id
    @GeneratedValue
    private Integer id;

    private float lat;
    private float lng;

    private float heading;

    private float fov;

    private float pitch;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public float getLat()
    {
        return lat;
    }

    public void setLat(float lat)
    {
        this.lat = lat;
    }

    public float getLng()
    {
        return lng;
    }

    public void setLng(float lng)
    {
        this.lng = lng;
    }

    public float getHeading()
    {
        return heading;
    }

    public void setHeading(float heading)
    {
        this.heading = heading;
    }

    public float getFov()
    {
        return fov;
    }

    public void setFov(float fov)
    {
        this.fov = fov;
    }

    public float getPitch()
    {
        return pitch;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public void updateGSV(float lat, float lng, float heading, float fov, float pitch)
    {
        this.lat = lat;
        this.lng = lng;
        this.heading = heading;
        this.fov = fov;
        this.pitch = pitch;
    }

    public void updateGSV(GSV gsv)
    {
        updateGSV(gsv.lat, gsv.lng, gsv.heading, gsv.fov, gsv.pitch);
    }
}
