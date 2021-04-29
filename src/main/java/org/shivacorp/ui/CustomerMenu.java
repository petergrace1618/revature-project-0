package org.shivacorp.ui;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.User;

public class CustomerMenu extends Menu {
    User currentUser;
    public CustomerMenu(User user) {
        title = "Customer menu";
        menuItems = new String[] {
                "Apply for new account",
                "View account balance",
                "Withdraw funds",
                "Deposit funds",
                "Transfer funds",
                "Logout"
        };
        numMenuItems = menuItems.length;
        currentUser = user;
    }

    @Override
    public Menu getSelection() {
        display();
        displayPrompt();
        Menu nextMenu = this;
        selection = Stdin.getInt(1, numMenuItems);
        switch (selection) {
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
                break;
        }
        return nextMenu;
    }

    public void displayPrompt() {
        if (currentUser != null) {
            log.info("[" + currentUser.getUsername() + "] " + menuPrompt);
        } else {
            super.displayPrompt();
        }
    }

    private void applyForNewAccount() {
        displaySubmenu();
        log.info("Please enter a starting balance:");
        double balance = Stdin.getDouble();
        Account account = new Account(currentUser, balance, Account.StatusType.PENDING);
        try {
            shivacorpService.addAccount(account);
            log.info("Your new account is pending approval.");
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    private void viewAccountBalance() {
        displaySubmenu();
        try {
            Account account = shivacorpService.getAccountByUser(currentUser);
            log.info(account);
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    private void withdraw() {
        displaySubmenu();
        log.info("Enter withdrawal amount (0 to cancel):");
        double amount = Stdin.getDouble();
        if (amount == 0)
            return;
        try {
            Account account = shivacorpService.withdraw(currentUser, amount);
            log.info(account);
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    private void deposit() {
        displaySubmenu();
        log.info("Enter deposit amount (0 to cancel):");
        double amount = Stdin.getDouble();
        if (amount == 0)
            return;
//        Account account = shivacorpService;
    }

    private void transfer() {
        serviceUnavailable();
//        displaySubmenu();
    }

    private void logout() {
        log.info("Logging out");
    }
}
