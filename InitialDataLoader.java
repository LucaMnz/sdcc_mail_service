import controller.MailController;
import dataSource.DataSource;
import model.Mail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static dataSource.DataSource.ROOT_PASSWORD;
import static dataSource.DataSource.ROOT_USERNAME;

public class InitialDataLoader {

    private MailController mailController = new MailController();

    //Creating DB for SDCC user
    public void createDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", ROOT_USERNAME, ROOT_PASSWORD);
        Statement statement = con.createStatement();
        statement.execute("CREATE DATABASE SDCC_mail\n" +
                "    WITH \n" +
                "    OWNER = " + ROOT_USERNAME + "\n" +
                "    ENCODING = 'UTF8'\n" +
                "    CONNECTION LIMIT = -1;");
        statement.close();
        this.createUsers();
    }

    public void createUsers() throws SQLException {
        DataSource dataSource = new DataSource();
        Statement statement = dataSource.getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE mail(\n" +
                "   id SERIAL NOT NULL,\n" +
                "   tag NOT NULL VARCHAR(255),\n" +
                "   object VARCHAR(255),\n" +
                "   message VARCHAR(255),\n" +
                "   attachment VARCHAR(255),\n" +
                "   PRIMARY KEY (id));");
        statement.close();
        this.mailController.save(new Mail("REGISTRATION_SUCCESS", "Registrazione", "Registrazione avvenuta con successo.", null));
        this.mailController.save(new Mail("ORDER_SUCCESS", "Ordine inviato", "Ordine inviato con successo.", null));
    }
}
