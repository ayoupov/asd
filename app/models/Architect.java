package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    public Long id;

    public String name;

}
