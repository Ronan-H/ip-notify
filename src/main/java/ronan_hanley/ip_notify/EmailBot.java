package ronan_hanley.ip_notify;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailBot {
    private static String USER_NAME = "username"; // Mail.com email address
    private static String PASSWORD = "password"; // Mail.com password
    private static String RECIPIENT = "recipient";

    public static void sendIPChangeNotification(String fromIP, String toIP, int retryTimeout) {
        String from = USER_NAME;
        String pass = PASSWORD;
        String[] to = { RECIPIENT }; // list of recipient email addresses
        String subject = "Notification of IP Address Change";
        String body = "Hello,\n\n"
                + "This is an automated email to inform you that an IP address change has been detected.\n"
                + String.format("It appears to have changed from %s to %s.\n", fromIP.replace(".", "-"), toIP.replace(".", "-"))
                + "Don't forget to re-launch IP Notify using the new IP address to get continued notifications of changes of IP.\n";

        sendFromGMail(from, pass, to, subject, body, retryTimeout);
    }

    public static void sendFromGMail(String from, String pass, String[] to, String subject, String body, int retryTimeout) {
        boolean messageSent = false;

        Properties props = System.getProperties();
        String host = "smtp.mail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        while (!messageSent) {
            try {
                message.setFrom(new InternetAddress(from));
                InternetAddress[] toAddress = new InternetAddress[to.length];

                // To get the array of addresses
                for( int i = 0; i < to.length; i++ ) {
                    toAddress[i] = new InternetAddress(to[i]);
                }

                for( int i = 0; i < toAddress.length; i++) {
                    message.addRecipient(Message.RecipientType.TO, toAddress[i]);
                }

                message.setSubject(subject);
                message.setText(body);
                Transport transport = session.getTransport("smtp");
                transport.connect(host, from, pass);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                messageSent = true;
                System.out.println("Successfully sent the notification email.");
            }
            catch (MessagingException e) {
                System.out.printf("Some error occurred while trying to send the email, sleeping for %d seconds before trying again...%n", retryTimeout);

                e.printStackTrace();

                try {
                    Thread.sleep(retryTimeout * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}