package Service;

import DB.DBConnection;
import Model.User;

import java.sql.*;
import java.util.HashMap;

public class UserService {
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

    public User login(String username, String password) throws SQLException {
        if (userMap.containsKey(username)) return userMap.get(username);

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"
        );
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User(
                    rs.getInt("id"),
                    username,
                    password,
                    rs.getDouble("balance")
            );
            userMap.put(username, user);
            return user;
        }

        con.close();
        return null;
    }

    public void showAllUsers() {
        System.out.println("All Logged-in Users:");
        for (String username : userMap.keySet()) {
            System.out.println("- " + username);
        }
    }
}