package DAO;

import java.sql.Connection;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.*;
import Util.ConnectionUtil;

public class AccountDAO {
    // Get a connection object from ConnectionUtil
    Connection connection = ConnectionUtil.getConnection();
    // Initialize an empty list to store all Account objects retrieved from the
    // database
    List<Account> accountList = new ArrayList<>();

   // post -- This method will handle the user registration by creating a new account in the database
public Account createAccount(Account account) {
    try {
        // Check if the username and password meet the required criteria
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            throw new IOException("Username is blank or password length is less than 4");
        }

        // Check if the username already exists in the database
        if (checkUsernameExists(account.getUsername())) {
            throw new IOException("Username already exists"); // Throw an exception if the username already exists
        }

        String sql = "INSERT INTO account (username, password) Values(?,?)";
        //The Statement.RETURN_GENERATED_KEYS flag is used to indicate that the PreparedStatement object should return any auto-generated keys that were generated by executing the SQL statement, if any.
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        // Setting values for the parameters of the prepared statement
        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setString(2, account.getPassword());
        // executing the preparedStatement
        preparedStatement.executeUpdate();
        // Retrieving the auto-generated primary key
        ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
        // using if statement to check if a new account was successfully created and if
        // so, returns the new Account object with the generated ID.
        if (pkeyResultSet.next()) {
            // if pkeyResultSet exists , Retrieving the generated account_id
            int generated_account_id = (int) pkeyResultSet.getLong(1);
            // Creating a new Account object with the generated account_id and the usernam and password of the input account
            return new Account(generated_account_id, account.getUsername(), account.getPassword());
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    } catch (IOException e) {
        e.printStackTrace();
    }
    // Returning null if no account was created
    return null;
}


    // check if there is account with the same username
    public boolean checkUsernameExists(String username) {
        try {
            String sql = "SELECT COUNT(*) FROM account WHERE username = ?";
            // String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }


    // login -- This method will handle the user login by verifying if the provided
    // username and password match a real account existing in the database.
    public Account getAccountByUsernameAndPassword(String username, String password) {
        try { 
            // Check if the username already exists in the database
            if (!checkUsernameAndPasswordExists(username, password)) {
                throw new IOException("Username and password do not exist"); 
            }

            // SQL logic to retrive account with specifce username and password
            String sql = "select * from account where username = ? and password = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // write PreparedStatement setString and setInt methods here.
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Account account = new Account(rs.getInt("account_id"), rs.getString("username"),
                        rs.getString("password"));
                return account;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

     // check if there is account with the same username and password
     public boolean checkUsernameAndPasswordExists(String username, String password) {
        try {
            String sql = "SELECT COUNT(*) FROM account WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
    
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    //I have these next to methods but it did not required in theis pre-project
    // This method retrieves all accounts from the account table in the database and
    // returns them as a list of Account objects.
    public List<Account> getAllAccounts() {

        // SQL logic here to get all accounts
        String sql = "SELECT * FROM account;";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()) { // method uses a try-with-resources block to
                                                                   // automatically close the Connection,
                                                                   // PreparedStatement, and ResultSet objects after
                                                                    // they are used.
                List<Account> allAccount = new ArrayList<>();                                                   
            // Loop through each row in the ResultSet
            while (rs.next()) {
                // For each row, create a new Account object using the data from the current row
                Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"));
                // Add the newly created Account object to the list
                allAccount.add(account);
                return allAccount;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // This method retrieves a specific account from the database using its ID, and
    // returns it as an Account object.
    public Account getAccountById(int id) {
        try (
                // Create a PreparedStatement object for executing a SQL query
                PreparedStatement preparedStatement = connection
                        .prepareStatement("select * from account where account_id = ?")) {
            // Set the value for the first parameter of the PreparedStatement object using
            // the id parameter
            preparedStatement.setInt(1, id);
            // Execute the query and get a ResultSet object
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                // Create a new Account object using the data from the current row
                Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"));
                // Return the Account object
                return account;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
