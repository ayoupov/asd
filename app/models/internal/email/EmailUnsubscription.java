package models.internal.email;

import models.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 09.11.2015
 * Time: 8:41
 */
@Entity
@Table(name = "email_unsubscription")
public class EmailUnsubscription
{
    @Id
    private String hash;

    @Column(name = "added_dt")
    private Date addedDT;

    @Column(name = "unsubscribed_dt")
    private Date unsubscribedDT;

    @OneToOne
    @JoinColumn(name="subscriber")
    private User subscriber;

    public EmailUnsubscription()
    {

    }

    public EmailUnsubscription(User user, String hash)
    {
        this.subscriber = user;
        this.hash = hash;
        this.addedDT = new Date();
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public Date getAddedDT()
    {
        return addedDT;
    }

    public void setAddedDT(Date addedDT)
    {
        this.addedDT = addedDT;
    }

    public Date getUnsubscribedDT()
    {
        return unsubscribedDT;
    }

    public void setUnsubscribedDT(Date unsubscribedDT)
    {
        this.unsubscribedDT = unsubscribedDT;
    }

    public User getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(User subscriber)
    {
        this.subscriber = subscriber;
    }
}
