package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:47
 */
@Entity
public class Image
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String description;

    public String path;

    public Image(String description, String path)
    {
        this.description = description;
        this.path = path;
    }

    public Image()
    {
//        System.out.println("Image: No args constructor called!");
    }
}
