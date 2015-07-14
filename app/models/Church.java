package models;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 02.07.2015
 * Time: 1:04
 */

import models.address.Address;
import models.internal.UserManager;
import models.user.User;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Indexed
@AnalyzerDef(name="polish_def_analyzer",
        charFilters = {
                @CharFilterDef(factory = MappingCharFilterFactory.class, params = {
                        @Parameter(name = "mapping",
                                value = "models/internal/search/polish_mapping-chars.properties")
                })
        },
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class, params = {
                        @Parameter(name="words",
                                value= "models/internal/search/church_stoplist.properties" ),
                        @Parameter(name="ignoreCase", value="true")
                })
        })
public class Church
{
    @Id
    public String extID;

    @Field
    @Analyzer(definition = "polish_def_analyzer")
    public String name;

    /**
     * working relates to church status
     */
    public boolean working;

    /**
     * enabled relates to view availability
     */
    public boolean enabled;

    public Integer constructionStart;
    public Integer constructionEnd;

    @IndexedEmbedded
    @Analyzer(definition = "polish_def_analyzer")
    @OneToMany(cascade = CascadeType.ALL)
    public Set<Synonym> synonyms;

    @IndexedEmbedded
    @Analyzer(definition = "polish_def_analyzer")
    @OneToOne
    public Address address;

    @OneToMany(cascade = CascadeType.MERGE)
    public Set<Architect> architects;

    @OneToMany(cascade = CascadeType.ALL)
    public Set<Image> images;

    @OneToMany(cascade = CascadeType.ALL)
    public Set<MediaContent> media;

    @OneToOne
    @JoinColumn(name = "added_by")
    public User addedBy;

    @Column(name = "added_dt")
    public Date addedDT;

    @OneToOne
    @JoinColumn(name = "approved_by")
    public User approvedBy;

    @Column(name = "approved_dt")
    public Date approvedDT;

    // only for internal update!
    public Church(String extID, String name, Address address)
    {
        this(extID, name, false, null, null, address, null, null, null, UserManager.getAutoUser());
        approvedBy = addedBy;
        approvedDT = new Date();
    }

    public Church(String extID, String name, boolean working, Integer constructionStart, Integer constructionEnd,
                  Address address, Set<Architect> architects, Set<Image> images, Set<MediaContent> media, User addedBy)
    {
        this.extID = extID;
        this.name = name;
        this.working = working;
        this.constructionStart = constructionStart;
        this.constructionEnd = constructionEnd;
        this.address = address;
        this.architects = architects;
        this.images = images;
        this.media = media;
        this.enabled = true;
        this.addedBy = addedBy;
        this.addedDT = new Date();
    }

    public Church()
    {
//        System.out.println("Church: No args constructor called!");
    }

    public void setId(String id)
    {
        this.extID = id;
    }

    @Override
    public String toString()
    {
        return "Church{" +
                "id='" + extID + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
