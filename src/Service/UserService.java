package Service;

import DB.DBConnection;
import Model.User;

import java.sql.*;
import java.util.HashMap;

public class UserService {

    // Adding user registration details into database
    private final HashMap<String, User> userMap = new HashMap<>();
    public boolean register(String username, String password, String aadhar, String pan, String mail) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username, password,Aadhar_No, Pan_No, Mail_id ) VALUES (?, ?, ?, ?, ?)"
        );
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, aadhar);
        ps.setString(4, pan);
        ps.setString(5, mail);
        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }
}
