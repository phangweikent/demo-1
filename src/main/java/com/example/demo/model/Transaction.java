package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "customerId")
    private long customerId;

    @Column(name = "name")
    private String name;
    @Column(name = "amount")
    private String amount;

    public Transaction() {

    }

    public Transaction(long customerId, String name, String amount) {
        this.customerId = customerId;
        this.name = name;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", customerId =" + customerId + " name=" + name + ", amount=" + amount + "]";
    }
}
