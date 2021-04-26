package org.shivacorp.model;

public class Account {
    int acctno;
    int userid;
    double balance;
    StatusType status;

    public enum StatusType {
        PENDING, APPROVED, DENIED
    }
}
