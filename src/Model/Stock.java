package Model;

public class Stock {
    private String symbol;
    private String companyName;
    private String category;
    private double previousClosePrice;
    private double todayOpenPrice;

    // constructor
    public Stock(String symbol, String companyName, String category, double previousClosePrice, double todayOpenPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.category = category;
        this.previousClosePrice = previousClosePrice;
        this.todayOpenPrice = todayOpenPrice;
    }

    // getters
    public String getSymbol() { return symbol; }
    public String getCompanyName() { return companyName; }
    public String getCategory() { return category; }
    public double getPreviousClosePrice() { return previousClosePrice; }
    public double getTodayOpenPrice() { return todayOpenPrice; }

    // toString override
    @Override
    public String toString() {
        return String.format("%-10s %-30s %-20s %-15.2f %-15.2f",
                symbol, companyName, category, previousClosePrice, todayOpenPrice);
    }
}

