package org.shivacorp.ui;

import org.shivacorp.exception.BusinessException;
import org.shivacorp.model.Account;
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
            List<Account> accountList = shivacorpService.getAccountsByStatus(Account.StatusType.PENDING);
            if (accountList.isEmpty()) {
                log.info("No pending accounts");
                return;
            }
            // choose pending account
            Menu pendingAccountsSubmenu = new PendingAccountsSubmenu(accountList);
            Account accountToUpdate = (Account) pendingAccountsSubmenu.getSelection();
            if (accountToUpdate == null) return;

            // choose account status
            Menu accountStatusSubmenu = new AccountStatusSubmenu(Account.StatusType.values());
            Account.StatusType status = (Account.StatusType) accountStatusSubmenu.getSelection();
            if (status == null || status == Account.StatusType.PENDING) return;

            // Update account
            accountToUpdate = shivacorpService.updateAccountStatus(accountToUpdate, status);
            log.info("Account status updated");
            log.info(accountToUpdate);
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    public void viewAccounts() {
        displaySubmenu();
        try {
            List<Account> accounts = shivacorpService.getAccounts();
            if (accounts.isEmpty()) {
                log.info("No existing accounts");
                return;
            }
            for (Account account: accounts) {
                log.info(account);
            }
        } catch (BusinessException e) {
            log.info(e.getMessage());
        }
    }

    public void viewTransactions() {
//        displaySubmenu();
        serviceUnavailable();
    }

    private void logout() {
        log.info("Logging out");
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
                log.info("Cancelled");
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
