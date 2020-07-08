package controller;

import dataSource.DataSource;
import model.Mail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MailController {

    public MailController(){
        super();
    }

    //Retrieve mail by tag
    public Mail findByTag(String tag){
        DataSource dataSource = new DataSource();
        try {
            PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT * FROM mail WHERE mail.tag = ?;");
            statement.setString(1, tag);
            ResultSet resultSet = statement.executeQuery();
            dataSource.closeConnection();
            if (resultSet.next()){
                return new Mail(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
            }
            else return null;
        } catch (SQLException e) {
            System.out.println("Database Exception");
            return null;
        }
    }

    //Delete mail by ID
    public boolean deleteByID(int ID){
        try {
            DataSource db = new DataSource();
            PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM mail WHERE mail.id = ?");
            statement.setInt(1, ID);
            statement.executeUpdate();
            db.closeConnection();
            return true;
        } catch (SQLException e) {
            System.out.println("Database Exception");
            return false;
        }
    }

    //Save a new mail into DB
    public boolean save(Mail mail) {
        DataSource dataSource = new DataSource();
        try {
            PreparedStatement statement = dataSource.getConnection().prepareStatement("INSERT INTO mail (tag, object, message, attachment)\n" +
                    "VALUES (?, ?, ?, ?);");
            statement.setString(1, mail.getTag());
            statement.setString(2, mail.getObject());
            statement.setString(3, mail.getMessage());
            statement.setString(4, mail.getAttachment());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Database Exception");
            return false;
        }
    }
}
