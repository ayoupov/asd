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
    public User user;

    public String providerUserId;
    public String providerKey;

}