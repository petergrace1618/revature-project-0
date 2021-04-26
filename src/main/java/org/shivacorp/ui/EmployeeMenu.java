package org.shivacorp.ui;

import org.apache.log4j.Logger;

public class EmployeeMenu extends Menu {
    public EmployeeMenu() {
        title = "\nEmployee Menu";
        menuItems = new String[] {
                "Approve/reject account",
                "View customer accounts",
                "View log of all transactions",
                "Logout"
        };
        log = Logger.getLogger(EmployeeMenu.class);
    }

    @Override
    public Menu processInput() {
        Menu nextMenu = this;
        log.info(MENU_PROMPT);
        menuChoice = Stdin.nextInt();
        switch (menuChoice) {
            case 1:
                approveRejectAcct();
                break;
            case 2:
                viewAcct();
                break;
            case 3:
                viewTransactions();
                break;
            case 4:
                logout();
                nextMenu = new MainMenu();
                break;
            default:
                log.info(INVALID_MENU_CHOICE);
                break;
        }
        return nextMenu;
    }
    public void approveRejectAcct() {
        displaySubmenu();
    }

    public void viewAcct() {
        displaySubmenu();
    }

    public void viewTransactions() {
        displaySubmenu();
    }

    private void logout() {
        log.info("Logging out");
    }
}
