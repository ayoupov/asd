package models.internal.email;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import utils.ServerProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import controllers.routes;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.11.2015
 * Time: 11:39
 */
@Entity
@Table(name = "email_template")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmailTemplate
{
    @Id
    String name;

    @Type(type = "text")
    String body;

    String subject;

    private static Map<String, String> subsRegexps = new LinkedHashMap<>();

    @Transient
    private static Set<Pair<String, String>> commonSubs = new HashSet<>();

    static {
        subsRegexps.put(EmailSubstitution.Username.name(), "\\$USERNAME");
        subsRegexps.put(EmailSubstitution.ChurchName.name(), "\\$CHURCHNAME");
        subsRegexps.put(EmailSubstitution.ChurchPassportLink.name(), "\\$CHURCHPASSPORTLINK");
        subsRegexps.put(EmailSubstitution.ChurchPassportAdd.name(), "\\$CHURCHADDCONTENTLINK");
        subsRegexps.put(EmailSubstitution.UnsubscribeLink.name(), "\\$UNSUBSCRIBELINK");

        subsRegexps.put(EmailSubstitution.FacebookPage.name(), "\\$FACEBOOKPAGE");
        subsRegexps.put(EmailSubstitution.ArticlesLink.name(), "\\$ARTICLESLINK");
        subsRegexps.put(EmailSubstitution.StoriesLink.name(), "\\$STORIESLINK");

        commonSubs.add(Pair.of(EmailSubstitution.FacebookPage.name(), ServerProperties.getValue("facebook.url")));
        commonSubs.add(Pair.of(EmailSubstitution.ArticlesLink.name(), ServerProperties.getValue("asd.absolute.url") + "#slide-articles"));
        commonSubs.add(Pair.of(EmailSubstitution.StoriesLink.name(), ServerProperties.getValue("asd.absolute.url") + "#slide-stories"));
    }


    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProcessedSubject(Pair<String, String>... substitutions)
    {
        return substitute(subject, substitutions);
    }

    public String getProcessedBody(Pair<String, String>... substitutions)
    {
        return substitute(body, substitutions);
    }

    public String substitute(String input, Pair<String, String>... substitutions)
    {
        // context-dependent
        for (Pair<String, String> substitution : substitutions) {
//            System.out.println("trying to apply: " + substitution);
            input = input.replaceAll(subsRegexps.get(substitution.getLeft()), substitution.getRight());
        }
        // content-independent
        for (Pair<String, String> substitution : commonSubs) {
            input= input.replaceAll(subsRegexps.get(substitution.getLeft()), substitution.getRight());
        }
        return input;
    }

    @Override
    public String toString()
    {
        return "EmailTemplate{" +
                "name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
