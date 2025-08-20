package Service;

import DS.StocksList;
import DB.DBConnection;
import Model.Stock;
import java.sql.*;
import java.util.*;

public class StockService {
    Scanner sc = new Scanner(System.in);
    StocksList stocksList = new StocksList();

        // Fetch stocks details from Database
    public void getAllStocks() throws SQLException {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM stocks");

        while (rs.next()) {
            String sym = rs.getString("Symbols");
            String name = rs.getString("Company_names");
            String category = rs.getString("Category");
            double prev = rs.getDouble("Previous_ClosePrice");
            double today = rs.getDouble("Today_OpenPrice");

            stocksList.InsertStocks(sym, name, category, prev, today);
        }
        stocksList.DisplayStock();

        rs.close();
        st.close();
        con.close();
    }

        // regex
    public static String TableShow(String str, int length) {
        while (str.length() < length) {
            str += " ";
        }
        return str;
    }
    
}

