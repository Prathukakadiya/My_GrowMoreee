import Model.User;
import Service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        TradingService tradingService = new TradingService();
        StockService stockService = new StockService();
        UserValidation uv = new UserValidation();
        HashMap<String,String> MailPass = new HashMap<>();
        while (true) {
            System.out.println("1. Register\n2. Login\n3.exit");
            int choice = sc.nextInt();
            sc.nextLine();
            User user = null;

            switch (choice){
                case 1:
                uv.UserInput();
                if (uv.age < 18) {
                    System.out.println("You are not eligible for Trading!!");
                    return;
                }
                if (userService.register(uv.name, uv.password, uv.aadhar, uv.pan, uv.email)) {
                    MailPass.put(uv.email,uv.password);
                    //System.out.println(Mail);
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
                    if(!(MailPass.containsKey(email))){
                    System.out.println("Email not found");
                    System.out.println("Please Sign Up And Then Log In");
                    break;
                    }else{
                        System.out.println("email found please enter your pass");
                        String pass = sc.next();
                        if(MailPass.get(email).equals(pass)){
                            System.out.println("log in successfully");
                        }
                    }

            }

            /*while (user == null) {
                System.out.print("Username: ");
                String u = sc.nextLine();
                System.out.print("Password: ");
                String p = sc.nextLine();
                user = userService.login(u, p);
                if (user == null) System.out.println("Login failed. Try again.");
            }*/

//            while (true) {
//                System.out.println("\n--- MENU ---");
//                System.out.println("1. View Stocks");
//                System.out.println("2. Buy Stock");
//                System.out.println("3. Portfolio");
//                System.out.println("4. Transaction History");
//                System.out.println("5. View All Logged-in Users");
//                System.out.println("6. Exit");
//
//                int opt = sc.nextInt();
//                sc.nextLine();
//
//                switch (opt) {
//                    case 1:
//                        for (var s : stockService.getAllStocks()) {
//                            System.out.println(s.symbol + " - " + s.name + " - ₹" + s.price);
//                        }
//                        break;
//                    case 2:
//                        System.out.print("Enter symbol: ");
//                        String sym = sc.nextLine();
//                        System.out.print("Quantity: ");
//                        int qty = sc.nextInt();
//                        sc.nextLine();
//                        if (tradingService.buyStock(user, sym, qty)) {
//                            System.out.println("Success!");
//                        } else {
//                            System.out.println("Failed: insufficient funds or invalid stock.");
//                        }
//                        break;
//                    case 3:
//                        user.showPortfolio();
//                        break;
//                    case 4:
//                        user.transactionHistory.display();
//                        break;
//                    case 5:
//                        userService.showAllUsers();
//                        break;
//                    case 6:
//                        System.out.println("Bye!");
//                        return;
//                }
//            }
        }
    }
}
