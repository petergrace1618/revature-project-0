package org.shivacorp.service;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.Transaction;
import org.shivacorp.model.User;
import java.util.List;

public interface ShivacorpService {
        // CREATE
    User register(User user) throws BusinessException;
    Account addAccount(User user) throws BusinessException;

        // READ
    User login(User user) throws BusinessException;
    List<Account> getUsers() throws BusinessException;
    List<Account> getAccounts() throws BusinessException;
    User getUserByUsername(String username) throws BusinessException;
    Account getAccountByUser(User user) throws BusinessException;
    List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException;
    List<Transaction> viewTransactions() throws BusinessException;
    boolean hasActiveAccount(User user) throws BusinessException;

        // UPDATE
    Account updateStatus(Account account, Account.StatusType status) throws BusinessException;
    Account withdraw(User user, double amount) throws BusinessException;
    Account deposit(User user, double amount) throws BusinessException;
    Account transfer(User user, int toAccountId, double amount) throws BusinessException;

        // DELETE
//    void deleteAccount(Account account) throws BusinessException;
}
