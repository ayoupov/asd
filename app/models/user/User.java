package models.user;

import org.hibernate.annotations.Table;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:50
 */
@Entity(name="Users")
public class User
{
    @Id
    public String id;   // external id

    public String name;

    public UserRole role;

    public UserStatus status;

    public User(String name, UserRole role, UserStatus status)
    {
        this.name = name;
        this.role = role;
        this.status = status;
    }

    public User()
    {
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
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

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
