package models.internal.email;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.LinkedHashMap;
import java.util.Map;

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

    static {
        subsRegexps.put(EmailSubstitution.Username.name(), "\\$USERNAME");
        subsRegexps.put(EmailSubstitution.ChurchLink.name(), "\\$CHURCH");
        subsRegexps.put(EmailSubstitution.ChurchPassportLink.name(), "\\$CHURCHPASSPORT");
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
        for (Pair<String, String> substitution : substitutions) {
            input = input.replaceAll(subsRegexps.get(substitution.getLeft()), substitution.getRight());
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
