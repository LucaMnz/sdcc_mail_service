package service;

import controller.MailController;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import model.Mail;
import sdccFoodDelivery.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MailService {


    public MailController mailController;
    static sdcc_user_serviceGrpc.sdcc_user_serviceBlockingStub blockingStub;

    public MailService(Channel channel) {

        this.mailController = new MailController();
        this.blockingStub = sdcc_user_serviceGrpc.newBlockingStub(channel);
    }

    private static final Logger logger = Logger.getLogger(MailService.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new MailServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    MailService.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

   /* private void run(ManagedChannel channel) {


        System.out.println("Starting client");

        //metodo da chiamare

        System.out.println("Shutting down channel");
        channel.shutdown();
    }*/

    //Update an existing mail
    private boolean modifyMail(Mail newMail) {
        if (!this.mailController.deleteByID(newMail.getId())) return false;
        return this.mailController.save(newMail);
    }

    //Update mail's object
    public boolean updateObject(String newObject, String tag) {
        Mail newMail;
        if ((newMail = this.mailController.findByTag(tag)) == null) return false;
        newMail.setObject(newObject);
        return this.modifyMail(newMail);
    }

    //Update mail's message
    public boolean updateMessage(String newMessage, String tag) {
        Mail newMail;
        if ((newMail = this.mailController.findByTag(tag)) == null) return false;
        newMail.setObject(newMessage);
        return this.modifyMail(newMail);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001)
                .usePlaintext()
                .build();

        final MailService server = new MailService(channel);

       // final MailService client = new MailService(channel);
       // client.run(channel);
        server.start();
        server.blockUntilShutdown();
    }

    //Update mail's attachment
    public boolean updateAttachment(String newAttachment, String tag) {
        Mail newMail;
        if ((newMail = this.mailController.findByTag(tag)) == null) return false;
        newMail.setObject(newAttachment);
        return this.modifyMail(newMail);
    }


    static class MailServiceImpl extends sdcc_mail_serviceGrpc.sdcc_mail_serviceImplBase {

        String mail = null;
        private final String MAIL_USERNAME = "luca.menzolini@gmail.com";
        private final String MAIL_PASSWORD = "dprlslsfyapgxwtz";

        @Override
        public void getUserMail(IDMessage request, StreamObserver<UserMessage> responseObserver) {
            int id = request.getId();

            final IDMessage idMessage = IDMessage.newBuilder().setId(id).build();

            UserMessage response;

            try {
                response = blockingStub.findByID(idMessage);
            } catch (StatusRuntimeException e) {
                e.printStackTrace();
                return;
            }

            System.out.println(response.getMail());
            mail = response.getMail();
        }

        @Override
        public void sendMail(TagMessage request, StreamObserver<BooleanMessage> responseObserver) {
            //Ask user service for user's mail
            String address;

            MailController mailController = new MailController();
            if ((address = mail) == null){
                BooleanMessage response = BooleanMessage.newBuilder().setOk(FALSE).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }

            //retrieve mail from DB
            Mail mail;
            if ((mail = mailController.findByTag(request.getTag())) == null) {
                BooleanMessage response = BooleanMessage.newBuilder().setOk(FALSE).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }

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
                BooleanMessage response = BooleanMessage.newBuilder().setOk(FALSE).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
            BooleanMessage response = BooleanMessage.newBuilder().setOk(TRUE).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}