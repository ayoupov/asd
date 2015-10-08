package models;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 02.07.2015
 * Time: 1:04
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.address.Address;
import models.internal.UserManager;
import models.user.User;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.stempel.StempelPolishStemFilterFactory;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "church")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Indexed
@AnalyzerDef(name = "polish_def_analyzer",
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
                        @Parameter(name = "words",
                                value = "models/internal/search/church_stoplist.properties"),
                        @Parameter(name = "ignoreCase", value = "true")
                }),
                @TokenFilterDef(factory = StempelPolishStemFilterFactory.class),
                @TokenFilterDef(factory = EdgeNGramFilterFactory.class, params =
                        {
                                @Parameter(name= "minGramSize", value = "2"),
                                @Parameter(name= "maxGramSize", value = "8")
                        })

        })
// todo: ensure availability of objects via search
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // dirty hack to avoid serialization of proxies
public class Church
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(name="ext_id")
    public String extID;

    public int version;

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

    @Column(name="cons_start")
    public Integer constructionStart;
    @Column(name="cons_end")
    public Integer constructionEnd;

    @IndexedEmbedded
    @Analyzer(definition = "polish_def_analyzer")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    public Set<Synonym> synonyms;

    @IndexedEmbedded
    @Analyzer(definition = "polish_def_analyzer")
    @OneToOne
    public Address address;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    public Set<Architect> architects;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Image> images;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="church_media")
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

    public String website;

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
        this.version = 0;
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

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getExtID()
    {
        return extID;
    }

    public void setExtID(String extID)
    {
        this.extID = extID;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isWorking()
    {
        return working;
    }

    public void setWorking(boolean working)
    {
        this.working = working;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Integer getConstructionStart()
    {
        return constructionStart;
    }

    public void setConstructionStart(Integer constructionStart)
    {
        this.constructionStart = constructionStart;
    }

    public Integer getConstructionEnd()
    {
        return constructionEnd;
    }

    public void setConstructionEnd(Integer constructionEnd)
    {
        this.constructionEnd = constructionEnd;
    }

    public Set<Synonym> getSynonyms()
    {
        return synonyms;
    }

    public void setSynonyms(Set<Synonym> synonyms)
    {
        this.synonyms = synonyms;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public Set<Architect> getArchitects()
    {
        return architects;
    }

    public void setArchitects(Set<Architect> architects)
    {
        this.architects = architects;
    }

    public Set<Image> getImages()
    {
        return images;
    }

    public void setImages(Set<Image> images)
    {
        this.images = images;
    }

    public Set<MediaContent> getMedia()
    {
        return media;
    }

    public void setMedia(Set<MediaContent> media)
    {
        this.media = media;
    }

    public User getAddedBy()
    {
        return addedBy;
    }

    public void setAddedBy(User addedBy)
    {
        this.addedBy = addedBy;
    }

    public Date getAddedDT()
    {
        return addedDT;
    }

    public void setAddedDT(Date addedDT)
    {
        this.addedDT = addedDT;
    }

    public User getApprovedBy()
    {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy)
    {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedDT()
    {
        return approvedDT;
    }

    public void setApprovedDT(Date approvedDT)
    {
        this.approvedDT = approvedDT;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }
}
