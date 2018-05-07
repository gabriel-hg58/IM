package utils;

import controller.HelpJpaController;
import controller.UserAccountJpaController;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.Help;
import model.UserAccount;
import view.EmProvider;

public class SendMail {

    //Control
    HelpJpaController controlHelp = new HelpJpaController(EmProvider.getInstance().getEntityManagerFactory());
    UserAccountJpaController controlUser = new UserAccountJpaController(EmProvider.getInstance().getEntityManagerFactory());
    //Auxiliary
    Help actualHelp = new Help();
    UserAccount actualUser = new UserAccount();
    private final String systemMail = "integracaomunicipal@gmail.com";
    private final String systemPassword = "controlemunicipal2018";

    public void sendMailToSystem(int idHelp, String user) {
        actualHelp = controlHelp.findHelp(idHelp);
        actualUser = controlUser.findUserAccount(user);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session s = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(systemMail, systemPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(s);
            message.setFrom(new InternetAddress(systemMail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(systemMail));

            message.setSubject(actualHelp.getTitleHelp());
            message.setContent(actualHelp.getDescription()
                    + "<br><br>De: " + actualUser.getName()
                    + " - " + actualUser.getDepartmentIdDepartment().getName(),
                    "text/html; charset=utf-8");

            //send message  
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMailToUser(String mail, String title, String msg){

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session s = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(systemMail, systemPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(s);
            message.setFrom(new InternetAddress(systemMail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));

            message.setSubject(title);
            message.setContent(msg
                    + "<br><br>Sistema Integração Municipal - Sertaneja PR",
                    "text/html; charset=utf-8");

            //send message  
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
