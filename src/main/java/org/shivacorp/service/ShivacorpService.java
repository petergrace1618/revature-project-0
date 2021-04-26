package org.shivacorp.service;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.User;

public interface ShivacorpService {
    public User getUserByUsername(String username) throws BusinessException;
}
