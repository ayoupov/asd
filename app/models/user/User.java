package models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.LinkedAccount;
import models.MediaContent;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:50
 */
@Entity
@Table(name = "users")
public class User
{
    private static final String ANONYMOUS_HASH = "000000";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String hash;

    @Constraints.Email
    // if you make this unique, keep in mind that users *must* merge/link their
    // accounts then on signup with additional providers
    // @Column(unique = true)
    @JsonIgnore
    private String email;

    private String name;

    @JsonIgnore
    private UserRole role;

    @JsonIgnore
    private UserStatus status;

    @Column(name="email_validated")
    @JsonIgnore
    private Boolean emailValidated = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="user")
    @JsonIgnore
    private List<LinkedAccount> linkedAccounts;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="authors")
    @JsonIgnore
    private Set<MediaContent> authorOf;

    public User(String name, UserRole role, UserStatus status)
    {
        this.name = name;
        this.role = role;
        this.status = status;
    }

    public User()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UserRole getRole()
    {
        return role;
    }

    public void setRole(UserRole role)
    {
        this.role = role;
    }

    public UserStatus getStatus()
    {
        return status;
    }

    public void setStatus(UserStatus status)
    {
        this.status = status;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public List<LinkedAccount> getLinkedAccounts()
    {
        return linkedAccounts;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isEmailValidated()
    {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated)
    {
        this.emailValidated = emailValidated;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts)
    {
        this.linkedAccounts = linkedAccounts;
    }

    public Set<MediaContent> getAuthorOf()
    {
        return authorOf;
    }

    public void setAuthorOf(Set<MediaContent> authorOf)
    {
        this.authorOf = authorOf;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public Boolean getEmailValidated()
    {
        return emailValidated;
    }

    public void setEmailValidated(Boolean emailValidated)
    {
        this.emailValidated = emailValidated;
    }

    public static String anonymousHash()
    {
        return ANONYMOUS_HASH;
    }
}
