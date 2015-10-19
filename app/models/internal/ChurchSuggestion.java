package models.internal;

import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.Null;

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

    private String extID;

    private String name;

    private String address;

    private Integer constructionStart;

    private Integer constructionEnd;

    private String architects;

    private String website;

    private ChurchSuggestionType type;

    private String field;

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

    public Integer getConstructionStart()
    {
        return constructionStart;
    }

    public void setConstructionStart(Integer constructionStart)
    {
        this.constructionStart = constructionStart;
    }

    public Integer getConstructionEnd()
    {
        return constructionEnd;
    }

    public void setConstructionEnd(Integer constructionEnd)
    {
        this.constructionEnd = constructionEnd;
    }

    public String getArchitects()
    {
        return architects;
    }

    public void setArchitects(String architects)
    {
        this.architects = architects;
    }

    public ChurchSuggestionType getType()
    {
        return type;
    }

    public void setType(ChurchSuggestionType type)
    {
        this.type = type;
    }

    public String getExtID()
    {
        return extID;
    }

    public void setExtID(String extID)
    {
        this.extID = extID;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    @Override
    public String toString()
    {
        return "ChurchSuggestion{" +
                "type=" + type +
                ", extID='" + extID + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}
