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

    @Override
    public User addUser(User user) throws BusinessException {
        if (shivacorpDAO.userExists(user)) {
            throw new BusinessException("Username '"+user.getUsername()+"' already exists.");
        } else {
            return shivacorpDAO.addUser(user);
        }
    }

    @Override
    public Account addAccount(Account account) throws BusinessException {
//        Account checkForAccount =
        if (shivacorpDAO.accountExists(account))
            throw new BusinessException("Account for "+account.getUser().getUsername()+" already exists");
        if (account.getBalance() < 500.0) {
            throw new BusinessException("Requires a starting balance of $500 or more.");
        }
        return shivacorpDAO.addAccount(account);
    }

    @Override
    public List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException {
        List<Account> accountList = shivacorpDAO.getAccountsByStatus(status);
        return accountList;
    }

    @Override
    public User getUserByUsername(String username) throws BusinessException {
        return shivacorpDAO.getUserByUsername(username);
    }

    @Override
    public Account getAccountByUsername(String username) throws BusinessException {
        return shivacorpDAO.getAccountByUsername(username);
    }

    @Override
    public Account updateAccountStatus(Account account, Account.StatusType status) throws BusinessException {
        return shivacorpDAO.updateAccountStatus(account, status);
    }

    @Override
    public boolean userExists(User user) throws BusinessException {
        return shivacorpDAO.userExists(user);
    }

    @Override
    public boolean accountExists(Account account) throws BusinessException {
        return shivacorpDAO.accountExists(account);
    }

    @Override
    public List<Account> getAccounts() throws BusinessException {
        return shivacorpDAO.getAccounts();
    }
}
