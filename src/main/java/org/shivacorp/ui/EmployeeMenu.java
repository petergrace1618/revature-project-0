package org.shivacorp.ui;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
import org.shivacorp.model.Transaction;
import org.shivacorp.model.User;
import java.util.ArrayList;
import java.util.List;

public class EmployeeMenu extends Menu {
    User currentUser;
    public EmployeeMenu(User user) {
        title = "Employee Menu";
        menuItems = new String[] {
                "Approve/reject account",
                "View customer accounts",
                "View users",
                "View log of all transactions",
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
                approveOrRejectAccount();
                break;
            case 2:
                viewAccounts();
                break;
            case 3:
                viewUsers();
                break;
            case 4:
                viewTransactions();
                break;
            case 5:
                logout();
                nextMenu = new MainMenu();
                break;
            default:
                log.info(INVALID_MENU_CHOICE);
                break;
        }
        return nextMenu;
    }

    @Override
    public void displayPrompt() {
        if (currentUser != null) {
            log.info("[" + currentUser.getUsername() + "] " + menuPrompt);
        } else {
            super.displayPrompt();
        }
    }

    public void approveOrRejectAccount() {
        displaySubmenu();
        try {
            List<Account> accounts = shivacorpService.getAccountsByStatus(Account.StatusType.PENDING);

            // choose pending account
            Menu pendingAccountsSubmenu = new PendingAccountsSubmenu(accounts);
            Account account = (Account) pendingAccountsSubmenu.getSelection();

            // user cancelled
            if (account == null)
                return;

            // choose account status
            Menu accountStatusSubmenu = new AccountStatusSubmenu(Account.StatusType.values());
            Account.StatusType status = (Account.StatusType) accountStatusSubmenu.getSelection();

            // user cancelled
            if (status == null)
                return;

            // Update account and log the change
            account = shivacorpService.updateStatus(account, status);
            log.info("Account "+account.getStatus());
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    public void viewAccounts() {
        displaySubmenu();
        try {
            List<Account> accounts = shivacorpService.getAccounts();
            for (Account account: accounts) {
                log.info(account);
            }
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    public void viewUsers() {
        displaySubmenu();
        try {
            List<Account> accounts = shivacorpService.getUsers();
            for (Account account: accounts) {
                log.info(account);
            }
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    public void viewTransactions() {
        displaySubmenu();
        try {
            List<Transaction> transactions = shivacorpService.viewTransactions();
            for (Transaction transaction: transactions)
                log.info(transaction);
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    private void logout() {
        log.info("Logging out "+currentUser.getUsername());
    }

    private class PendingAccountsSubmenu extends Menu {
        private List<Account> accounts;

        PendingAccountsSubmenu(List<Account> accountList) {
            this.accounts = accountList;
            title = accounts.size()+" account(s) pending:";
            menuItems = toStringArray(accounts);
            numMenuItems = accounts.size();
            menuPrompt = "Choose account:";
            showCancelOption = true;
        }

        @Override
        public Account getSelection() {
            display();
            super.displayPrompt();
            selection = Stdin.getInt(numMenuItems);
            if (selection == 0) {
                log.info("Cancelled");
                return null;
            }
            return accounts.get(selection-1);
        }

        String[] toStringArray(List<Account> accounts) {
            List<String> s = new ArrayList<>();
            for (Account account: accounts) {
                s.add(account.toString());
            }
            return s.toArray(new String[0]);
        }
    }

    private class AccountStatusSubmenu extends Menu {
        Account.StatusType[] statusTypes;
        AccountStatusSubmenu(Account.StatusType[] values) {
            statusTypes = values;
            menuItems = toStringArray(statusTypes);
            numMenuItems = menuItems.length;
            menuPrompt = "Choose status:";
            showCancelOption = true;
        }

        @Override
        public Account.StatusType getSelection() {
            display();
            super.displayPrompt();
            selection = Stdin.getInt(numMenuItems);
            if (selection == 0) {
                return null;
            }
            return statusTypes[selection-1];
        }

        String[] toStringArray(Account.StatusType[] statusTypes) {
            List<String> s = new ArrayList<>();
            for (Account.StatusType status: statusTypes) {
                s.add(status.name());
            }
            return s.toArray(new String[0]);
        }
    }
}
