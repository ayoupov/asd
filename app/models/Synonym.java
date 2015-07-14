package models;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 03.07.2015
 * Time: 23:46
 */
@Embeddable
@Entity
public class Synonym
{
    @Id
    public Long id;

    @IndexedEmbedded
    @ElementCollection
    public Set<String> synset = new LinkedHashSet<String>();
}
