package Model;
import DB.*;
import DS.CustomLinkedList;

import java.sql.*;
import java.util.HashMap;
import java.util.*;

public class User {
    public int id;
    public String username;
    public String password;
    public double balance;
    public String email;
    public HashMap<String, Integer> portfolio = new HashMap<>();
    public CustomLinkedList transactionHistory = new CustomLinkedList();

   public void showPortfolio(String mail) throws SQLException {
       Connection con=DBConnection.getConnection();

       String balanceQuery = "SELECT Balance FROM users WHERE Mail_id = ?";
       PreparedStatement pstBalance = con.prepareStatement(balanceQuery);
       pstBalance.setString(1, mail);
       ResultSet rsBal = pstBalance.executeQuery();

       double balance = 0.0;
       if (rsBal.next()) {
           balance = rsBal.getDouble("Balance");
       }

       System.out.println("User Mail: " + mail);
       System.out.println("Current Balance: ₹" + balance);
       System.out.println("--------------------------------------------------");

       // Step 2: Get portfolio (grouped by company)
       String portfolioQuery = "SELECT Symbol, SUM(Quantity) AS TotalQty, SUM(Quantity * Price) AS InvestedAmount FROM transactions WHERE Mail_id = ? AND Action = 'BUY' GROUP BY Symbol";
       PreparedStatement pstPortfolio = con.prepareStatement(portfolioQuery);
       pstPortfolio.setString(1, mail);
       ResultSet rs = pstPortfolio.executeQuery();

       System.out.printf("%-15s %-15s %-20s%n", "Company", "Total Qty", "Invested Amount");
       System.out.println("--------------------------------------------------");

       while (rs.next()) {
           String symbol = rs.getString("Symbol");
           int totalQty = rs.getInt("TotalQty");
           double invested = rs.getDouble("InvestedAmount");

           System.out.printf("%-15s %-15d ₹%-20.2f%n", symbol, totalQty, invested);
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