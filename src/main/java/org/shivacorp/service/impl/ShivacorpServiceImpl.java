package org.shivacorp.service.impl;

import java.util.List;
import org.shivacorp.dao.ShivacorpDAO;
import org.shivacorp.dao.impl.ShivacorpDAOImpl;
import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.Transaction;
import org.shivacorp.model.User;
import org.shivacorp.service.ShivacorpService;

public class ShivacorpServiceImpl implements ShivacorpService {
    private final ShivacorpDAO shivacorpDAO = new ShivacorpDAOImpl();

    // CREATE
    @Override
    public User register(User user) throws BusinessException {
        if (shivacorpDAO.userExists(user)) {
            throw new BusinessException("Username '"+user.getUsername()+"' already exists. Please choose another.");
        } else {
            user = shivacorpDAO.addUser(user);
            return user;
        }
    }

    @Override
    public Account addAccount(User user) throws BusinessException {
        // account already exists
        if (shivacorpDAO.hasAccount(user))
            throw new BusinessException("Account for "+user.getUsername()+" already exists");

        Account account = new Account(user, 0, Account.StatusType.PENDING);
        account = shivacorpDAO.addAccount(account);
        return account;
    }

    // READ
    @Override
    public User login(User user) throws BusinessException {
        // lookup username
        User result = shivacorpDAO.getUserByUsername(user.getUsername());

        // user not found
        if (result == null)
            throw new BusinessException("Username '"+user.getUsername()+"' does not exist");

        // username found, failed authentication
        if (!user.getPassword().equals(result.getPassword()))
            throw new BusinessException("Authentication failed");

        // user authenticated
        return result;
    }

    @Override
    public List<Account> getUsers() throws BusinessException {
        List<Account> users =  shivacorpDAO.getUsers();
        if (users.isEmpty())
            throw new BusinessException("No existing accounts");
        return users;
    }

    @Override
    public List<Account> getAccounts() throws BusinessException {
        List<Account> accounts =  shivacorpDAO.getAccounts();
        if (accounts.isEmpty())
            throw new BusinessException("No existing accounts");
        return accounts;
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

    @Override
    public List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException {
        List<Account> accountList = shivacorpDAO.getAccountsByStatus(status);
        if (accountList.isEmpty())
            throw new BusinessException("No pending accounts");
        return accountList;
    }

    @Override
    public List<Transaction> viewTransactions() throws BusinessException {
        List<Transaction> transactions = shivacorpDAO.getTransactions();
        if (transactions.isEmpty())
            throw new BusinessException("No transactions to view");
        return transactions;
    }

    @Override
    public boolean hasActiveAccount(User user) throws BusinessException {
        Account account = shivacorpDAO.getAccountByUser(user);
        if (account == null)
            throw new BusinessException("N");
        return (account != null) && (account.getStatus() == Account.StatusType.APPROVED);
    }

    // UPDATE
    @Override
    public Account updateStatus(Account account, Account.StatusType status) throws BusinessException {
        // update if approved
        if (status == Account.StatusType.APPROVED) {
            account = shivacorpDAO.updateStatus(account, status);

            // add transaction
            Transaction transaction = new Transaction.Builder()
                    .withTimestamp()
                    .withTransactionType(Transaction.TransactionType.ACCOUNT_APPROVED)
                    .withAccountId(account.getId())
                    .withAmount(account.getBalance())
                    .build();
            shivacorpDAO.addTransaction(transaction);
        }
        // delete if denied
        else if (status == Account.StatusType.DENIED) {
            account.setStatus(status);
            shivacorpDAO.deleteAccount(account);
        }
        // do nothing if still pending
        return account;
    }

    @Override
    public Account deposit(User user, double amount) throws BusinessException {
        // exceptions for no account and account pending are checked by getAccountByUser()
        Account account = getAccountByUser(user);

        // negative amount
        if (amount <= 0)
            throw new BusinessException("Deposit amount must be greater than zero");

        // make deposit and add to transactions
        account = shivacorpDAO.updateBalance(account, account.getBalance() + amount);
        Transaction transaction = new Transaction.Builder()
                .withTimestamp()
                .withTransactionType(Transaction.TransactionType.DEPOSIT)
                .withAccountId(account.getId())
                .withAmount(amount)
                .build();
        shivacorpDAO.addTransaction(transaction);
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

        // make withdrawal and add to transactions
        account = shivacorpDAO.updateBalance(account, account.getBalance() - amount);
        Transaction transaction = new Transaction.Builder()
                .withTimestamp()
                .withTransactionType(Transaction.TransactionType.WITHDRAWAL)
                .withAccountId(account.getId())
                .withAmount(amount)
                .build();
        shivacorpDAO.addTransaction(transaction);
        return account;
    }

    @Override
    public Account transfer(User user, int toAccountId, double amount) throws BusinessException {
        Account fromAccount = shivacorpDAO.getAccountByUser(user);

        // source and destination accounts are the same
        if (fromAccount.getId() == toAccountId)
            throw new BusinessException("Source and destination accounts can't be the same");

        // user has no account
        if (fromAccount == null) {
            throw new BusinessException("No account for username "+user.getUsername());
        }

        // user has insufficient funds
        if (fromAccount.getBalance() < amount)
            throw new BusinessException("Insufficient funds to cover transfer");

        // other account doesn't exist
        Account toAccount = shivacorpDAO.getAccountById(toAccountId);
        if (toAccount == null)
            throw new BusinessException("Destination account doesn't exist");

        // other account is pending
        if (toAccount.getStatus() == Account.StatusType.PENDING)
            throw new BusinessException("Destination account is currently pending approval");

        // update source account
        fromAccount =  shivacorpDAO.updateBalance(fromAccount, fromAccount.getBalance() - amount);

        // add transaction showing debit from source account
        Transaction transaction = new Transaction.Builder()
                .withTimestamp()
                .withTransactionType(Transaction.TransactionType.TRANSFER_DEBIT)
                .withAccountId(fromAccount.getId())
                .withAmount(amount)
                .build();
        shivacorpDAO.addTransaction(transaction);

        // update destination account
        shivacorpDAO.updateBalance(toAccount, toAccount.getBalance() + amount);

        // add transaction showing credit to destination account
        transaction = new Transaction.Builder()
                .withTimestamp()
                .withTransactionType(Transaction.TransactionType.TRANSFER_CREDIT)
                .withAccountId(toAccount.getId())
                .withAmount(amount)
                .build();
        shivacorpDAO.addTransaction(transaction);

        return fromAccount;
    }
}
