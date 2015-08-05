package models;

import models.user.User;
import models.user.UserRole;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:54
 */
@Entity
@Indexed
public class MediaContent
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public MediaContentType contentType;
    @Field
    public String text;
    @Field
    public String lead;
    @Field
    public String title;
    @Field
    public Boolean starred;

    @Column(name = "added_dt")
    public Date addedDT;

    @JoinColumn(name = "added_by")
    @OneToOne
    public User addedBy;

    @Column(name = "approved_dt")
    public Date approvedDT;

    @OneToOne
    @JoinColumn(name = "approved_by")
    public User approvedBy;

    public MediaContent(MediaContentType contentType, String text, String lead, String title, Boolean starred, User addedBy)
    {
        this.contentType = contentType;
        this.text = text;
        this.lead = lead;
        this.title = title;
        this.starred = starred;
        this.addedBy = addedBy;
        this.addedDT = new Date();
        if (addedBy != null && (addedBy.role == UserRole.Administrator || addedBy.role == UserRole.Moderator))
        {
            approvedBy = addedBy;
            approvedDT = addedDT;
        }
    }

    public MediaContent()
    {
    }
}
