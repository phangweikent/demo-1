package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name")
    private String name;
    @Column(name = "ic")
    private String ic;

    public Customer() {

    }

    public Customer(String name, String ic) {
        this.name = name;
        this.ic = ic;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", name=" + name + ", ic=" + ic + "]";
    }

}
