package org.shivacorp.ui;

import org.apache.log4j.Logger;
import org.shivacorp.model.User;

import java.util.Locale;

public class MainMenu extends Menu {
    public MainMenu() {
        super.title = "\nMain menu";
        super.menuItems = new String[] {
                "Login",
                "Register",
                "Quit"
        };
        super.log = Logger.getLogger(MainMenu.class);
    }

    @Override
    public Menu processInput() {
        Menu nextMenu = this;
        log.info("Enter choice:");
        menuChoice = Stdin.nextInt();
        switch (menuChoice) {
            case 1:
                User.Usertype usertype = login();
                if (usertype == User.Usertype.CUSTOMER) {
                    nextMenu = new CustomerMenu();
                } else if (usertype == User.Usertype.EMPLOYEE) {
                    nextMenu = new EmployeeMenu();
                } else {
                    log.info("Unknown usertype");
                    nextMenu = null;
                }
                break;
            case 2:
                register();
                break;
            case 3:
                quit();
                nextMenu = null;
                break;
            default:
                log.info("Invalid choice");
                break;
        }
        return nextMenu;
    }

    private User.Usertype login() {
        displaySubmenu();
        log.info("Username:");
        String username = Stdin.nextString();
        log.info("Password:");
        String password = Stdin.getPassword();
        log.info("Searching for username: "+username);
        if (username.toLowerCase().charAt(0) == 'e') {
            return User.Usertype.EMPLOYEE;
        } else {
            return User.Usertype.CUSTOMER;
        }
    }

    private void register() {
        String username;
        String password1;
        String password2;
        displaySubmenu();
        do {
            log.info("Username:");
            username = Stdin.nextString();
            log.info("Password:");
            password1 = Stdin.getPassword();
            log.info("Confirm password:");
            password2 = Stdin.getPassword();

            if (!password1.equals(password2)) {
                log.info("Passwords do not match. Try again.");
            }
        } while (!password1.equals(password2));
        log.info("New user account created for username: "+username);
    }

    private void quit() {
        log.info("Thank you for using ShivaCorp Banking app");
        log.info("Goodbye");
    }
}
