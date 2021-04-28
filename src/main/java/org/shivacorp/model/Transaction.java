package org.shivacorp.model;

import java.time.LocalDateTime;

public class Transaction {
    int id;
    int accountId;
    LocalDateTime datetime;
    TransactionType transactionType;
    double amount;
    int otherAcct;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT,
        TRANSFER_CREDIT, ACCT_APPROVED, ACCT_DENIED
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getOtherAcct() {
        return otherAcct;
    }

    public void setOtherAcct(int otherAcct) {
        this.otherAcct = otherAcct;
    }

    @Override
    public String toString() {
        return "Transaction {" +
                "id=" + id +
                ", accountId=" + accountId +
                ", datetime=" + datetime +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", otherAcct=" + otherAcct +
                '}';
    }
}
