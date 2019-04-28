package com.bot.woyhemat.database;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String category; // TODO: ini nanti mau dibikin class activity ya ga?
    private String description;
    private Timestamp timestamp;
    private int amount;

    @ManyToOne
    private User user;

    protected Expenditure() {
    }

    public Expenditure(String category, String description, Timestamp timestamp, int amount, User user) {
        this.category = category;
        this.description = description;
        this.timestamp = timestamp;
        this.amount = amount;
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getAmount() {
        return amount;
    }
}
