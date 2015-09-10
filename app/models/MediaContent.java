package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import models.user.User;
import models.user.UserRole;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import utils.serialize.DateTimeConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:54
 */
@Entity
@Table(name = "content")
@Indexed
public class MediaContent
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public MediaContentType contentType;
    @Field
    @Type(type = "text")
    public String text;
    @Field
    @Type(type = "text")
    public String lead;
    @Field
    public String coverDescription;
    @Field
    public String title;
    @Field
    public Boolean starred;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Image coverImage;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Image> images;

    @Column(name = "added_dt")
    public Date addedDT;

    @JoinColumn(name = "added_by")
    @OneToOne
    public User addedBy;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    public List<User> authors;

    @Column(name = "approved_dt")
    @JsonSerialize(using = DateTimeConverter.class)
    public Date approvedDT;

    @OneToOne
    @JoinColumn(name = "approved_by")
    public User approvedBy;

    public MediaContent(MediaContentType contentType, String text, String lead, String title, Boolean starred, List<User> authors, User addedBy)
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

    public List<User> getAuthors()
    {
        return authors;
    }

    public void setAuthors(List<User> authors)
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

    public Image getCoverImage()
    {
        return coverImage;
    }

    public void setCoverImage(Image coverImage)
    {
        this.coverImage = coverImage;
    }

    public Set<Image> getImages()
    {
        return images;
    }

    public void setImages(Set<Image> images)
    {
        this.images = images;
    }

    @Override
    public String toString()
    {
        return contentType + "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
