package Service;

import DB.DBConnection;
import Model.*;
import java.sql.*;

public class TradingService {
    private final StockService stockService = new StockService();

    public boolean buyStock(User user, String symbol, int qty) throws SQLException {
        Stock stock = stockService.getStock(symbol);
        if (stock == null) return false;

        double total = stock.todayOpenPrice * qty;
        if (user.balance < total) return false;

        Connection con = DBConnection.getConnection();
        con.setAutoCommit(false);

        try {
            // Update balance in DB
            PreparedStatement ps = con.prepareStatement("UPDATE users SET balance=? WHERE id=?");
            ps.setDouble(1, user.balance - total);
            ps.setInt(2, user.id);
            ps.executeUpdate();

            // Insert into transaction table
            PreparedStatement insertTxn = con.prepareStatement(
                    "INSERT INTO transactions(user_id, symbol, quantity, price) VALUES (?, ?, ?, ?)"
            );
            insertTxn.setInt(1, user.id);
            insertTxn.setString(2, symbol);
            insertTxn.setInt(3, qty);
            insertTxn.setDouble(4, stock.todayOpenPrice);
            insertTxn.executeUpdate();

            con.commit();

            // Update memory
            user.balance -= total;
            user.addStock(symbol, qty);
            user.transactionHistory.add(new Transaction(symbol, qty, stock.todayOpenPrice, new java.sql.Timestamp(System.currentTimeMillis()).toString()));

            return true;
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }
}