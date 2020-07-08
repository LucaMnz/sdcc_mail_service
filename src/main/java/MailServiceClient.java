import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import io.grpc.StatusRuntimeException;
import sdccFoodDelivery.BooleanMessage;
import sdccFoodDelivery.TagMessage;
import sdccFoodDelivery.sdcc_mail_serviceGrpc;


public class MailServiceClient {

    final  sdcc_mail_serviceGrpc.sdcc_mail_serviceBlockingStub blockingStub;


    public MailServiceClient(Channel channel) {
        this.blockingStub = sdcc_mail_serviceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        final MailServiceClient client = new MailServiceClient(channel);
        client.run(channel);
    }

    private void run(ManagedChannel channel) {

        String tag = "";  //scrivi tag

        sendMail(tag);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void sendMail(String tag){

        final TagMessage tagMessage = TagMessage.newBuilder().setTag(tag).build();

        BooleanMessage response;
        try{
            response = blockingStub.sendMail(tagMessage);
        } catch (StatusRuntimeException e){
            e.printStackTrace();
            return;
        }
        if(response.getOk()){
            System.out.println("Correctly send");
        }else {
            System.out.println("Uncorrectly send");
        }

    }
}
