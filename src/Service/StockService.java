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
            Stock stock = new Stock(
                    rs.getString("Symbols"),
                    rs.getString("Company_names"),
                    rs.getDouble("Previous_ClosePrice"),
                    rs.getDouble("Today_OpenPrice")
            );
            list.add(stock);

            // ✅ Pad and print here
            String symbol = TableShow(stock.symbol, 12);
            String name = TableShow(stock.name, 35);
            String prev = "Previous Close: ₹" + stock.previousClosePrice;
            String open = "Today Open: ₹" + stock.todayOpenPrice;

            System.out.println(symbol + name + prev + "   |   " + open);
        }
        return list;
    }

    public String TableShow(String str, int length) {
        while (str.length() < length) {
            str += " ";
        }
        return str;
    }

    public Stock getStock(String symbol) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM stocks WHERE symbol=?");
        ps.setString(1, symbol);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Stock(
                    rs.getString("Symbols"),
                    rs.getString("Company_names"),
                    rs.getDouble("Previous_ClosePrice"),
                    rs.getDouble("Today_OpenPrice")
            );
        }
        con.close();
        return null;
    }
}