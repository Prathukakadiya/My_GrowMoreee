package DB;
import java.sql.*;
public class DBConnection {
    // Connection to Database
    private static final String URL = "jdbc:mysql://localhost:3306/trading_app";
    private static final String USER = "root";
    private static final String PASS = "";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
