package org.shivacorp.dao;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.Transaction;
import org.shivacorp.model.User;
import java.util.List;

public interface ShivacorpDAO {
        // CREATE
    User addUser(User user) throws BusinessException;
    Account addAccount(Account account) throws BusinessException;
    Transaction addTransaction(Transaction transaction) throws BusinessException;

        // READ
    User getUserByUsername(String username) throws BusinessException;
    Account getAccountByUser(User user) throws BusinessException;
    List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException;
    Account getAccountById(int id) throws BusinessException;
    List<Account> getAccounts() throws BusinessException;
    List<Account> getUsers() throws BusinessException;
    List<Transaction> getTransactions() throws BusinessException;

        // UPDATE
    Account updateAccountStatus(Account account, Account.StatusType status) throws BusinessException;
    Account updateBalance(Account account, double amount) throws BusinessException;

        // DELETE
    void deleteAccount(Account account) throws BusinessException;

        // Utility methods
    boolean userExists(User user) throws BusinessException;
    boolean hasAccount(User user) throws BusinessException;
}
