package Model;

import DS.CustomLinkedList;
import java.util.HashMap;
import java.util.*;

public class User {
    public int id;
    public String username;
    public String password;
    public double balance;
    public HashMap<String, Integer> portfolio = new HashMap<>();
    public CustomLinkedList transactionHistory = new CustomLinkedList();

//    public User(int id, String username, String password, double balance) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
//        this.balance = balance;
//    }

   /* public void addStock(String symbol, int qty) {
        portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
    }
*/
    public void showPortfolio() {
        if (portfolio.isEmpty()) {
            System.out.println("Your portfolio is empty.");
        } else {
            System.out.println("Your Portfolio:");
            for (var entry : portfolio.entrySet()) {
                System.out.println(entry.getKey() + " → " + entry.getValue() + " shares");
            }
        }
    }
    public double addFunds() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Enter amount to add (or type 'exit' to stop): ");
                String input = sc.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting fund addition. Final Balance: ₹" + balance);
                    break;
                }
                double funds = Double.parseDouble(input);

                if (funds < 0) {
                    System.out.println("Error: Please enter a positive amount.");
                    continue; // loop again
                }
                this.balance += funds;
                System.out.println("Funds added successfully. New Balance: ₹" + balance);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
        return balance;
    }
}