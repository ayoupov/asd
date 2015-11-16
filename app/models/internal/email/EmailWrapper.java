package models.internal.email;

import models.internal.ContentManager;
import models.user.User;
import models.user.UserStatus;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Logger;
import utils.ServerProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 07.11.2015
 * Time: 11:16
 */
public class EmailWrapper
{

    private static final String defaultSenderName = "Zespół projektu Architektura VII Dnia";

    public static void sendEmail(String templateName, String senderName, User to, Pair<String, String>... substitutions) throws MalformedURLException, EmailException
    {
        if (!to.getUnsubscribed() && to.getStatus() == UserStatus.Active) {
            Logger.info("sending email with these substitutions: " + Arrays.deepToString(substitutions));
            EmailTemplate et = ContentManager.getEmailTemplateByName(templateName);
            sendEmail(
                    et.getProcessedBody(substitutions),
                    et.getProcessedSubject(substitutions),
                    senderName,
                    to
            );
        }
    }

    public static void sendEmail(String processedEmailTemplate, String subject, String senderName, User to) throws EmailException, MalformedURLException
    {
        // Create the email message
        HtmlEmail email = new HtmlEmail();
//        ImageHtmlEmail email = new ImageHtmlEmail();

        URL url = new URL(ServerProperties.getValue("asd.absolute.url"));
//        email.setDataSourceResolver(new DataSourceUrlResolver(url));

        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setStartTLSEnabled(true);
        email.setAuthentication(ServerProperties.getValue("asd.email"), ServerProperties.getValue("asd.email.pwd"));

        email.setCharset("UTF-8");

        email.setFrom(
                ServerProperties.getValue("asd.email"), senderName == null ? defaultSenderName : senderName,
                "UTF-8");
        email.addTo(to.getEmail(), to.getName(), "UTF-8");

        email.setSubject(subject);

        // set the html message
        email.setHtmlMsg(processedEmailTemplate);

        // set the alternative message
        email.setTextMsg("Your email client does not support HTML messages");

        // send the email
        email.send();
    }

    public static class EmailNames
    {
        public static final String AddStory = "story_sent";
        public static final String ApproveStory = "story_approved";
        public static final String UserRegistered = "greet";
        public static final String PassportFixed = "passport_fixed";
        public static final String ChurchApproved = "church_added";
    }
}
