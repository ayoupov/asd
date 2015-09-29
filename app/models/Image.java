package models;

import models.user.User;
import models.user.UserRole;

import javax.persistence.*;
import java.io.File;
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
    public Long id;

    public String description;

    public String path;

    @OneToOne
    @JoinColumn(name = "uploaded_by")
    public User uploadedBy;

    @Column(name = "uploaded_ts")
    public Date uploadedTS;

    @OneToOne
    @JoinColumn(name = "approved_by")
    public User approvedBy;

    @Column(name = "approved_ts")
    public Date approvedTS;

    public Image(String description, String path)
    {
        this.description = description;
        this.path = path;
    }

    public Image()
    {
    }

    public Image(String filename, User user)
    {
        path = filename;
        uploadedBy = user;
        uploadedTS = new Date();
        if (user != null && (user.getRole() == UserRole.Administrator || user.getRole() == UserRole.Moderator))
        {
            approvedBy = user;
            approvedTS = uploadedTS;
        }
        description = "uploaded by " + user + " at " + uploadedTS;
        save(this);
    }

    public Long getId()
    {
        return id;
    }
}
