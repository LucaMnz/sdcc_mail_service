import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import service.MailService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import sdccFoodDelivery.*;
import service.MailServiceInterface;

import static java.lang.Boolean.TRUE;

public class MailServiceServer {

    private static final Logger logger = Logger.getLogger(MailServiceServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new MailServiceServer.MailServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    MailServiceServer.this.stop();
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

    static class MailServiceImpl extends sdcc_mail_serviceGrpc.sdcc_mail_serviceImplBase{

        private static MailServiceInterface mailService = new MailService();

        @Override
        public void sendMail(SendMailRequest request, StreamObserver<BooleanMessage> responseObserver) {
            boolean isSuccessful = mailService.SendMail(request.getTag(), Integer.parseInt(request.getUserID()));
            BooleanMessage response = BooleanMessage.newBuilder().setOk(isSuccessful).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
