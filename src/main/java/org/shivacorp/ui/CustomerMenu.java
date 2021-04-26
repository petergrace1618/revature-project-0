package org.shivacorp.ui;

import org.apache.log4j.Logger;

public class CustomerMenu extends Menu {
    public CustomerMenu() {
        title = "\nCustomer menu";
        menuItems = new String[] {
                "Apply for new account",
                "View account balance",
                "Withdraw funds",
                "Deposit funds",
                "Transfer funds",
                "Logout"
        };
        log = Logger.getLogger(CustomerMenu.class);
    }

    @Override
    public Menu processInput() {
        Menu nextMenu = this;
        log.info(MENU_PROMPT);
        menuChoice = Stdin.nextInt();
        switch (menuChoice) {
            case 1:
                applyForNewAccount();
                break;
            case 2:
                viewAccountBalance();
                break;
            case 3:
                withdraw();
                break;
            case 4:
                deposit();
                break;
            case 5:
                transfer();
                break;
            case 6:
                logout();
                nextMenu = new MainMenu();
                break;
            default:
                log.info(INVALID_MENU_CHOICE);
                break;
        }
        return nextMenu;
    }

    private void applyForNewAccount() {
        displaySubmenu();
    }

    private void viewAccountBalance() {
        displaySubmenu();
    }

    private void withdraw() {
        displaySubmenu();
    }

    private void deposit() {
        displaySubmenu();
    }

    private void transfer() {
        displaySubmenu();
    }

    private void logout() {
        log.info("Logging out");
    }
}
