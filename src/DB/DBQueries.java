package DB;
import DB.DBConnection;
import Service.*;
import java.sql.*;
public class DBQueries {
    double cur_price=0;
    public double getCurPrice() {
        return this.cur_price;
    }
    public String getSharesDetail(String sym) throws Exception{
        double prev_close=0;
        double today_open=0;
        String detail=null;
        StockService stockService=new StockService();
        Connection con=DBConnection.getConnection();
        String q3 = "select Symbols from stocks where Symbols=?";
        PreparedStatement pst3 = con.prepareStatement(q3);
        pst3.setString(1, sym);
        ResultSet rs = pst3.executeQuery();
        if (rs.next()) {
            String actualsym = rs.getString("Symbols");
            String q4 = "{call details(?)}";
            CallableStatement cst = con.prepareCall(q4);
            cst.setString(1, sym);
            ResultSet rs2 = cst.executeQuery();
            if(rs2.next()) {
                String symbol = stockService.TableShow(rs2.getString(1), 12);
                String name = stockService.TableShow(rs2.getString(2), 15);
                String prev = "Previous Close: Rs." + rs2.getString(3);
                String open = "Today Open: Rs." + rs2.getString(4);
                prev_close=rs2.getDouble(3);
                today_open=rs2.getDouble(4);
                cur_price = today_open * (1 + (Math.random() * 0.06 - 0.03));
                cur_price = Math.round(cur_price * 100.0) / 100.0;
                detail=(symbol + name +"   "+ prev + "     " + open + "    Current Price: Rs."+cur_price);
            }
            return detail;
        }
        else{
            System.out.println("Company not found!!");
            return null;
        }
    }
    public void dbtransaction(String mail, String symbol, int qty, double price) throws SQLException {
        Connection con = DBConnection.getConnection();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String sql = "INSERT INTO transactions (Mail_id, Symbol, Quantity, Price, Timestamp) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, mail);
        pst.setString(2, symbol);
        pst.setInt(3, qty);
        pst.setDouble(4, price);
        pst.setTimestamp(5, time);
        pst.executeUpdate();
    }
}
