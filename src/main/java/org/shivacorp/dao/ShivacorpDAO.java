package org.shivacorp.dao;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.User;
import java.util.List;

public interface ShivacorpDAO {
    User getUserByUsername(String username) throws BusinessException;
    User addUser(User user) throws BusinessException;
    Account addAccount(Account account) throws BusinessException;
    List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException;
    Account getAccountByUsername(String username) throws BusinessException;
    Account updateAccountStatus(Account account, Account.StatusType status) throws BusinessException;
    boolean userExists(User user) throws BusinessException;
    boolean accountExists(Account account) throws BusinessException;
    List<Account> getAccounts() throws BusinessException;
}
