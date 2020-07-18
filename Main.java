import model.Mail;
import service.MailService;

public class Main {

    public static void main(String[] args) {

        MailService mailService = new MailService();
        mailService.SendMail("prova", -1);
        //InitialDataLoader initialDataLoader = new InitialDataLoader();
        //initialDataLoader.createDB();

    }
}
