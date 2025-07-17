package Model;

public class  Stock {
    public String symbol;
    public String name;
    public double previousClosePrice;
    public double todayOpenPrice;

    public Stock(String symbol, String name, double previousClosePrice, double todayOpenPrice) {
        this.symbol = symbol;
        this.name = name;
        this.previousClosePrice = previousClosePrice;
        this.todayOpenPrice = todayOpenPrice;
    }
}
