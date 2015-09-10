package models.user;

import javax.persistence.*;

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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(unique = true)
    public String socialId;   // external id

    public String name;

    public UserRole role;

    public UserStatus status;

    public User(String name, UserRole role, UserStatus status, String socialId)
    {
        this.name = name;
        this.role = role;
        this.status = status;
        this.socialId = socialId;
    }

    public User()
    {
    }

    public void setSocialId(String id)
    {
        this.socialId = socialId;
    }

    public String getSocialId()
    {
        return socialId;
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
                ", socialId='" + socialId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
