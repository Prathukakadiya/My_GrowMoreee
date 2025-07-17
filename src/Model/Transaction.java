package Model;

public class Transaction {
    public String symbol;
    public int quantity;
    public double price;
    public String timestamp;

    public Transaction(String symbol, int quantity, double price, String timestamp) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return symbol + " | Qty: " + quantity + " | ₹" + price + " | " + timestamp;
    }
}