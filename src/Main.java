import DS.StocksList;
import Model.User;
import Service.*;
import DB.*;

import java.sql.*;
import java.util.*;

public class Main {
    // main method
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        User user = new User();
        DBConnection db = new DBConnection();
        DBQueries dbq=new DBQueries();
        StocksList sl=new StocksList();
        StockService stockService = new StockService();
        UserValidation uv = new UserValidation();
        Connection con = db.getConnection();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        int qty=0;
        ResultSet rs;
        double cur_price=0;
        double bal=0;
        try{
            System.out.println("***************************************");
            System.out.println("||     WELCOME TO GROWMOREEE         ||");
            System.out.println("||      PRESENTED BY LJ-A4           ||");
            System.out.println("***************************************");
            Thread.sleep(1000);
            System.out.println();
            System.out.println("--------Trade with patience, profit with discipline--------");
            System.out.println();
            Thread.sleep(1000);
        }catch(Exception e){

        }
        while(true) {
            System.out.print("Enter your option : ");
            System.out.println("1. Admin");
            System.out.println("2. user");
            System.out.println("3. exit");
            int cho = sc.nextInt();
            switch(cho) {
                case 1:
                    // Admin section
                    System.out.println("Welcome Admin");
                    System.out.print("Enter your opinion: ");
                    System.out.println("1. Give suggestion");
                    System.out.println("2. Get top 5 profiter");
                    System.out.println("3. Get top 5 looser ");
                    System.out.println("4. Exit");
                    int ich=sc.nextInt();
                    switch(ich){
                        case 1:
                            // Giving suggestions
                            System.out.println("Suggestion System");

                            System.out.print("Enter category name: ");
                            String cat = sc.next();

                            String q0 = "SELECT SuggestedSymbol FROM suggestions WHERE Category=?";
                            PreparedStatement ps0 = con.prepareStatement(q0);
                            ps0.setString(1, cat);
                            ResultSet rs0 = ps0.executeQuery();

                            if (rs0.next()) {
                                System.out.println("\n Current Suggestion for " + cat + " : " + rs0.getString("SuggestedSymbol"));
                            } else {
                                System.out.println("\n No suggestion found for " + cat + " (new entry will be created).");
                            }

                            String q1 = "SELECT Symbols, Company_names FROM stocks WHERE Category=?";
                            PreparedStatement ps1 = con.prepareStatement(q1);
                            ps1.setString(1, cat);
                            ResultSet rs1 = ps1.executeQuery();

                            boolean found = false;
                            System.out.println("\nAvailable stocks in category: " + cat);
                            System.out.println("----------------------------------");
                            while (rs1.next()) {
                                found = true;
                                System.out.println("Symbol : " + rs1.getString("Symbols") +
                                        " | Company : " + rs1.getString("Company_names"));
                            }

                            if (!found) {
                                System.out.println(" No stocks found for this category.");
                                break;
                            }

                            System.out.print("\nDo you want to change the suggestion? (yes/no): ");
                            String choice = sc.next();

                            if (choice.equalsIgnoreCase("yes")) {
                                System.out.print("Enter new stock Symbol to set as suggestion: ");
                                String selectedSym = sc.next();

                                String q2 = "INSERT INTO suggestions (Category, SuggestedSymbol) VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE SuggestedSymbol=?";
                                PreparedStatement ps2 = con.prepareStatement(q2);
                                ps2.setString(1, cat);
                                ps2.setString(2, selectedSym);
                                ps2.setString(3, selectedSym);
                                ps2.executeUpdate();

                                System.out.println("\n Suggestion updated successfully!");
                                System.out.println("Category : " + cat + " | Suggested Stock : " + selectedSym);
                            } else {
                                System.out.println("\n Suggestion unchanged.");
                            }

                            break;
                        case 2:
                            // Displaying top 5 profiter
                            String qpl="SELECT Mail_id, SUM(ProfitLoss) AS TotalProfit FROM adminprofitloss GROUP BY Mail_id ORDER BY TotalProfit DESC LIMIT 5";
                            PreparedStatement ppl=con.prepareStatement(qpl);
                            ResultSet rspl=ppl.executeQuery();
                            System.out.println("top 5 profiter is : ");
                            while(rspl.next()){
                                System.out.println(rspl.getString(1));
                            }
                            break;
                        case 3:
                            // Displaying top 5 looser
                            String qpl1="SELECT Mail_id, SUM(ProfitLoss) AS TotalProfit FROM adminprofitloss GROUP BY Mail_id ORDER BY TotalProfit LIMIT 5";
                            PreparedStatement ppl1=con.prepareStatement(qpl1);
                            ResultSet rspl1=ppl1.executeQuery();
                            System.out.println("top 5 loser is : ");
                            while(rspl1.next()){
                                System.out.println(rspl1.getString(1));
                            }
                            break;
                        case 4:
                            //exiting the app
                            break;

                    }

                    break;
                case 2:
                    // User section
                    while (true) {
                        System.out.println("-----Enter ");
                        System.out.println("1. For Register \n2. For Login\n3. To exit");
                        int choice = 0;
                        while (true) {
                            try {
                                System.out.print("Enter your option: ");
                                choice = sc.nextInt();
                                sc.nextLine();
                                break;
                            } catch (InputMismatchException e) {
                                System.out.println("NOTICE : Invalid input. Please enter a valid integer.");
                                sc.nextLine();
                            }
                        }
                        switch (choice) {
                            case 1:
                                // Register
                                uv.UserInput();
                                if (uv.age < 18) {
                                    System.out.println("Sorry! You are not eligible for Trading!!");
                                    System.out.println("Your age must greater then 18.");
                                    return;
                                }
                                if (userService.register(uv.name, uv.password, uv.aadhar, uv.pan, uv.email)) {
                                    System.out.println("***************************************");
                                    System.out.println("     Registered successfully.");
                                    System.out.println("     Please Log in to Trade.");
                                    System.out.println("***************************************");
                                } else {
                                    System.out.println("Username already exists.");
                                    System.out.println("Now, Please Log In");
                                }
                                break;
                            case 2:
                                // Log-in
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
                                        System.out.println("NOTICE : Invalid email! Enter a correct email.");
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
                                    System.out.println("Email found please enter your pass: ");
                                    boolean valpass=false;
                                    int count=0;
                                    while(!valpass && count<3){
                                        String pass = sc.next();
                                        String q2 = "select password from users where Mail_id=?";
                                        PreparedStatement pst2 = con.prepareStatement(q2);
                                        pst2.setString(1, user.email);
                                        rs = pst2.executeQuery();
                                        if ((rs.next())) {
                                            String actualPass = rs.getString("password");
                                            if (actualPass.equals(pass)) {
                                                System.out.println("Login successfully");
                                                valpass=true;
                                                while (true) {
                                                    System.out.println("\n--- MENU ---");
                                                    System.out.println("1. View Stocks");
                                                    System.out.println("2. Buy Stock");
                                                    System.out.println("3. Sell Stock");
                                                    System.out.println("4. Portfolio");
                                                    System.out.println("5. Transaction History");
                                                    System.out.println("6. Add Funds");
                                                    System.out.println("7. For suggestion to any category(By our Exppert)");
                                                    System.out.println("8. Exit");
                                                    int opt = 0;
                                                    while (true) {
                                                        try {
                                                            System.out.print("Enter your option: ");
                                                            opt = sc.nextInt();
                                                            sc.nextLine();
                                                            break;
                                                        } catch (InputMismatchException e) {
                                                            System.out.println("NOTICE : Invalid input. Please enter a valid integer.");
                                                            sc.nextLine();
                                                        }
                                                    }

                                                    switch (opt) {
                                                        case 1:
                                                            stockService.getAllStocks();
                                                            break;
                                                        case 2:
                                                            System.out.println("here is the suggesstions from out experts for our users");
                                                            String qs="select * from suggestions";
                                                            PreparedStatement sp=con.prepareStatement(qs);
                                                            ResultSet sr=sp.executeQuery();
                                                            while(sr.next()){
                                                                System.out.println("  category : "+sr.getString("category")+"\n suggestion : "+sr.getString("SuggestedSymbol"));
                                                            }
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
                                                                System.out.println("NOTICE : Add sufficient Balance");
                                                                break;
                                                            }
                                                            System.out.println("You can buy maximum " + maxShares + " shares");
                                                            while (true) {
                                                                try {
                                                                    System.out.print("Enter Quantity: ");
                                                                    qty = sc.nextInt();
                                                                    if (qty <= 0) {
                                                                        System.out.println("NOTICE : Please enter a positive quantity.");
                                                                        continue;
                                                                    }
                                                                    if (maxShares < qty) {
                                                                        System.out.println("You can buy maximum " + maxShares + " shares");
                                                                        continue;
                                                                    }
                                                                    break;
                                                                } catch (InputMismatchException e) {
                                                                    System.out.println("NOTICE : Invalid input. Please enter a valid quantity (numbers only).");
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
                                                            dbq.dbtransaction(user.email, sym, qty, cur_price, "BUY");
                                                            break;
                                                        case 3:
                                                            System.out.println("Your portFolio is");
                                                            user.showPortfolio(user.email);
                                                            if (User.SymQty.isEmpty()) {
                                                                System.out.println("No Data Available");
                                                                break;
                                                            }
                                                            System.out.println();
                                                            System.out.println("Enter Symbol for sell shares");
                                                            String SymSell = sc.nextLine();

                                                            if (!(User.SymQty.containsKey(SymSell))) {
                                                                System.out.println("No shares Available of this Company");
                                                                break;
                                                            }
                                                            dbq.getSharesDetail(SymSell);
                                                            cur_price = dbq.getCurPrice();
                                                            double avgPrice = User.SymPrice.get(SymSell);
                                                            System.out.println("---------------------------------------------");
                                                            System.out.println("Company : " + SymSell);
                                                            System.out.println("Avg. price : " + avgPrice);
                                                            System.out.println("Current price : " + cur_price);
                                                            System.out.println("---------------------------------------------");

                                                            System.out.println("If you want to sell, then Enter Y/y otherwise N/n");
                                                            String ch = sc.next();
                                                            if (ch.equalsIgnoreCase("N")) {
                                                                break;
                                                            }

                                                            int QtySell = User.SymQty.get(SymSell);
                                                            int sellqty = 0;
                                                            while (true) {
                                                                try {
                                                                    System.out.print("Enter quantity: ");
                                                                    System.out.println("You have " + QtySell + " shares of " + SymSell);
                                                                    sellqty = sc.nextInt();
                                                                    if (sellqty <= 0) {
                                                                        System.out.println("Quantity must be greater than 0. Try again.");
                                                                    } else {
                                                                        break;
                                                                    }
                                                                } catch (InputMismatchException e) {
                                                                    System.out.println("NOTICE : Invalid input. Please enter a valid integer.");
                                                                    sc.nextLine();
                                                                }
                                                            }
                                                            if (sellqty > QtySell) {
                                                                System.out.println("You can sell maximum " + QtySell + " shares");
                                                                break;
                                                            }
                                                            double sellAmount = cur_price * sellqty;
                                                            bal = bal + sellAmount;
                                                            String sql01 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                                            PreparedStatement pst05 = con.prepareStatement(sql01);
                                                            pst05.setDouble(1, bal);  // Set the balance
                                                            pst05.setString(2, user.email);
                                                            pst05.executeUpdate();
                                                            dbq.dbtransaction(user.email, SymSell, sellqty, cur_price, "SELL");
                                                            System.out.println("Transaction completed");
                                                            if (cur_price > avgPrice) {
                                                                double profit = (cur_price - avgPrice) * sellqty;
                                                                profit = ((int) (profit * 100)) / 100;
                                                                dbq.dbprofitloss(user.email,profit);
                                                                System.out.println("You make Profit of " + profit + " Rs.");
                                                            } else {
                                                                double loss = (avgPrice - cur_price) * sellqty;
                                                                loss = ((int) (loss * 100)) / 100;
                                                                dbq.dbprofitloss(user.email,-(loss));
                                                                System.out.println("You make Loss of " + loss + " Rs.");
                                                            }
                                                            break;
                                                        case 4:
                                                            user.showPortfolio(user.email);
                                                            if (User.SymQty.isEmpty()) {
                                                                System.out.println("No data available");
                                                            }
                                                            break;
                                                        case 5:
                                                            dbq.transactionHistory(user.email);
                                                            break;
                                                        case 6:
                                                            double fund = user.addFunds();
                                                            System.out.println("Funds added successfully!");

                                                            try {
                                                                con.setAutoCommit(false);

                                                                sql = "SELECT Balance FROM users WHERE Mail_id = ?";
                                                                pstt = con.prepareStatement(sql);
                                                                pstt.setString(1, user.email);
                                                                rs = pstt.executeQuery();

                                                                if (rs.next()) {
                                                                    bal = rs.getDouble("Balance");
                                                                }

                                                                bal = bal + fund;

                                                                sql1 = "UPDATE users SET balance = ? WHERE Mail_id = ?";
                                                                pst5 = con.prepareStatement(sql1);
                                                                pst5.setDouble(1, bal);
                                                                pst5.setString(2, user.email);
                                                                pst5.executeUpdate();

                                                                con.commit();

                                                                System.out.println("Your balance is = " + bal);

                                                            } catch (Exception e) {
                                                                try {
                                                                    con.rollback();
                                                                    System.out.println("Transaction failed! Rolled back.");
                                                                } catch (Exception ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                                e.printStackTrace();
                                                            } finally {
                                                                try {
                                                                    con.setAutoCommit(true);
                                                                } catch (Exception ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            }
                                                            break;

                                                        case 7:
                                                            System.out.println("Enter category for suggestion by our top experts");
                                                            String cat1 = sc.next();
                                                            String sugg = "SELECT category FROM stocks";
                                                            PreparedStatement psugg = con.prepareStatement(sugg);
                                                            ResultSet rsugg = psugg.executeQuery();

                                                            boolean found = false;

                                                            while (rsugg.next()) {
                                                                if (cat1.equalsIgnoreCase(rsugg.getString("category"))) {
                                                                    found = true;
                                                                    System.out.println("Category found");
                                                                    System.out.println("Here is the suggestion for you");

                                                                    String sugg2 = "SELECT SuggestedSymbol FROM suggestions WHERE category = ?";
                                                                    PreparedStatement pstsugg = con.prepareStatement(sugg2);
                                                                    pstsugg.setString(1, cat1);
                                                                    ResultSet rsugg2 = pstsugg.executeQuery();

                                                                    while (rsugg2.next()) {
                                                                        System.out.println(rsugg2.getString("SuggestedSymbol"));
                                                                    }

                                                                    rsugg2.close();
                                                                    pstsugg.close();
                                                                    break;
                                                                }
                                                            }

                                                            if (!found) {
                                                                System.out.println("Category not found");
                                                            }

                                                            rsugg.close();
                                                            psugg.close();
                                                            break;

                                                        case 8:
                                                            System.out.println("Good Bye,Have a nice day!");
                                                            return;
                                                    }
                                                }
                                            } else {
                                                System.out.println("oops! You entered Wrong Pass.");
                                                System.out.println("Kindly enter correct Password");
                                                count++;
                                                valpass=false;
                                                if(count==3){
                                                    System.out.println("Too many wrong attempts! Exiting...");
                                                }
                                            }
                                        }
                                    }
                                }
                            case 3:
                                System.out.println("Thank you!!");
                                return;
                        }
                    }
                case 3:
                    System.out.println("You are exiting the App");
                    break;
            }
            if(cho==3){
                break;
            }
        }
    }
}

