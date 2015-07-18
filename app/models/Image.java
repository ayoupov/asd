package models;

import models.user.User;

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

    public Image(String description, String path)
    {
        this.description = description;
        this.path = path;
    }

    public Image()
    {
    }

    public Image(String filename, File file, User user)
    {
        String path = "/public/images/" + filename;
        file.renameTo(new File(path));
        uploadedBy = user;
        uploadedTS = new Date();
        description = "uploaded by " + user + " at " + uploadedTS;
        save(this);
    }

    public Long getId()
    {
        return id;
    }
}
