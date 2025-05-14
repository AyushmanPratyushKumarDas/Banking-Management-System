import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class BankingManagementSystem {
    public static void main(String[] args) throws IOException {
        Connection con = null;
        Statement stmt = null;
        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String conurl = "jdbc:oracle:thin:@172.17.144.110:1521:ora11g";
            con = DriverManager.getConnection(conurl, "id2241014018", "2241014018");
            stmt = con.createStatement();
            int choice;
            do {
                System.out.println("\n\n***** Banking Management System *****");
                System.out.println("1. Show Customer Records");
                System.out.println("2. Add Customer Record");
                System.out.println("3. Delete Customer Record");
                System.out.println("4. Update Customer Information");
                System.out.println("5. Show Account Details of a Customer");
                System.out.println("6. Show Loan Details of a Customer");
                System.out.println("7. Deposit Money to an Account");
                System.out.println("8. Withdraw Money from an Account");
                System.out.println("9. Exit the Program");
                System.out.print("Enter your choice (1-9): ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        ResultSet rs1 = stmt.executeQuery("SELECT * FROM CUSTOMER");
                        while (rs1.next()) {
                            System.out.printf("%-6s %-20s %-15s %-15s\n", rs1.getString(1), rs1.getString(2), rs1.getString(3), rs1.getString(4));
                        }
                        break;

                    case 2:
                        System.out.print("Enter Customer No: ");
                        String cno = sc.nextLine();
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter Phone No: ");
                        String phone = sc.nextLine();
                        System.out.print("Enter City: ");
                        String city = sc.nextLine();
                        stmt.executeUpdate("INSERT INTO CUSTOMER VALUES ('" + cno + "', '" + name + "', '" + phone + "', '" + city + "')");
                        System.out.println("Customer added successfully.");
                        break;

                    case 3:
                        System.out.print("Enter Customer No to delete: ");
                        String delCno = sc.nextLine();
                        stmt.executeUpdate("DELETE FROM CUSTOMER WHERE CUST_NO = '" + delCno + "'");
                        System.out.println("Customer deleted successfully.");
                        break;

                    case 4:
                        System.out.print("Enter Customer No to update: ");
                        String updCno = sc.nextLine();
                        System.out.println("1. Update Name\n2. Update Phone No\n3. Update City");
                        System.out.print("Enter your choice: ");
                        int updChoice = sc.nextInt();
                        sc.nextLine();
                        switch (updChoice) {
                            case 1:
                                System.out.print("Enter new Name: ");
                                String newName = sc.nextLine();
                                stmt.executeUpdate("UPDATE CUSTOMER SET NAME='" + newName + "' WHERE CUST_NO='" + updCno + "'");
                                break;
                            case 2:
                                System.out.print("Enter new Phone No: ");
                                String newPhone = sc.nextLine();
                                stmt.executeUpdate("UPDATE CUSTOMER SET PHONE_NO='" + newPhone + "' WHERE CUST_NO='" + updCno + "'");
                                break;
                            case 3:
                                System.out.print("Enter new City: ");
                                String newCity = sc.nextLine();
                                stmt.executeUpdate("UPDATE CUSTOMER SET CITY='" + newCity + "' WHERE CUST_NO='" + updCno + "'");
                                break;
                            default:
                                System.out.println("Invalid update choice.");
                        }
                        System.out.println("Customer information updated successfully.");
                        break;

                    case 5:
                        System.out.print("Enter Customer No: ");
                        String accCno = sc.nextLine();
                        ResultSet rs5 = stmt.executeQuery(
                            "SELECT C.CUST_NO, C.NAME, A.ACCOUNT_NO, A.TYPE, A.BALANCE, B.BRANCH_CODE, B.BRANCH_NAME, B.BRANCH_CITY " +
                            "FROM CUSTOMER C JOIN DEPOSITOR D ON C.CUST_NO = D.CUST_NO " +
                            "JOIN ACCOUNT A ON D.ACCOUNT_NO = A.ACCOUNT_NO " +
                            "JOIN BRANCH B ON A.BRANCH_CODE = B.BRANCH_CODE " +
                            "WHERE C.CUST_NO = '" + accCno + "'");
                        while (rs5.next()) {
                            System.out.printf("%-6s %-20s %-6s %-3s %-10d %-6s %-20s %-10s\n",
                                rs5.getString(1), rs5.getString(2), rs5.getString(3), rs5.getString(4),
                                rs5.getInt(5), rs5.getString(6), rs5.getString(7), rs5.getString(8));
                        }
                        break;

                    case 6:
                        System.out.print("Enter Customer No: ");
                        String loanCno = sc.nextLine();
                        ResultSet rs6 = stmt.executeQuery(
                            "SELECT C.CUST_NO, C.NAME, L.LOAN_NO, L.AMOUNT, B.BRANCH_CODE, B.BRANCH_NAME, B.BRANCH_CITY " +
                            "FROM CUSTOMER C JOIN LOAN L ON C.CUST_NO = L.CUST_NO " +
                            "JOIN BRANCH B ON L.BRANCH_CODE = B.BRANCH_CODE " +
                            "WHERE C.CUST_NO = '" + loanCno + "'");
                        boolean hasLoan = false;
                        while (rs6.next()) {
                            hasLoan = true;
                            System.out.printf("%-6s %-20s %-6s %-10d %-6s %-20s %-10s\n",
                                rs6.getString(1), rs6.getString(2), rs6.getString(3),
                                rs6.getInt(4), rs6.getString(5), rs6.getString(6), rs6.getString(7));
                        }
                        if (!hasLoan) {
                            System.out.println("Congratulations! The customer has no loan.");
                        }
                        break;

                    case 7:
                        System.out.print("Enter Account No to deposit: ");
                        String depAcc = sc.nextLine();
                        System.out.print("Enter amount to deposit: ");
                        int depAmt = sc.nextInt();
                        stmt.executeUpdate("UPDATE ACCOUNT SET BALANCE = BALANCE + " + depAmt + " WHERE ACCOUNT_NO = '" + depAcc + "'");
                        System.out.println("Amount deposited successfully.");
                        break;

                    case 8:
                        System.out.print("Enter Account No to withdraw: ");
                        String witAcc = sc.nextLine();
                        System.out.print("Enter amount to withdraw: ");
                        int witAmt = sc.nextInt();
                        ResultSet rsBal = stmt.executeQuery("SELECT BALANCE FROM ACCOUNT WHERE ACCOUNT_NO = '" + witAcc + "'");
                        if (rsBal.next()) {
                            int currentBal = rsBal.getInt(1);
                            if (currentBal >= witAmt) {
                                stmt.executeUpdate("UPDATE ACCOUNT SET BALANCE = BALANCE - " + witAmt + " WHERE ACCOUNT_NO = '" + witAcc + "'");
                                System.out.println("Amount withdrawn successfully.");
                            } else {
                                System.out.println("Insufficient balance.");
                            }
                        }
                        break;

                    case 9:
                        System.out.println("Exiting program.");
                        break;

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                }
            } while (choice != 9);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
