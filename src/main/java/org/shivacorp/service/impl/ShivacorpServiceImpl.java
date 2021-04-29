package org.shivacorp.service.impl;

import org.shivacorp.dao.ShivacorpDAO;
import org.shivacorp.dao.impl.ShivacorpDAOImpl;
import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.User;
import org.shivacorp.service.ShivacorpService;
import java.util.List;

public class ShivacorpServiceImpl implements ShivacorpService {
    private ShivacorpDAO shivacorpDAO = new ShivacorpDAOImpl();

        // CREATE
    @Override
    public User addUser(User user) throws BusinessException {
        if (shivacorpDAO.userExists(user)) {
            throw new BusinessException("Username '"+user.getUsername()+"' already exists. Please choose another.");
        } else {
            return shivacorpDAO.addUser(user);
        }
    }

    @Override
    public Account addAccount(Account account) throws BusinessException {
        if (shivacorpDAO.hasAccount(account.getUser()))
            throw new BusinessException("Account for "+account.getUser().getUsername()+" already exists");
        if (account.getBalance() < 500.0) {
            throw new BusinessException("Requires a starting balance of $500 or more.");
        }
        return shivacorpDAO.addAccount(account);
    }

        // READ
    @Override
    public List<Account> getAccounts() throws BusinessException {
        List<Account> accounts =  shivacorpDAO.getAccounts();
        if (accounts.isEmpty())
            throw new BusinessException("No existing accounts");
        return accounts;
    }

    @Override
    public List<Account> getUsers() throws BusinessException {
        List<Account> users =  shivacorpDAO.getUsers();
        if (users.isEmpty())
            throw new BusinessException("No existing accounts");
        return users;
    }

    @Override
    public List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException {
        List<Account> accountList = shivacorpDAO.getAccountsByStatus(status);
        if (accountList.isEmpty())
            throw new BusinessException("No pending accounts");
        return accountList;
    }

    @Override
    public User getUserByUsername(String username) throws BusinessException {
        return shivacorpDAO.getUserByUsername(username);
    }

    @Override
    public Account getAccountByUser(User user) throws BusinessException {
        Account account = shivacorpDAO.getAccountByUser(user);
        if (account == null) {
            throw new BusinessException("No account for user "+user.getUsername());
        }
        if (account.getStatus() == Account.StatusType.PENDING)
            throw new BusinessException("Your account is currently pending approval. Please try again later.");
        return account;
    }

        // UPDATE
    @Override
    public Account updateAccountStatus(Account account, Account.StatusType status) throws BusinessException {
        // update if approved
        if (status == Account.StatusType.APPROVED)
            account = shivacorpDAO.updateAccountStatus(account, status);
        // delete if denied
        else if (status == Account.StatusType.DENIED) {
            shivacorpDAO.deleteAccount(account);
            account.setStatus(status);
        }
        // do nothing if still pending
        return account;
    }

    @Override
    public Account deposit(User user, double amount) throws BusinessException {
        // no account and account pending exceptions are checked by getAccountByUser()
        Account account = getAccountByUser(user);
        // negative amount
        if (amount <= 0)
            throw new BusinessException("Deposit amount must be greater than zero");
        account = shivacorpDAO.updateBalance(account, account.getBalance() + amount);
        return account;
    }

    @Override
    public Account withdraw(User user, double amount) throws BusinessException {
        // no account and account pending exceptions are checked by getAccountByUser()
        Account account = getAccountByUser(user);
        // negative amount
        if (amount <= 0)
            throw new BusinessException("Withdrawal amount must be greater than zero");
        // insufficient funds
        if (amount > account.getBalance())
            throw new BusinessException("Insufficient funds to make withdrawal");
        account = shivacorpDAO.updateBalance(account, account.getBalance() - amount);
        return account;
    }


    // DELETE
//    @Override
//    public void deleteAccount(Account account) throws BusinessException { }

        // Utility methods
//    @Override
//    public boolean userExists(User user) throws BusinessException {
//        return shivacorpDAO.userExists(user);
//    }

}
