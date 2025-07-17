package Service;

import DB.DBConnection;
import Model.Stock;

import java.sql.*;
import java.util.*;

public class StockService {
    public List<Stock> getAllStocks() throws SQLException {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM stocks");

        List<Stock> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Stock(
                    rs.getString("symbol"),
                    rs.getString("name"),
                    rs.getDouble("price")
            ));
        }
        con.close();
        return list;
    }

    public Stock getStock(String symbol) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM stocks WHERE symbol=?");
        ps.setString(1, symbol);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Stock(
                    rs.getString("symbol"),
                    rs.getString("name"),
                    rs.getDouble("price")
            );
        }
        con.close();
        return null;
    }
}