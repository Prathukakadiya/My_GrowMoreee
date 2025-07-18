import Model.Stock;
import Model.User;
import Service.*;
import DB.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        User user1 = new User();
        TradingService tradingService = new TradingService();
        DBConnection db = new DBConnection();
        StockService stockService = new StockService();
        UserValidation uv = new UserValidation();
        Connection con = db.getConnection();
        int qty=0;
        ResultSet rs;
        double prev_close=0;
        double today_open=0;
        double cur_price=0;
        double bal=0;
        //double bal =0;
        while (true) {
            System.out.println("1. Register\n2. Login\n3.exit");
            int choice = sc.nextInt();
            sc.nextLine();
            User user = null;

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
                    //String email;
                    boolean isValidmail;
                    do {
                        System.out.print("Enter your E-mail id: ");
                        user1.email = sc.nextLine();
                        isValidmail = true;
                        if (user1.email.length() > 10 && user1.email.endsWith("@gmail.com")) {
                            for (int j = 0; j < user1.email.length() - 10; j++) {
                                char e = user1.email.charAt(j);
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
                    pst.setString(1, user1.email);
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
                        pst2.setString(1, user1.email);
                        rs= pst2.executeQuery();
                        if ((rs.next())) {
                            String actualPass = rs.getString("password");
                            if (actualPass.equals(pass)) {
                                System.out.println("Login successfully");
                                while (true) {
                                    System.out.println("\n--- MENU ---");
                                    System.out.println("1. View Stocks");
                                    System.out.println("2. Buy Stock");
                                    System.out.println("3. Portfolio");
                                    System.out.println("4. Transaction History");
                                    System.out.println("5. View All Logged-in Users");
                                    System.out.println("6. Add Funds");
                                    System.out.println("7. Exit");

                                    int opt = sc.nextInt();
                                    sc.nextLine();

                                    switch (opt) {
                                        case 1:
                                            stockService.getAllStocks();
                                            break;
                                        case 2:
                                            System.out.print("Enter symbol: ");
                                            String sym = sc.nextLine();
                                            String q3 = "select Symbols from stocks where Symbols=?";
                                            PreparedStatement pst3 = con.prepareStatement(q3);
                                            pst3.setString(1, sym);
                                            rs = pst3.executeQuery();
                                            if (rs.next()) {
                                                String actualsym = rs.getString("Symbols");
                                                    String q4 = "{call details(?)}";
                                                    CallableStatement cst = con.prepareCall(q4);
                                                    cst.setString(1, sym);
                                                    ResultSet rs2 = cst.executeQuery();
                                                    if(rs2.next()) {
                                                        String symbol = stockService.TableShow(rs2.getString(1), 12);
                                                        String name = stockService.TableShow(rs2.getString(2), 15);
                                                        String prev = "Previous Close: Rs." + rs2.getString(3);
                                                        String open = "Today Open: Rs." + rs2.getString(4);
                                                        prev_close=rs2.getDouble(3);
                                                        today_open=rs2.getDouble(4);
                                                        cur_price = today_open * (1 + (Math.random() * 0.06 - 0.03));
                                                        cur_price = Math.round(cur_price * 100.0) / 100.0;
                                                        System.out.println(symbol + name + prev + "     " + open + "    Current Price: Rs."+cur_price);
                                                    }
                                                String sql = "SELECT Balance FROM users WHERE Mail_id = ?";
                                                PreparedStatement pstt = con.prepareStatement(sql);
                                                pstt.setString(1, user1.email);  // Or any email you want to query
                                                rs = pstt.executeQuery();
                                                if (rs.next()) {
                                                    bal = rs.getDouble("Balance");
                                                }
                                                System.out.println("balance = "+bal);
                                                int maxShares=(int)(bal/cur_price);
                                                    if(maxShares==0){
                                                        System.out.println("Add sufficient Balance");
                                                        break;
                                                    }
                                                System.out.println("you can buy maximum "+maxShares+" shares");
                                                while (true) {
                                                    try {
                                                        System.out.print("Enter Quantity: ");
                                                        qty = sc.nextInt();
                                                        if (qty <= 0) {
                                                            System.out.println("Please enter a positive quantity.");
                                                            continue;
                                                        }
                                                        if(maxShares<qty){
                                                            System.out.println("you can buy maximum "+maxShares+" shares");
                                                            continue;
                                                        }
                                                        break;
                                                    } catch (InputMismatchException e) {
                                                        System.out.println("Invalid input. Please enter a valid quantity (numbers only).");
                                                        sc.next();
                                                    }
                                                }
                                                bal = Math.floor((bal - cur_price * qty) * 100) / 100;
                                                System.out.println("Your Balance is: "+bal);
                                                String sql1 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                                PreparedStatement pst5 = con.prepareStatement(sql1);
                                                pst5.setDouble(1, bal);  // Set the balance
                                                pst5.setString(2, user1.email);
                                                int rows = pst5.executeUpdate();

                                            }
                                            else{
                                                System.out.println("company not found");
                                            }
                                                break;
                                                case 3:
                                                    user.showPortfolio();
                                                    break;
                                                case 4:
                                                    user.transactionHistory.display();
                                                    break;
                                                case 5:
                                                    userService.showAllUsers();
                                                    break;
                                                case 6:
                                                    user1.addFunds();
                                                    System.out.println("funds addded");
                                                    bal=bal+=user1.balance;
                                                    String sql1 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                                    PreparedStatement pst5 = con.prepareStatement(sql1);
                                                    pst5.setDouble(1, bal);  // Set the balance
                                                    pst5.setString(2, user1.email);
                                                    int rows = pst5.executeUpdate();
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

//
            }
        }
    }
}