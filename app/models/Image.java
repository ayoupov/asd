package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import models.user.User;
import models.user.UserRole;
import utils.serialize.converters.DateTimeConverter;

import javax.persistence.*;
import java.util.Date;

import static utils.HibernateUtils.save;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:47
 */
@Entity
@Table(name = "image")
public class Image
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String path;

    @OneToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "uploaded_ts")
//    @JsonSerialize(using = DateTimeConverter.class)
    private Date uploadedTS;

    @OneToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_ts")
//    @JsonSerialize(using = DateTimeConverter.class)
    private Date approvedTS;

    public Image(String description, String path)
    {
        this.description = description;
        this.path = path;
        this.uploadedTS = new Date();
    }

    public Image()
    {
    }

    public Image(String filename, User user)
    {
        this(filename, user, null);
    }

    public Image(String filename, User user, String description)
    {
        path = filename;
        uploadedBy = user;
        uploadedTS = new Date();
        if (user != null && (user.getRole() == UserRole.Administrator || user.getRole() == UserRole.Moderator)) {
            approvedBy = user;
            approvedTS = uploadedTS;
        }
        if (description == null)
            this.description = "uploaded by " + user + " at " + uploadedTS;
        else
            this.description = description;
        this.id = (Long) save(this);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public User getUploadedBy()
    {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy)
    {
        this.uploadedBy = uploadedBy;
    }

    public Date getUploadedTS()
    {
        return uploadedTS;
    }

    public void setUploadedTS(Date uploadedTS)
    {
        this.uploadedTS = uploadedTS;
    }

    public User getApprovedBy()
    {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy)
    {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedTS()
    {
        return approvedTS;
    }

    public void setApprovedTS(Date approvedTS)
    {
        this.approvedTS = approvedTS;
    }

    @Override
    public String toString()
    {
        return "Image{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", path='" + path + '\'' +
                ", uploadedBy=" + uploadedBy +
                ", uploadedTS=" + uploadedTS +
                ", approvedBy=" + approvedBy +
                ", approvedTS=" + approvedTS +
                '}';
    }


    public void disapprove(User who)
    {
        approve(who, null);
    }

    public void approve(User who)
    {
        approve(who, new Date());
    }

    public void approve(User who, Date when)
    {
        setApprovedTS(when);
        setApprovedBy(who);
    }

}
