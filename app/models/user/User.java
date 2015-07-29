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
}
