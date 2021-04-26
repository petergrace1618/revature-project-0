package org.shivacorp.dao;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.User;

public interface ShivacorpDAO {
    public User getUserByUsername(String username) throws BusinessException;
}
