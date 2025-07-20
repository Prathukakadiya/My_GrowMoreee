package Model;
import Service.StockService;
import Service.StockService.*;
import DB.*;
import java.sql.*;
import java.util.HashMap;
import java.util.*;

public class User {
    public int id;
    public double balance;
    public String email;
    public static HashMap<String,Integer> SymQty=new HashMap<>();
    public static HashMap<String,Double> SymPrice=new HashMap<>();
    StockService st=new StockService();
    public void showPortfolio(String mail) throws SQLException {
        Connection con = DBConnection.getConnection();
        String balanceQuery = "SELECT Balance FROM users WHERE Mail_id = ?";
        PreparedStatement pstBalance = con.prepareStatement(balanceQuery);
        pstBalance.setString(1, mail);
        ResultSet rsBal = pstBalance.executeQuery();

        double balance = 0;
        if (rsBal.next()) {
            balance = rsBal.getDouble("Balance");
        }
        System.out.println("Current Balance: ₹" + balance);
        System.out.println("-----------------------------------------------------------------");
        String portfolioQuery = " SELECT Symbol, Action, Quantity, Price FROM transactions WHERE Mail_id = ? ORDER BY Symbol, Mail_id";

        PreparedStatement pstPortfolio = con.prepareStatement(portfolioQuery);
        pstPortfolio.setString(1, mail);
        ResultSet rs = pstPortfolio.executeQuery();

        Map<String, Integer> qtyMap = new HashMap<>();
        Map<String, Double> buyMap = new HashMap<>();
        Map<String, Double> sellMap = new HashMap<>();

        while (rs.next()) {
            String symbol = rs.getString("Symbol");
            String action = rs.getString("Action");
            int qty = rs.getInt("Quantity");
            double price = rs.getDouble("Price");

            if (!qtyMap.containsKey(symbol)) {
                qtyMap.put(symbol, 0);
            }
            if (!buyMap.containsKey(symbol)) {
                buyMap.put(symbol, 0.0);
            }
            if (!sellMap.containsKey(symbol)) {
                sellMap.put(symbol, 0.0);
            }

            if (action.equalsIgnoreCase("BUY")) {
                qtyMap.put(symbol, qtyMap.get(symbol) + qty);
                buyMap.put(symbol, buyMap.get(symbol) + (qty * price));
            } else if (action.equalsIgnoreCase("SELL")) {
                qtyMap.put(symbol, qtyMap.get(symbol) - qty);
                sellMap.put(symbol, sellMap.get(symbol) + (qty * price));
            }
        }

        System.out.printf("%-15s %-15s %-20s %-20s%n", "Company", "Total Qty", "Avg. Price", "Invested Amount");
        System.out.println("-----------------------------------------------------------------");

        SymQty.clear();
        SymPrice.clear();
        for (String sym : qtyMap.keySet()) {
            int totalQty = qtyMap.get(sym);
            if (totalQty <= 0) continue;  // Sold out, skip

            double totalBuy = buyMap.getOrDefault(sym, 0.0);
            double totalSell = sellMap.getOrDefault(sym, 0.0);
            double invested = totalBuy - totalSell;
            invested = Math.round(invested * 100) / 100.0;

            double avgPrice = invested / totalQty;
            avgPrice = Math.round(avgPrice * 100) / 100.0;

            // Store for later use (e.g. in sell)
            SymQty.put(sym, totalQty);
            SymPrice.put(sym, avgPrice);

            System.out.println(
                    st.TableShow(sym, 15) +
                            st.TableShow(String.valueOf(totalQty), 15) +
                            st.TableShow("Rs." + avgPrice, 20) +
                            st.TableShow("Rs." +  invested, 20)
            );
        }
    }

    public double addFunds() {
        Scanner sc = new Scanner(System.in);
        double funds = 0;

        while (true) {
            try {
                System.out.print("Enter amount to add: ");
                funds = sc.nextDouble();
                if (funds < 0) {
                    System.out.println("Error: Please enter a positive amount.");
                    continue;
                }
                break;

            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number.");
                sc.nextLine(); // Clear invalid input
            }
        }

        return funds;
    }
}