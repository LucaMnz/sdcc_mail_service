package dataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static String JDBC_CONNECTION_URL =
            "jdbc:postgresql://localhost:5432/sdcc_mail";

    public static String ROOT_USERNAME = "postgres",
                         ROOT_PASSWORD = "postgres";

    private static Connection connection = null;

    public DataSource(){}

    public Connection getConnection() {
        if (connection != null) return connection;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(JDBC_CONNECTION_URL, ROOT_USERNAME, ROOT_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection(){
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
