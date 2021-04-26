package org.shivacorp.dao.impl;

import org.shivacorp.dao.ShivacorpDAO;
import org.shivacorp.dao.dbutil.PostgreSQLConnection;
import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShivacorpDAOImpl implements ShivacorpDAO {
    @Override
    public User getUserByUsername(String username) throws BusinessException {
        User user = null;
        try(Connection connection = PostgreSQLConnection.getConnection()) {
            String sql = "SELECT id, fullname, address, phone, usertype, username, pwd"+
                    "FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setAddress(resultSet.getString("address"));
                user.setPhone(resultSet.getString("phone"));
                user.setUsertype(User.Usertype.CUSTOMER);
                user.setUsername(resultSet.getString("username"));
                user.setPwd(resultSet.getString("pwd"));
            } else {
                throw new BusinessException("Username: '"+username+"' not found.");
            }
        } catch (SQLException e) {
            throw new BusinessException("Internal error: "+e.getMessage());
        }
        return user;
    }
}
