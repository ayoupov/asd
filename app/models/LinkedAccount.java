package models;

import models.user.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "account")
public class LinkedAccount implements Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    public User user;

    @Column(name="provider_user_id")
    public String providerUserId;
    @Column(name="provider_name")
    public String providerKey;

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getProviderUserId()
    {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId)
    {
        this.providerUserId = providerUserId;
    }

    public String getProviderKey()
    {
        return providerKey;
    }

    public void setProviderKey(String providerKey)
    {
        this.providerKey = providerKey;
    }


    @Override
    public String toString()
    {
        return providerKey;
    }
}