package models.internal;

import models.Church;
import models.user.User;
import org.hibernate.annotations.Type;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.Date;

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

    private String years;

    private String architects;

    private String website;

    private ChurchSuggestionType type;

    private String field;

    private boolean fixed;

    private boolean ignored;

    @ManyToOne
    private Church relatedChurch;

    @Column(name = "suggested_on_dt")
    private Date suggestedOn;


    @OneToOne()
    @JoinColumn(name = "suggested_by")
    private User suggestedBy;

    @Type(type = "text")
    private String other;

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

    public String getYears()
    {
        return years;
    }

    public void setYears(String years)
    {
        this.years = years;
    }

    public String getOther()
    {
        return other;
    }

    public void setOther(String other)
    {
        this.other = other;
    }

    public User getSuggestedBy()
    {
        return suggestedBy;
    }

    public void setSuggestedBy(User suggestedBy)
    {
        this.suggestedBy = suggestedBy;
    }

    public Date getSuggestedOn()
    {
        return suggestedOn;
    }

    public void setSuggestedOn(Date suggestedOn)
    {
        this.suggestedOn = suggestedOn;
    }

    public Church getRelatedChurch()
    {
        return relatedChurch;
    }

    public void setRelatedChurch(Church relatedChurch)
    {
        this.relatedChurch = relatedChurch;
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

    public boolean isFixed()
    {
        return fixed;
    }

    public void setFixed(boolean fixed)
    {
        this.fixed = fixed;
    }

    public boolean isIgnored()
    {
        return ignored;
    }

    public void setIgnored(boolean ignored)
    {
        this.ignored = ignored;
    }
}
