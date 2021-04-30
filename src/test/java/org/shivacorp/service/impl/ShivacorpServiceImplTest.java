package org.shivacorp.service.impl;

import org.junit.jupiter.api.Test;
import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.User;
import org.shivacorp.service.ShivacorpService;

import static org.junit.jupiter.api.Assertions.*;

class ShivacorpServiceImplTest {
    ShivacorpService shivacorpService = new ShivacorpServiceImpl();

    @Test
    void loginAdminSuccess() {
        User expectedUser = new User("admin", "admin","admin", User.Usertype.EMPLOYEE);
        expectedUser.setId(1);
        User actualUser = null;
        try {
            actualUser = shivacorpService.login(new User("admin", "admin"));
        } catch (BusinessException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void loginAdminBadPassword() {
        assertThrows(BusinessException.class,
                ()->shivacorpService.login(new User("admin", "wrongpassword")));
    }

    @Test
    void loginBadUsername() {
        assertThrows(BusinessException.class,
                ()->shivacorpService.login(new User("badusername", ".")));
    }

    @Test
    void withdrawNSF() { // attempt to withdraw with insufficient funds
        assertThrows(BusinessException.class,
                ()->shivacorpService.withdraw(new User("zerobalance", "."),1));
    }

    @Test
    void transferNSF() {
        User user = new User();
        user.setId(5); // zero balance account
        assertThrows(BusinessException.class,
                ()->shivacorpService.transfer(user, 111111,1.0));
    }

}