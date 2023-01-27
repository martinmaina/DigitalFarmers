package database;

import controller.FarmerPageController;
import functions.functions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.swing.JOptionPane;

/**
 * @author Martin Maina
 */
public class DatabaseFunctions {

    private static final String url = "jdbc:mysql://localhost:3306/digitalfarmers";
    private static final String pass = "";
    private static final String user = "root";
    private static Connection con = null;
    private static Statement stmt = null;

    public static void Connection() {

    }

    public static ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = con.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Exception at execQuery:database " + e.getLocalizedMessage());
            return null;
        }
        return result;
    }

    public static boolean execAction(String query) {
        try {
            stmt = con.createStatement();
            stmt.execute(query);
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execAction:database " + e.getLocalizedMessage());
            return false;
        }
    }

    public static boolean CheckUserExists(String username) {
        boolean exists = false;

        return !exists;
    }

    public static int getId(String username) {
        int id = 0;
        String sql = "SELECT userid FROM users WHERE username='" + username + "'";
        try {
            con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql);

            while (rs.next()) {
                id = rs.getInt("userid");
            }
        } catch (SQLException e) {
            System.out.println("GET ID " + e.getLocalizedMessage());
        }
        return id;
    }

    public static String getRole(String username) {
        String role = "";
        String sql = "SELECT role FROM users where username='" + username + "'";

        try {
            con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql);

            while (rs.next()) {
                role = rs.getString("password");
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return role;
    }

    public static String getRole(int id) {
        String role = "";
        String sql = "SELECT role FROM users where userid='" + id + "'";
        try {
            con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql);

            while (rs.next()) {
                role = rs.getString("role");
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return role;
    }

    public static void loggedIn(String USER, int id) {
        String sql1 = "DELETE  FROM loggedin";
        String sql = USER.equals("CUSTOMER") ? "INSERT INTO loggedin VALUES('CUSTOMER', '" + id + "')" : "INSERT INTO loggedin VALUES('FARMER', '" + id + "')";

        try {
            con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql);
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.execute();
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean login(String username, String password) {

        boolean isAuthenticated;

        String hashedPassword = functions.generateHash(password);
        isAuthenticated = hashedPassword.equals(verifyPassword(username, password));

        return isAuthenticated;

    }

    public static String verifyPassword(String username, String password) {

        String storedHashedPassword = null;

        try {

            con = DriverManager.getConnection(url, user, pass);

            String query = "select * from users where username ='" + username + "' ";

            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery(query);

            while (rs.next()) {

                storedHashedPassword = rs.getString("password");

            }

        } catch (SQLException e) {
            functions.dialogPopUp("Error!", e.getMessage());
        }

        return storedHashedPassword;
    }

    public static boolean loginMain(String username, String password) {
        boolean isAuthenticated = false;

        String passw = "";

        try {
            con = DriverManager.getConnection(url, user, pass);
            String query = "SELECT * FROM users WHERE username= '" + username + "'";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery(query);
            while (rs.next()) {
                passw = rs.getString("password");
            }
        } catch (SQLException e) {
            System.out.println("Login Main " + e.getMessage());
        }

        if (passw.equals(functions.generateHash(password))) {
            isAuthenticated = true;
        }
        return isAuthenticated;
    }

    public static boolean addNewCollectionPoint(String name, String address) {
        String query = "INSERT INTO collectionpoints (name, id, address, commision) VALUES('" + name + "', NULL, '" + address + "', 0.0)";
        return execAction(query);
    }

    public static boolean addNewProduct(String name, String price, String quantity, String username) {
        int farmerId = getId(username);
        String query = "INSERT INTO products  VALUES('" + farmerId + "','" + name + "', NULL, '" + price + "', '" + quantity + "')";

        return execAction(query);
    }

    public static boolean addNewUser(String role, String password, String username) {
        String query = "INSERT INTO users VALUES('" + username + "','" + role + "','', '" + password + "','')";
        return execAction(query);
    }
    
    public static boolean addNewCustomer(String name, String phoneNumber, String address, String email){
        String query = "INSERT INTO customers VALUES('"+name+"',NULL,'"+phoneNumber+"', '"+address+"', '"+email+"')";
        return execAction(query);
    }
      public static boolean addNewFarmer(String name,  String phoneNumber, String address, String email){
        String query = "INSERT INTO farmers VALUES('"+name+"','"+address+"',NULL, 0,'"+phoneNumber+"', '"+email+"')";
        return execAction(query);
    }
    
    public static String getPurchasedProductsByProductId(String productId){
        String purchasedProducts = "";
        String query = "SELECT SUM(quantity) FROM orders WHERE productid='"+productId+"'"; ResultSet rs = database.DatabaseFunctions.execQuery(query);
        
        try {
            while(rs.next()){
                purchasedProducts = rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return purchasedProducts;
    }

    public static String checkLoggedIn() {

        String UserLoggedIn = "";
        try {

            String query = "SELECT userid FROM loggedin LIMIT 1";
            ResultSet rs = DatabaseFunctions.execQuery(query);
            while (rs.next()) {
                UserLoggedIn = rs.getString("userid");
            }
            return UserLoggedIn;
        } catch (SQLException ex) {
            Logger.getLogger(FarmerPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return UserLoggedIn;
    }
}
