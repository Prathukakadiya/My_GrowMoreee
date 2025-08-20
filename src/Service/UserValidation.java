package Service;
import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

public class UserValidation {
    // Validation
    public String name;
    public String dob;
    public String aadhar;
    public String pan;
    public String email;
    public String password;
    public int age;

    static Scanner sc = new Scanner(System.in);

    public void UserInput() throws SQLException {
        // Name
        boolean validname;
        do {
            System.out.print("Enter your name: ");
            this.name = sc.nextLine();
            validname = true;

            if (name.length() == 0) {
                System.out.println("NOTICE : Invalid Name");
                validname = false;
            }

            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (!Character.isLetter(c)) {
                    System.out.println("NOTICE : Invalid Name: Only alphabets allowed");
                    validname = false;
                    break;
                }
            }
            String sname="select username from users";
            Connection con = DBConnection.getConnection();
            PreparedStatement pstname=con.prepareStatement(sname);
            ResultSet rsname=pstname.executeQuery();
            while(rsname.next()){
                if(name.equals(rsname.getString(1))){
                    System.out.println("name is already exists please enter diffrent name");
                    validname=false;
                    break;
                }
            }
        } while (!validname);

        // Date of Birth
        boolean validDate = false;
        do {
            System.out.print("Enter your Date of Birth (yyyy-MM-dd): ");
            String dobInput = sc.nextLine();

            try {
                LocalDate dobDate = LocalDate.parse(dobInput);
                LocalDate today = LocalDate.now();
                this.age = Period.between(dobDate, today).getYears();

                if (age >= 18) {
                    validDate = true;
                    this.dob = dobInput;
                } else {
                    System.out.println("You must be at least 18 years old.");
                }
            } catch (Exception e) {
                System.out.println("NOTICE : Invalid date format. Please use yyyy-MM-dd");
            }

        } while (!validDate);

        // Aadhar
        boolean validAadhar;
        do {
            System.out.print("Enter Your Aadhar Number: ");
            this.aadhar = sc.nextLine();
            validAadhar = true;

            if (aadhar.length() != 12) {
                validAadhar = false;
            } else {
                for (int i = 0; i < 12; i++) {
                    if (!Character.isDigit(aadhar.charAt(i))) {
                        validAadhar = false;
                        break;
                    }
                }
            }

            if (!validAadhar) {
                System.out.println("NOTICE : Invalid Aadhar number. Please try again.");
            }

        } while (!validAadhar);

        // PAN
        boolean validPan;
        do {
            System.out.print("Enter Your PAN Number: ");
            this.pan = sc.nextLine();
            validPan = true;

            if (pan.length() != 10) {
                validPan = false;
            } else {
                for (int i = 0; i < 5; i++) {
                    if (!Character.isUpperCase(pan.charAt(i))) {
                        validPan = false;
                        break;
                    }
                }

                for (int i = 5; i < 9 && validPan; i++) {
                    if (!Character.isDigit(pan.charAt(i))) {
                        validPan = false;
                        break;
                    }
                }

                if (validPan && !Character.isUpperCase(pan.charAt(9))) {
                    validPan = false;
                }
            }

            if (!validPan) {
                System.out.println("NOTICE : Invalid PAN number. Please enter again.");
            }

        } while (!validPan);

        // Email
        boolean mailvalidate=true;
        while(mailvalidate) {
            System.out.print("Enter your E-mail id: ");
            email= sc.nextLine();
            String mail = validMail(email);
            if (mail == null) {
                System.out.println("Enter Valid Email");
            }
            else{
                mailvalidate=false;
            }
        }

        // Password
        do {
            System.out.print("Enter your password: ");
            this.password = sc.nextLine();

            if (password.length() < 6) {
                System.out.println("Password must be at least 6 characters.");
            }

        } while (password.length() < 6);
    }

    public String validMail(String email){
        boolean isValidmail;
        do {
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
                return null;
            }
        } while (!isValidmail);
        return email;
    }
}

