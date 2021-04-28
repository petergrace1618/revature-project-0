package org.shivacorp.dao.impl;

import org.shivacorp.dao.ShivacorpDAO;
import org.shivacorp.dao.dbutil.PostgreSQLConnection;
import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShivacorpDAOImpl implements ShivacorpDAO {

    @Override
    public User getUserByUsername(String username) throws BusinessException {
        User user = null;
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id, u.username, u.password, u.usertype, u.accountid, "+
                        "a.id, a.userid, a.balance::money::numeric::float8, a.status "+
                    "FROM shivacorp_schema.users as u "+
                    "LEFT JOIN shivacorp_schema.accounts as a ON u.id = a.userid "+
                    "WHERE username = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                Account account = new Account();
                account.setId(resultSet.getInt("accountid"));
                account.setBalance(resultSet.getDouble("balance"));
                String status = resultSet.getString("status");
                account.setStatus(status != null ? Account.StatusType.valueOf(status) : null);
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
                user.setAccount(account);
                account.setUser(user);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getUserByUsername: "+e.getMessage());
        }
        return user;
    }

    @Override
    public User addUser(User user) throws BusinessException {
        try (Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "INSERT INTO shivacorp_schema.users (username, password, usertype) "+
                    "VALUES (?, ?, CAST(? AS shivacorp_schema.user_type));";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getUsertype().name());
            int c = preparedStatement.executeUpdate();
            if (c == 1) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    user.setId(resultSet.getInt(1));
                }
            } else {
                throw new BusinessException("Failed: Add user");
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".addUser: "+e.getMessage());
        }
        return user;
    }

    @Override
    public Account addAccount(Account account) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "INSERT INTO shivacorp_schema.accounts (userid, balance, status) "+
                    "VALUES (?, ?::float8::numeric::money, CAST(? AS shivacorp_schema.status_type));";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, account.getUserId());
            preparedStatement.setDouble(2, account.getBalance());
            preparedStatement.setString(3, Account.StatusType.PENDING.name());
            int c = preparedStatement.executeUpdate();
            if (c == 1) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    account.setId(resultSet.getInt(1));
                }
            } else {
                throw new BusinessException("Failed: Create new account");
            }
            // new account created now update user record to point to new account
            sql = "UPDATE shivacorp_schema.users SET accountid = ? WHERE id = ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account.getId());
            preparedStatement.setInt(2, account.getUserId());
            c = preparedStatement.executeUpdate();
            if (c == 0) {
                throw new BusinessException("Failed: Associate user with new account");
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".addAccount: "+e.getMessage());
        }
        return account;
    }

    @Override
    public List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException {
        List<Account> accountList = new ArrayList<>();
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id as user_id, u.username, u.password, u.usertype, u.accountid, "+
                        "a.id, a.userid, a.balance::money::numeric::float8, a.status "+
                    "FROM shivacorp_schema.accounts as a "+
                    "LEFT JOIN shivacorp_schema.users as u ON u.id = a.userid "+
                    "WHERE a.status = CAST(? AS shivacorp_schema.status_type);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
                user.setAccountId(resultSet.getInt("accountid"));

                Account account = new Account();
                account.setId(resultSet.getInt("id"));
                account.setUserId(resultSet.getInt("userid"));
                account.setBalance(resultSet.getDouble("balance"));
                account.setStatus(Account.StatusType.valueOf(resultSet.getString("status")));

                // user.setAccount(account) causes an infinite loop when toString() is called
                account.setUser(user);
                accountList.add(account);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getAccountsByStatus: "+e.getMessage());
        }
        return accountList;
    }

    @Override
    public Account updateAccountStatus(Account account, Account.StatusType status) throws BusinessException {
        try (Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "UPDATE shivacorp_schema.accounts "+
                    "SET status = CAST(? AS shivacorp_schema.status_type) "+
                    "WHERE id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status.name());
            preparedStatement.setInt(2, account.getId());
            int c = preparedStatement.executeUpdate();
            // Is it necessary to check return value of executeUpdate()?
            if (c == 1) {
                account.setStatus(status);
            } else {
                throw new BusinessException(className()+".updateAccountStatus: Failed: Update account status");
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".updateAccountStatus: "+e.getMessage());
        }
        return account;
    }

    @Override
    public boolean userExists(User user) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql = "SELECT username FROM shivacorp_schema.users WHERE username = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return true;
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean accountExists(Account account) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql = "SELECT userid FROM shivacorp_schema.accounts WHERE userid = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account.getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return true;
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Account> getAccounts() throws BusinessException {
        List<Account> accountList = new ArrayList<>();
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id, u.username, u.usertype, u.accountid, "+
                        "a.id, a.userid, a.balance::money::numeric::float8, a.status "+
                    "FROM shivacorp_schema.users as u "+
                    "JOIN shivacorp_schema.accounts as a ON u.accountid = a.id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();
            while(resultSet.next()) {
                User user = new User();
                Account account = new Account();
                account.setId(resultSet.getInt("accountid"));
                account.setBalance(resultSet.getDouble("balance"));
                String status = resultSet.getString("status");
                account.setStatus(status != null ? Account.StatusType.valueOf(status) : null);
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
                account.setUser(user);
                accountList.add(account);
            }
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage());
        }
        return accountList;
    }

    @Override
    public Account getAccountByUsername(String username) throws BusinessException {
        Account account = null;
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id, u.username, u.password, u.usertype, u.accountid, "+
                            "a.id, a.userid, a.balance, a.status "+
                            "FROM shivacorp_schema.users as u "+
                            "LEFT JOIN shivacorp_schema.accounts as a ON u.id = a.userid "+
                            "WHERE username = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                account = new Account();
                account.setId(resultSet.getInt("accountid"));
                account.setBalance(resultSet.getDouble("balance"));
                String status = resultSet.getString("status");
                account.setStatus(status != null ? Account.StatusType.valueOf(status) : null);
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
                user.setAccount(account);
                account.setUser(user);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getUserByUsername: "+e.getMessage());
        }
        return account;
    }

    private String className() {
        String c = this.getClass().getName();
        return c.substring(c.lastIndexOf('.')+1);
    }
}
