package service;

import controller.MailController;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import model.Mail;
import sdccFoodDelivery.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class MailService implements MailServiceInterface {

    private final String MAIL_USERNAME = "luca.menzolini@gmail.com";
    private final String MAIL_PASSWORD = "dprlslsfyapgxwtz";
    private MailController mailController;

    public MailService() {
        this.mailController = new MailController();
    }

    //Update an existing mail
    private boolean modifyMail(Mail newMail) {
        if (!this.mailController.deleteByID(newMail.getId())) return false;
        return this.mailController.save(newMail);
    }

    //Update mail's object
    @Override
    public boolean updateObject(String newObject, String tag) {
        Mail newMail;
        if ((newMail = this.mailController.findByTag(tag)) == null) return false;
        newMail.setObject(newObject);
        return this.modifyMail(newMail);
    }

    //Update mail's message
    @Override
    public boolean updateMessage(String newMessage, String tag) {
        Mail newMail;
        if ((newMail = this.mailController.findByTag(tag)) == null) return false;
        newMail.setObject(newMessage);
        return this.modifyMail(newMail);
    }

    //Update mail's attachment
    @Override
    public boolean updateAttachment(String newAttachment, String tag) {
        Mail newMail;
        if ((newMail = this.mailController.findByTag(tag)) == null) return false;
        newMail.setObject(newAttachment);
        return this.modifyMail(newMail);
    }

    //Send mail
    @Override
    public boolean SendMail(String tag, int userID) {

        //Ask user service for user's mail
        String address;
        if ((address = this.getUserMail(userID)) == null) return false;

        //retrieve mail from DB
        Mail mail;
        if ((mail = this.mailController.findByTag(tag)) == null) return false;

        //Set properties
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL_USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(address)
            );
            message.setSubject(mail.getObject());
            message.setText(mail.getMessage());

            //send
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getUserMail(int userID) {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        sdcc_user_serviceGrpc.sdcc_user_serviceBlockingStub blockingStub = sdcc_user_serviceGrpc.newBlockingStub(channel);

        UserMessage response;
        IDMessage request = IDMessage.newBuilder().setId(userID).build();

        response = blockingStub.findByID(request);
        channel.shutdown();
        if (response == null){
            return null;
        }
        return response.getMail();
    }
}
