package org.shivacorp.model;

import java.util.Objects;

public class User {
    int id;
    String username;
    String password;
    String fullName;
    Usertype usertype;

    public User() { }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String fullName, Usertype usertype) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.usertype = usertype;
    }

    public enum Usertype {
        CUSTOMER, EMPLOYEE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

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

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public Usertype getUsertype() {
        return usertype;
    }

    public void setUsertype(Usertype usertype) {
        this.usertype = usertype;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullname='" + fullName + '\'' +
                ", usertype=" + usertype +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(fullName, user.fullName) && usertype == user.usertype;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, fullName, usertype);
    }
}
