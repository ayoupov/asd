package models.internal;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 09.10.2015
 * Time: 15:43
 */
@Entity
@Table(name="church_suggestion")
public class ChurchSuggestion
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String address;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }
}
