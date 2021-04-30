package org.shivacorp.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction {
    int id;
    Timestamp timestamp;
    TransactionType transactionType;
    int userId;
    int accountId;
    int otherAccountId;
    double amount;

    public enum TransactionType {
        USER_CREATED, ACCOUNT_CREATED, ACCOUNT_APPROVED, ACCOUNT_DENIED,
        DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT, TRANSFER_CREDIT
    }

    public static class Builder {
        int id;
        Timestamp timestamp;
        TransactionType transactionType;
        int userId;
        int accountId;
        int otherAccountId;
        double amount;

        public Builder() {}

        public Builder withId(int id) {
            this.id = id;
            return this;
        }
        public Builder withTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public Builder withTimestamp() {
            withTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            return this;
        }
        public Builder withTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }
        public Builder withUserId(int userId) {
            this.userId = userId;
            return this;
        }
        public Builder withAccountId(int accountId) {
            this.accountId = accountId;
            return this;
        }
        public Builder withOtherAccountId(int otherAccountId) {
            this.otherAccountId = otherAccountId;
            return this;
        }
        public Builder withAmount(double amount) {
            this.amount = amount;
            return this;
        }
        public Transaction build() {
            Transaction transaction = new Transaction();
            transaction.timestamp = this.timestamp;
            transaction.transactionType = this.transactionType;
            transaction.userId = this.userId;
            transaction.accountId = this.accountId;
            transaction.amount = this.amount;
            return transaction;
        }
    }

    private Transaction() {
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public int getOtherAccountId() {
        return otherAccountId;
    }

    public void setOtherAccountId(int otherAccountId) {
        this.otherAccountId = otherAccountId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", transactionType=" + transactionType +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", otherAccountId=" + otherAccountId +
                ", amount=" + amount +
                '}';
    }
}
