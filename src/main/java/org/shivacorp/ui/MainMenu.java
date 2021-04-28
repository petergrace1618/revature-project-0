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
                login();
                if (currentUser != null) {
                    if (currentUser.getUsertype() == User.Usertype.EMPLOYEE) {
                        nextMenu = new EmployeeMenu(currentUser);
                    } else {
                        nextMenu = new CustomerMenu(currentUser);
                    }
                }
                break;
            case 2:
                register();
                if (currentUser != null)
                    nextMenu = new CustomerMenu(currentUser);
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

    private void login() {
        displaySubmenu();
        log.info("Username:");
        String username = Stdin.getString();
        log.info("Password:");
        String password = Stdin.getPassword();
        log.info("Looking up "+username);
        User user = null;
        try {
            user = shivacorpService.getUserByUsername(username);
            if (user != null) {
                log.info("Found "+user.getUsername());
                if (password.equals(user.getPassword())) {
                    log.info(user.getUsername()+" authenticated");
                } else {
                    log.info("Authentication failed");
                    user = null;
                }
            } else {
                log.info("Username '"+username+"' does not exist");
            }
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
        currentUser = user;
    }

    private void register() {
        displaySubmenu();
        String username;
        String password1;
        String password2;
        User user = null;
        log.info("Username:");
        username = Stdin.getString();
        log.info("Password:");
        password1 = Stdin.getPassword();
        log.info("Confirm password:");
        password2 = Stdin.getPassword();

        if (username.isEmpty() || password1.isEmpty()) {
            log.info("username and password cannot be empty. Please try again.");
            return;
        } else if (!password1.equals(password2)) {
            log.info("Passwords do not match. Please try again.");
            return;
        }
        user = new User(username, password1, User.Usertype.CUSTOMER, null);
        try {
            user = shivacorpService.addUser(user);
            log.info("New user account created for "+username);
        } catch (BusinessException e) {
            user = null;
            log.info(e.getMessage());
        }
        currentUser = user;
    }

    private void quit() {
        log.info("Thank you for using ShivaCorp Banking app");
        log.info("Goodbye");
    }
}
