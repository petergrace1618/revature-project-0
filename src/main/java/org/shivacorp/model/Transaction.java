package org.shivacorp.model;

import java.time.LocalDateTime;

public class Transaction {
    int id;
    int acctno;
    LocalDateTime datetime;
    TransactionType transactiontype;
    double amount;
    int otheracct;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT,
        TRANSFER_CREDIT, ACCT_APPROVED, ACCT_DENIED
    }
}
