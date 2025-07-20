package Model;
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

    public void showPortfolio(String mail) throws SQLException {
        Connection con = DBConnection.getConnection();

        // Get current balance
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

        // Fetch all transactions for user
        String portfolioQuery = " SELECT Symbol, Action, Quantity, Price FROM transactions WHERE Mail_id = ? ORDER BY Symbol, Mail_id";

        PreparedStatement pstPortfolio = con.prepareStatement(portfolioQuery);
        pstPortfolio.setString(1, mail);
        ResultSet rs = pstPortfolio.executeQuery();

        // Map to store quantities and buy/sell amounts
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

        User.SymQty.clear();
        User.SymPrice.clear();

        for (String sym : qtyMap.keySet()) {
            int totalQty = qtyMap.get(sym);
            if (totalQty <= 0) continue;  // Sold out, skip

            double totalBuy = buyMap.getOrDefault(sym, 0.0);
            double totalSell = sellMap.getOrDefault(sym, 0.0);
            double invested = totalBuy - totalSell;

            double avgPrice = invested / totalQty;
            avgPrice = Math.floor(avgPrice * 100) / 100.0;

            // Store for later use (e.g. in sell)
            User.SymQty.put(sym, totalQty);
            User.SymPrice.put(sym, avgPrice);

            System.out.printf("%-15s %-15d ₹%-20.2f ₹%-20.2f%n", sym, totalQty, avgPrice, invested);
        }
    }

    public double addFunds() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Enter amount to add (or type 'exit' to stop): ");
                String input = sc.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                double funds = Double.parseDouble(input);
                if (funds < 0) {
                    System.out.println("Error: Please enter a positive amount.");
                    continue; // loop again
                }
                this.balance =this.balance+ funds;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
        return balance;
    }
}