package org.shivacorp.model;

public class User {
    int id;
    String username;
    String password;
    Usertype usertype;
    int accountId;
    Account account;

    public User() { }

    public User(String username, String password, Usertype usertype, Account account) {
        this.username = username;
        this.password = password;
        this.usertype = usertype;
        this.account = account;
    }

    public enum Usertype {
        CUSTOMER, EMPLOYEE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public Usertype getUsertype() {
        return usertype;
    }

    public void setUsertype(Usertype usertype) {
        this.usertype = usertype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountId() { return accountId; }

    public void setAccountId(int accountId) { this.accountId = accountId; }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", usertype=" + usertype +
                ", account=" + account +
                '}';
    }
}
