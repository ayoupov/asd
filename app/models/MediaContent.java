package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import models.user.User;
import models.user.UserRole;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import utils.serialize.converters.DateTimeConverter;

import javax.persistence.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:54
 */
@Entity
@Table(name = "content")
@Indexed
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // dirty hack to avoid serialization of proxies
//@Analyzer(definition = "polish_def_analyzer")
public class MediaContent
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "content_type")
    @Field
    private MediaContentType contentType;

    @Field
    @Type(type = "text")
    @Analyzer(definition = "polish_def_analyzer")
//    @JsonIgnore
    private String text;

    @Field
    @Type(type = "text")
    @Analyzer(definition = "polish_def_analyzer")
    private String lead;

    @Column(name = "cover_description")
    @Field
    @Type(type = "text")
    private String coverDescription;

    @Field
    @Analyzer(definition = "polish_def_analyzer")
    private String title;

    private String alt;

    private String year;

    public Boolean starred;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "cover_image_id")
    private Image cover;

    private String coverThumbPath;

    private String hoverThumbPath;

    @Column(name = "added_dt")
    private Date addedDT;

    @OneToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "content_authors")
    private Set<User> authors;

    @Column(name = "approved_dt")
//    @JsonSerialize(using = DateTimeConverter.class)
    private Date approvedDT;

    @OneToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToMany(mappedBy = "media")
    @JsonIgnore
    private Set<Church> churches;

    @OneToOne
    @JoinColumn(name = "dedicated_church")
    @JsonIgnore
    private Church dedicatedChurch;

    public MediaContent(MediaContentType contentType, String text, String title, String year, Image cover, String coverThumbPath, User addedBy, Church church)
    {
        this.contentType = contentType;
        this.text = text;
        this.title = title;
        this.year = year;
        this.cover = cover;
        this.coverThumbPath = coverThumbPath;
        this.authors = Collections.singleton(addedBy);
        this.addedBy = addedBy;
        this.addedDT = new Date();
        if (addedBy != null && (addedBy.getRole() == UserRole.Administrator || addedBy.getRole() == UserRole.Moderator)) {
            approvedBy = addedBy;
            approvedDT = addedDT;
        }
        this.churches = Collections.singleton(church);
        this.dedicatedChurch = church;
    }

    public MediaContent(MediaContentType contentType, String text, String lead, String title, Boolean starred, Set<User> authors, User addedBy)
    {
        this.contentType = contentType;
        this.text = text;
        this.lead = lead;
        this.title = title;
        this.starred = starred;
        this.authors = authors;
        this.addedBy = addedBy;
        this.addedDT = new Date();
        if (addedBy != null && (addedBy.getRole() == UserRole.Administrator || addedBy.getRole() == UserRole.Moderator)) {
            approvedBy = addedBy;
            approvedDT = addedDT;
        }
    }

    public MediaContent()
    {
        this.addedDT = new Date();
    }

    public MediaContent(MediaContentType mediaContentType)
    {
        this.contentType = mediaContentType;
        this.addedDT = new Date();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public MediaContentType getContentType()
    {
        return contentType;
    }

    public void setContentType(MediaContentType contentType)
    {
        this.contentType = contentType;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getLead()
    {
        return lead;
    }

    public void setLead(String lead)
    {
        this.lead = lead;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Boolean getStarred()
    {
        return starred;
    }

    public void setStarred(Boolean starred)
    {
        this.starred = starred;
    }

    public Date getAddedDT()
    {
        return addedDT;
    }

    public void setAddedDT(Date addedDT)
    {
        this.addedDT = addedDT;
    }

    public User getAddedBy()
    {
        return addedBy;
    }

    public void setAddedBy(User addedBy)
    {
        this.addedBy = addedBy;
    }

    public Date getApprovedDT()
    {
        return approvedDT;
    }

    public void setApprovedDT(Date approvedDT)
    {
        this.approvedDT = approvedDT;
    }

    public User getApprovedBy()
    {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy)
    {
        this.approvedBy = approvedBy;
    }

    public Set<User> getAuthors()
    {
        return authors;
    }

    public void setAuthors(Set<User> authors)
    {
        this.authors = authors;
    }

    public String getCoverDescription()
    {
        return coverDescription;
    }

    public void setCoverDescription(String coverDescription)
    {
        this.coverDescription = coverDescription;
    }

    public Image getCover()
    {
        return cover;
    }

    public void setCover(Image coverImage)
    {
        this.cover = coverImage;
    }


    @Override
    public String toString()
    {
        return contentType + "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public Set<Church> getChurches()
    {
        return churches;
    }

    public void setChurches(Set<Church> churches)
    {
        this.churches = churches;
    }

    public String getAlt()
    {
        return alt;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }

    public String getCoverThumbPath()
    {
        return coverThumbPath;
    }

    public void setCoverThumbPath(String coverThumbPath)
    {
        this.coverThumbPath = coverThumbPath;
    }

    public String getHoverThumbPath()
    {
        return hoverThumbPath;
    }

    public void setHoverThumbPath(String hoverThumbPath)
    {
        this.hoverThumbPath = hoverThumbPath;
    }

    public Church getDedicatedChurch()
    {
        return dedicatedChurch;
    }

    public void setDedicatedChurch(Church dedicatedChurch)
    {
        this.dedicatedChurch = dedicatedChurch;
    }
}
