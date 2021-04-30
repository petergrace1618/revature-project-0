package org.shivacorp.ui;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.User;

public class MainMenu extends Menu {
    User currentUser;
    public MainMenu() {
        title = "Main menu";
        menuItems = new String[] {
                "Login",
                "Register",
                "Quit"
        };
        numMenuItems = menuItems.length;
        currentUser = null;
    }

    @Override
    public Menu getSelection() {
        display();
        displayPrompt();
        Menu nextMenu = this;
        selection = Stdin.getInt(1, numMenuItems);
        switch (selection) {
            case 1:
                currentUser = login();
                if (currentUser == null) {
                    nextMenu = this;    // login failed, back to main menu
                } else if (currentUser.getUsertype() == User.Usertype.EMPLOYEE) {
                    nextMenu = new EmployeeMenu(currentUser);
                } else {
                    nextMenu = new CustomerMenu(currentUser);
                }
                break;
            case 2:
                currentUser = register();
                // if user registered successfully, go to Customer menu
                // else go back to main menu
                nextMenu = (currentUser != null) ? new CustomerMenu(currentUser) : this;
                break;
            case 3:
                quit();
                nextMenu = null;
                break;
            default:
                break;
        }
        return nextMenu;
    }

    private User login() {
        displaySubmenu();
        log.info("Username:");
        String username = Stdin.getString();
        log.info("Password:");
        String password = Stdin.getPassword();
        log.info("Logging in "+username);
        User user = new User(username, password);
        try {
            user = shivacorpService.login(user);
            log.info(user.getUsername()+" authenticated");
        } catch (BusinessException e) {
            log.info(e.getMessage());
            user = null;
        }
        return user;
    }

    private User register() {
        displaySubmenu();
        String username;
        String password1;
        String password2;
        String fullname;

        log.info("Username:");
        username = Stdin.getString().trim();
        log.info("Password:");
        password1 = Stdin.getPassword().trim();
        log.info("Confirm password:");
        password2 = Stdin.getPassword().trim();
        log.info("Full name:");
        fullname = Stdin.getString().trim();

        if (!password1.equals(password2)) {
            log.info("Passwords do not match. Please try again.");
            return null;
        }
        User user = new User(username, password1, fullname, User.Usertype.CUSTOMER);
        try {
            user = shivacorpService.register(user);
            log.info("New user account created for "+username);
        } catch (BusinessException e) {
            user = null;
            log.info(e.getMessage());
        }
        return user;
    }

    private void quit() {
        log.info("Thank you for using ShivaCorp Banking app");
        log.info("Goodbye");
    }
}
