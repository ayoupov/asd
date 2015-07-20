package models;

import models.user.User;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

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
    public Long id;

    public MediaContentType type;
    @Field
    public String text;
    @Field
    public String lead;
    @Field
    public String caption;
    @Field
    public Boolean starred;

    @OneToMany
    public Set<Image> images;

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
}
