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
        ResultSet rs;
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
                    String email;
                    boolean isValidmail;
                    do {
                        System.out.print("Enter your E-mail id: ");
                        email = sc.nextLine();
                        isValidmail = true;
                        if (email.length() > 10 && email.endsWith("@gmail.com")) {
                            for (int j = 0; j < email.length() - 10; j++) {
                                char e = email.charAt(j);
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
                    pst.setString(1, email);
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
                        pst2.setString(1, email);
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
                                                        String prev = "Previous Close: ₹" + rs2.getString(3);
                                                        String open = "Today Open: ₹" + rs2.getString(4);

                                                        System.out.println(symbol + name + prev + "     " + open);
                                                    }
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