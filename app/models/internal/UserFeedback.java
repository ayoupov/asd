package models.internal;

import models.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 01.11.2015
 * Time: 20:06
 */
@Entity
@Table(name="user_feedback")
public class UserFeedback
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne()
    @JoinColumn(name = "suggested_by")
    private User suggestedBy;

    @Type(type = "text")
    private String comment;

    private transient String name;

    @Column(name = "suggested_on_dt")
    private Date suggestedOn;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public User getSuggestedBy()
    {
        return suggestedBy;
    }

    public void setSuggestedBy(User suggestedBy)
    {
        this.suggestedBy = suggestedBy;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getSuggestedOn()
    {
        return suggestedOn;
    }

    public void setSuggestedOn(Date suggestedOn)
    {
        this.suggestedOn = suggestedOn;
    }
}
