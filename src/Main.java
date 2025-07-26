import Model.User;
import Model.User.*;
import Service.*;
import DB.*;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("----- Welcome To GrowMore------");
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        User user = new User();
        DBConnection db = new DBConnection();
        DBQueries dbq=new DBQueries();
        StockService stockService = new StockService();
        UserValidation uv = new UserValidation();
        Connection con = db.getConnection();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        int qty=0;
        ResultSet rs;
        double cur_price=0;
        double bal=0;
        while (true) {
            System.out.println("1. Register\n2. Login\n3.exit");
            int choice = 0;
            while (true) {
                try {
                    System.out.print("Enter your option: ");
                    choice = sc.nextInt();
                    sc.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    sc.nextLine();
                }
            }
            switch (choice) {
                case 1:
                    uv.UserInput();
                    if (uv.age < 18) {
                        System.out.println("You are not eligible for Trading!!");
                        return;
                    }
                    if (userService.register(uv.name, uv.password, uv.aadhar, uv.pan, uv.email)) {
                        System.out.println("Registered successfully.");
                    } else {
                        System.out.println("Username already exists.");
                        System.out.println("Now, Please Log In");
                    }
                    break;
                case 2:
                    boolean isValidmail;
                    do {
                        System.out.print("Enter your E-mail id: ");
                        user.email = sc.nextLine();
                        isValidmail = true;
                        if (user.email.length() > 10 && user.email.endsWith("@gmail.com")) {
                            for (int j = 0; j < user.email.length() - 10; j++) {
                                char e = user.email.charAt(j);
                                if (!((e >= 'a' && e <= 'z') || (e >= 'A' && e <= 'Z') || (e >= '0' && e <= '9') || e == '.' || e == '_')) {
                                    isValidmail = false;
                                    break;
                                }
                            }
                        } else {
                            isValidmail = false;
                        }

                        if (!isValidmail) {
                            System.out.println("Invalid email! Enter a correct email.");
                        }
                    } while (!isValidmail);
                    String q1 = "select Mail_id from users where Mail_id=?";
                    PreparedStatement pst = con.prepareStatement(q1);
                    pst.setString(1, user.email);
                    rs = pst.executeQuery();
                    if (!(rs.next())) {
                        System.out.println("Email not found");
                        System.out.println("Please Sign Up And Then Log In");
                        break;
                    } else {
                        System.out.println("email found please enter your pass");
                        String pass = sc.next();
                        String q2 = "select password from users where Mail_id=?";
                        PreparedStatement pst2 = con.prepareStatement(q2);
                        pst2.setString(1, user.email);
                        rs = pst2.executeQuery();
                        if ((rs.next())) {
                            String actualPass = rs.getString("password");
                            if (actualPass.equals(pass)) {
                                System.out.println("Login successfully");
                                while (true) {
                                    System.out.println("\n--- MENU ---");
                                    System.out.println("1. View Stocks");
                                    System.out.println("2. Buy Stock");
                                    System.out.println("3. Sell Stock");
                                    System.out.println("4. Portfolio");
                                    System.out.println("5. Transaction History");
                                    System.out.println("6. Add Funds");
                                    System.out.println("7. Exit");
                                    int opt = 0;
                                    while (true) {
                                        try {
                                            System.out.print("Enter your option: ");
                                            opt = sc.nextInt();
                                            sc.nextLine();
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out.println("Invalid input. Please enter a valid integer.");
                                            sc.nextLine();
                                        }
                                    }

                                    switch (opt) {
                                        case 1:
                                            stockService.getAllStocks();
                                            break;
                                        case 2:
                                            System.out.print("Enter symbol: ");
                                            String sym = sc.nextLine();
                                            String s = dbq.getSharesDetail(sym);
                                            if (s == null) {
                                                break;
                                            }
                                            System.out.println(s);
                                            String sql = "SELECT Balance FROM users WHERE Mail_id = ?";
                                            PreparedStatement pstt = con.prepareStatement(sql);
                                            pstt.setString(1, user.email);  // Or any email you want to query
                                            rs = pstt.executeQuery();
                                            if (rs.next()) {
                                                bal = rs.getDouble("Balance");
                                            }
                                            System.out.println("balance = " + bal);
                                            cur_price = dbq.getCurPrice();
                                            int maxShares = (int) (bal / cur_price);
                                            if (maxShares == 0) {
                                                System.out.println("Add sufficient Balance");
                                                break;
                                            }
                                            System.out.println("you can buy maximum " + maxShares + " shares");
                                            while (true) {
                                                try {
                                                    System.out.print("Enter Quantity: ");
                                                    qty = sc.nextInt();
                                                    if (qty <= 0) {
                                                        System.out.println("Please enter a positive quantity.");
                                                        continue;
                                                    }
                                                    if (maxShares < qty) {
                                                        System.out.println("you can buy maximum " + maxShares + " shares");
                                                        continue;
                                                    }
                                                    break;
                                                } catch (InputMismatchException e) {
                                                    System.out.println("Invalid input. Please enter a valid quantity (numbers only).");
                                                    sc.next();
                                                }
                                            }
                                            bal = Math.floor((bal - cur_price * qty) * 100) / 100;
                                            System.out.println("Your Balance is: " + bal);
                                            String sql1 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                            PreparedStatement pst5 = con.prepareStatement(sql1);
                                            pst5.setDouble(1, bal);  // Set the balance
                                            pst5.setString(2, user.email);
                                            pst5.executeUpdate();
                                            dbq.dbtransaction(user.email, sym, qty, cur_price,"BUY");
                                            break;
                                        case 3:
                                            System.out.println("your portFolio is");
                                            user.showPortfolio(user.email);
                                            if(User.SymQty.isEmpty()){
                                                System.out.println("No Data Available");
                                                break;
                                            }
                                            System.out.println();
                                            System.out.println("Enter Symbol for sell shares");
                                            String SymSell=sc.nextLine();

                                            if(!(User.SymQty.containsKey(SymSell))){
                                                System.out.println("No shares Available of this Company");
                                                break;
                                            }
                                            dbq.getSharesDetail(SymSell);
                                            cur_price= dbq.getCurPrice();
                                            double avgPrice=User.SymPrice.get(SymSell);
                                            System.out.println("---------------------------------------------");
                                            System.out.println("Company : "+SymSell);
                                            System.out.println("Avg. price : "+avgPrice);
                                            System.out.println("Current price : "+cur_price);
                                            System.out.println("---------------------------------------------");

                                            System.out.println("if you want to sell, then Enter Y/y otherwise N/n");
                                            String ch=sc.next();
                                            if(ch.equalsIgnoreCase("N")){
                                                break;
                                            }

                                            int QtySell=User.SymQty.get(SymSell);
                                            int sellqty = 0;
                                            while (true) {
                                                try {
                                                    System.out.print("Enter quantity: ");
                                                    System.out.println("You have "+QtySell+" shares of "+SymSell);
                                                    sellqty = sc.nextInt();
                                                    if (sellqty <= 0) {
                                                        System.out.println("Quantity must be greater than 0. Try again.");
                                                    } else {
                                                        break;
                                                    }
                                                } catch (InputMismatchException e) {
                                                    System.out.println("Invalid input. Please enter a valid integer.");
                                                    sc.nextLine();
                                                }
                                            }
                                            if(sellqty>QtySell){
                                                System.out.println("you can sell maximum " + QtySell + " shares");
                                                break;
                                            }
                                            double sellAmount=cur_price*sellqty;
                                            bal=bal+sellAmount;
                                            String sql01 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                            PreparedStatement pst05 = con.prepareStatement(sql01);
                                            pst05.setDouble(1, bal);  // Set the balance
                                            pst05.setString(2, user.email);
                                            pst05.executeUpdate();
                                            dbq.dbtransaction(user.email, SymSell, sellqty, cur_price,"SELL");
                                            System.out.println("Transaction completed");
                                            if(cur_price>avgPrice){
                                                double profit=(cur_price-avgPrice)*sellqty;
                                                System.out.println("You make Profit of "+profit+" Rs.");
                                            }
                                            else{
                                                double loss=(avgPrice-cur_price)*sellqty;
                                                System.out.println("You make Loss of "+loss+" Rs.");
                                            }
                                            break;
                                        case 4:

                                            user.showPortfolio(user.email);
                                            if(User.SymQty.isEmpty()){
                                                System.out.println("No data available");
                                            }
                                            break;
                                        case 5:
                                            dbq.transactionHistory(user.email);
                                            break;
                                        case 6:
                                            double fund = user.addFunds();
                                            System.out.println("funds added");
                                            sql = "SELECT Balance FROM users WHERE Mail_id = ?";
                                            pstt = con.prepareStatement(sql);
                                            pstt.setString(1, user.email);  // Or any email you want to query
                                            rs = pstt.executeQuery();
                                            if (rs.next()) {
                                                bal = rs.getDouble("Balance");
                                            }
                                            bal = bal + fund;
                                            bal = (bal * 100) / 100;
                                            sql1 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                            pst5 = con.prepareStatement(sql1);
                                            pst5.setDouble(1, bal);  // Set the balance
                                            pst5.setString(2, user.email);
                                            pst5.executeUpdate();
                                            System.out.println("Your balance is = " + bal);
                                            break;
                                        case 7:
                                            System.out.println("Bye!");
                                            return;
                                    }
                                }
                            } else {
                                System.out.println("pass wrong");
                            }
                        }
                    }
                case 3:
                    System.out.println("Thank you!!");
                    return;
            }
        }
    }
}