package models;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 02.07.2015
 * Time: 1:04
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import models.address.Address;
import models.internal.ChurchSuggestion;
import models.internal.UserManager;
import models.user.User;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.stempel.StempelPolishStemFilterFactory;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;
import utils.serialize.CollectionToCSVBridge;
import utils.serialize.converters.SynonymConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
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
//                @TokenFilterDef(factory =  StopFilterFactory.class, params = {
//                        @Parameter(name = "words",
//                                value = "models/internal/search/church_stoplist.properties"),
//                        @Parameter(name = "ignoreCase", value = "true")
//                }),
                @TokenFilterDef(factory = StempelPolishStemFilterFactory.class)
                ,@TokenFilterDef(factory = EdgeNGramFilterFactory.class, params =
                        {
                                @Parameter(name= "minGramSize", value = "3"),
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

    @Field
    @Analyzer(definition = "polish_def_analyzer")
    public String name;

    /**
     * working relates to church status
     */
//    public boolean working;

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
    @OneToOne
    public Address address;

    @OneToOne
    public GSV gsv;

    @ManyToMany(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<Architect> architects;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy("uploadedTS")
    public List<Image> images;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
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

    @Field
    @IndexedEmbedded
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="synsets")
    @JsonSerialize(using = SynonymConverter.class)
//    @FieldBridge(impl=CollectionToCSVBridge.class)
    @Analyzer(definition = "polish_def_analyzer")
    public Set<String> synonyms = new LinkedHashSet<String>();

    @OneToMany(mappedBy = "relatedChurch")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Set<ChurchSuggestion> requests;

    boolean useCustomGSV = false;

    boolean useUserAddress = false;

    @JsonIgnore
    boolean wasPublished;

    // only for internal update!
    public Church(String extID, String name, Address address)
    {
        this(extID, name, null, null, address, null, null, null, UserManager.getAutoUser());
        approvedBy = addedBy;
        approvedDT = new Date();
    }

    public Church(String extID, User by)
    {
        setAddedBy(by);
        setAddedDT(new Date());
    }

    public Church(String extID, String name, Integer constructionStart, Integer constructionEnd,
                  Address address, Set<Architect> architects, List<Image> images, Set<MediaContent> media, User addedBy)
    {
        this.extID = extID;
        this.name = name;
//        this.working = working;
        this.constructionStart = constructionStart;
        this.constructionEnd = constructionEnd;
        this.address = address;
        this.architects = architects;
        this.images = images;
        this.media = media;
        this.enabled = true;
        this.addedBy = addedBy;
        this.addedDT = new Date();
        this.synonyms = new LinkedHashSet<>();
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

//    public boolean isWorking()
//    {
//        return working;
//    }

//    public void setWorking(boolean working)
//    {
//        this.working = working;
//    }

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

    public List<Image> getImages()
    {
        return images;
    }

    public void setImages(List<Image> images)
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

    public Set<String> getSynonyms()
    {
        return synonyms;
    }

    public void setSynonyms(Set<String> synset)
    {
        this.synonyms = synset;
    }

    public Set<ChurchSuggestion> getRequests()
    {
        return requests;
    }

    public void setRequests(Set<ChurchSuggestion> requests)
    {
        this.requests = requests;
    }

    public GSV getGsv()
    {
        return gsv;
    }

    public void setGsv(GSV gsv)
    {
        this.gsv = gsv;
    }

    public boolean isUseCustomGSV()
    {
        return useCustomGSV;
    }

    public void setUseCustomGSV(boolean useCustomGSV)
    {
        this.useCustomGSV = useCustomGSV;
    }

    public boolean isUseUserAddress()
    {
        return useUserAddress;
    }

    public void setUseUserAddress(boolean useUserAddress)
    {
        this.useUserAddress = useUserAddress;
    }

    public void disapprove(User who)
    {
        approve(who, null);
    }

    public void approve(User who)
    {
        approve(who, new Date());
    }

    public void approve(User who, Date when)
    {
        setWasPublished(true);
        setApprovedDT(when);
        setApprovedBy(who);
    }

    public boolean isWasPublished()
    {
        return wasPublished;
    }

    public void setWasPublished(boolean wasPublished)
    {
        this.wasPublished = wasPublished;
    }
}
