package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:52
 */
@Entity
@Table(name = "architect")
public class Architect
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "architects")
    @JsonIgnore
    private Set<Church> churches;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Set<Church> getChurches()
    {
        return churches;
    }

    public void setChurches(Set<Church> churches)
    {
        this.churches = churches;
    }

    @Override
    public String toString()
    {
        return "Architect{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
