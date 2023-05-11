package Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    //AccountService class  will provide business logic to perform operations on the Account object
    public AccountDAO accountDAO;

    //default constructor
    public AccountService(){
        this.accountDAO = new AccountDAO();
    }

    public Account addAccount(Account account) {
        Account createdAccount = accountDAO.createAccount(account);
        if (createdAccount == null) {
            return null;
        } else {
            return createdAccount;
        }
    }

    // public Account addAccount(Account account)  {
    //     return accountDAO.createAccount(account);
    
    // }

    
    public Account getAccountByUsernameAndPassword(String username, String password) throws SQLException {
        return accountDAO.getAccountByUsernameAndPassword(username, password);
    }


    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public Account getAccountById(int id) {
        return accountDAO.getAccountById(id);
    }

    
    
    
    
    
    
    
    


    
}
