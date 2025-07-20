package DB;
import Service.StockService.*;
import Service.*;
import java.sql.*;
public class DBQueries {
    StockService stockService=new StockService();
    double cur_price=0;
    public double getCurPrice() {
        return this.cur_price;
    }
    public String getSharesDetail(String sym) throws Exception{
        double today_open=0;
        String detail=null;
        StockService stockService=new StockService();
        Connection con=DBConnection.getConnection();
        String q3 = "select Symbols from stocks where Symbols=?";
        PreparedStatement pst3 = con.prepareStatement(q3);
        pst3.setString(1, sym);
        ResultSet rs = pst3.executeQuery();
        if (rs.next()) {
            String q4 = "{call details(?)}";
            CallableStatement cst = con.prepareCall(q4);
            cst.setString(1, sym);
            ResultSet rs2 = cst.executeQuery();
            if(rs2.next()) {
                String symbol = stockService.TableShow(rs2.getString(1), 12);
                String name = stockService.TableShow(rs2.getString(2), 15);
                String prev = "Previous Close: Rs." + rs2.getString(3);
                String open = "Today Open: Rs." + rs2.getString(4);
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
    public void dbtransaction(String mail, String symbol, int qty, double price, String buysell) throws SQLException {
        Connection con = DBConnection.getConnection();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        String sql = "INSERT INTO transactions (Mail_id, Symbol, Quantity, Price, Action, Timestamp) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, mail);
        pst.setString(2, symbol);
        pst.setInt(3, qty);
        pst.setDouble(4, price);
        pst.setString(5,buysell);
        pst.setTimestamp(6, time);
        pst.executeUpdate();
    }

    public void transactionHistory(String mail) throws SQLException {
        Connection con = DBConnection.getConnection();
        String sql="select * from transactions where Mail_id=?";
        PreparedStatement pst=con.prepareStatement(sql);
        pst.setString(1,mail);
        ResultSet rs= pst.executeQuery();
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Company   Quantity       Price     Action          Date & Time");
        System.out.println("--------------------------------------------------------------------");
        while (rs.next()){
            String s1=stockService.TableShow(rs.getString(2),12);
            String s2=stockService.TableShow(rs.getString(3),12);
            String s3=stockService.TableShow(rs.getString(4),12);
            String s4=stockService.TableShow(rs.getString(5),12);
            String s5=stockService.TableShow(rs.getString(6),12);
            System.out.println(s1+s2+s3+s4+s5);
        }
    }
}
