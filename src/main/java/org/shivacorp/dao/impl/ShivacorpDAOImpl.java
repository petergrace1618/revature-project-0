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
    // CREATE
    @Override
    public User addUser(User user) throws BusinessException {
        try (Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "INSERT INTO shivacorp_schema.users (username, password, fullname, usertype) "+
                            "VALUES (?, ?, ?, CAST(? AS shivacorp_schema.user_type));";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFullName());
            preparedStatement.setString(4, user.getUsertype().name());
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
        } catch (SQLException e) {
            throw new BusinessException(className()+".addAccount: "+e.getMessage());
        }
        return account;
    }

    // READ
    @Override
    public User getUserByUsername(String username) throws BusinessException {
        User user = null;
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT id, username, password, fullname, usertype "+
                            "FROM shivacorp_schema.users WHERE username = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setFullName(resultSet.getString("fullname"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getUserByUsername: "+e.getMessage());
        }
        return user;
    }

    @Override
    public List<Account> getAccountsByStatus(Account.StatusType status) throws BusinessException {
        List<Account> accountList = new ArrayList<>();
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id as user_id, u.username, u.fullname, u.usertype, "+
                            "a.id as account_id, a.balance::money::numeric::float8, a.status "+
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
                user.setFullName(resultSet.getString("fullname"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));

                Account account = new Account();
                account.setId(resultSet.getInt("account_id"));
//                account.setUserId(resultSet.getInt("user_id"));
                account.setBalance(resultSet.getDouble("balance"));
                account.setStatus(Account.StatusType.valueOf(resultSet.getString("status")));

                account.setUser(user);
                accountList.add(account);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getAccountsByStatus: "+e.getMessage());
        }
        return accountList;
    }

    @Override
    public Account getAccountByUser(User user) throws BusinessException {
        Account account = null;
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id, u.username, u.fullname, u.usertype, "+
                            "a.id as account_id, a.balance::money::numeric::float8, a.status "+
                    "FROM shivacorp_schema.users as u "+
                    "JOIN shivacorp_schema.accounts as a ON u.id = a.userid "+
                    "WHERE u.id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                account = new Account();
                account.setId(resultSet.getInt("account_id"));
                account.setBalance(resultSet.getDouble("balance"));
//                String status = resultSet.getString("status");
//                account.setStatus(status != null ? Account.StatusType.valueOf(status) : null);
                account.setStatus(Account.StatusType.valueOf(resultSet.getString("status")));

//                user.setId(resultSet.getInt("id"));
//                user.setUsername(resultSet.getString("username"));
//                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
                account.setUser(user);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getUserByUsername: "+e.getMessage());
        }
        return account;
    }

    @Override
    public List<Account> getAccounts() throws BusinessException {
        List<Account> accountList = new ArrayList<>();
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT a.id, a.userid, a.balance::money::numeric::float8, a.status, "+
                            "u.username, u.fullname, u.usertype "+
                            "FROM shivacorp_schema.accounts as a "+
                            "JOIN shivacorp_schema.users as u ON a.userid = u.id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();
            while(resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("userid"));
                user.setUsername(resultSet.getString("username"));
                user.setFullName(resultSet.getString("fullname"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));

                Account account = new Account();
                account.setId(resultSet.getInt("id"));
                account.setBalance(resultSet.getDouble("balance"));
                account.setStatus(Account.StatusType.valueOf(resultSet.getString("status")));
                account.setUser(user);
                accountList.add(account);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getAccounts: "+e.getMessage());
        }
        return accountList;
    }

    @Override
    public List<Account> getUsers() throws BusinessException {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "SELECT u.id as user_id, username, fullname, usertype, "+
                            "a.id as account_id, balance::money::numeric::float8, status "+
                    "FROM shivacorp_schema.users as u "+
                    "LEFT JOIN shivacorp_schema.accounts as a ON u.id = a.userid "+
                    "WHERE username <> 'admin';";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();
            while(resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setFullName(resultSet.getString("fullname"));
                user.setUsertype(User.Usertype.valueOf(resultSet.getString("usertype")));
                Account account = new Account();
                account.setUser(user);
                int id = resultSet.getInt("account_id");
                if (id != 0) {
                    account.setId(id);
                    account.setBalance(resultSet.getDouble("balance"));
                    account.setUserId(resultSet.getInt("user_id"));
                    account.setStatus(Account.StatusType.valueOf(resultSet.getString("status")));
                } // if id == 0 then the row is NULL
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".getUsers: "+e.getMessage());
        }
        return accounts;
    }

    // UPDATE
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
            account.setStatus(status);
            int c = preparedStatement.executeUpdate();
            // Is it necessary to check return value of executeUpdate()?
            if (c == 0) {
                throw new BusinessException(
                        className()+".updateAccountStatus: Failed: Update account status id="+account.getId()
                );
            }
        } catch (SQLException e) {
            throw new BusinessException(className()+".updateAccountStatus: "+e.getMessage());
        }
        return account;
    }

    @Override
    public Account updateBalance(Account account, double amount) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "UPDATE shivacorp_schema.accounts "+
                    "SET balance = ?::float8::numeric::money "+
                    "WHERE id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, account.getId());
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount == 0)
                throw new BusinessException(
                        className()+".updateBalance: Failed: update balance id="+account.getId()
                );
            account.setBalance(amount);
        } catch (SQLException e) {
            throw new BusinessException(className()+".updateBalance: "+e.getMessage());
        }
        return account;
    }


    // DELETE
    @Override
    public void deleteAccount(Account account) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql =
                    "DELETE FROM shivacorp_schema.accounts WHERE id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account.getId());
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount == 0)
                throw new BusinessException(
                        className()+".deleteAccount: Failed: delete account id="+account.getId()
                );
        } catch (SQLException e) {
            throw new BusinessException(className()+".deleteAccount: "+e.getMessage());
        }
    }

        // Utility methods
    @Override
    public boolean userExists(User user) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql = "SELECT username FROM shivacorp_schema.users WHERE username = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public boolean hasAccount(User user) throws BusinessException {
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql = "SELECT userid FROM shivacorp_schema.accounts WHERE userid = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private String className() {
        String c = this.getClass().getName();
        return c.substring(c.lastIndexOf('.')+1);
    }
}
