package Model;

import DS.CustomLinkedList;
import java.util.HashMap;

public class User {
    public int id;
    public String username;
    public String password;
    public double balance;
    public HashMap<String, Integer> portfolio = new HashMap<>();
    public CustomLinkedList transactionHistory = new CustomLinkedList();

    public User(int id, String username, String password, double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public void addStock(String symbol, int qty) {
        portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
    }

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
}