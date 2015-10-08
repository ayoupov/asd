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
import utils.serialize.DateTimeConverter;

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
    public Long id;

    @Column(name = "content_type")
    @Field
    public MediaContentType contentType;

    @Field
    @Type(type = "text")
    @Analyzer(definition = "polish_def_analyzer")
    public String text;

    @Field
    @Type(type = "text")
    @Analyzer(definition = "polish_def_analyzer")
    public String lead;

    @Column(name = "cover_description")
    @Field
    public String coverDescription;

    @Field
    @Analyzer(definition = "polish_def_analyzer")
    public String title;

    private Integer year;

    public Boolean starred;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cover_image_id")
    public Image cover;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cover_image_thumb_id")
    public Image coverThumb;

    //    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    public Set<Image> images;
    @Column(name = "added_dt")
    public Date addedDT;

    @OneToOne
    @JoinColumn(name = "added_by")
    public User addedBy;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "content_authors")
    public Set<User> authors;

    @Column(name = "approved_dt")
    @JsonSerialize(using = DateTimeConverter.class)
    public Date approvedDT;

    @OneToOne
    @JoinColumn(name = "approved_by")
    public User approvedBy;

    @ManyToMany(mappedBy = "media")
    @JsonIgnore
    public Set<Church> churches;

    public MediaContent(MediaContentType contentType, String text, String title, Integer year, Image cover, Image coverThumb, User addedBy, Church church)
    {
        this.contentType = contentType;
        this.text = text;
        this.title = title;
        this.year = year;
        this.cover = cover;
        this.coverThumb = coverThumb;
        this.authors = Collections.singleton(addedBy);
        this.addedBy = addedBy;
        this.addedDT = new Date();
        if (addedBy != null && (addedBy.role == UserRole.Administrator || addedBy.role == UserRole.Moderator)) {
            approvedBy = addedBy;
            approvedDT = addedDT;
        }
        this.churches = Collections.singleton(church);
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
        if (addedBy != null && (addedBy.role == UserRole.Administrator || addedBy.role == UserRole.Moderator)) {
            approvedBy = addedBy;
            approvedDT = addedDT;
        }
    }

    public MediaContent()
    {
    }

    public MediaContent(MediaContentType mediaContentType)
    {
        this.contentType = mediaContentType;
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

//    public Set<Image> getImages()
//    {
//        return images;
//    }
//
//    public void setImages(Set<Image> images)
//    {
//        this.images = images;
//    }

    @Override
    public String toString()
    {
        return contentType + "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public void setCoverThumb(Image coverThumb)
    {
        this.coverThumb = coverThumb;
    }
}
