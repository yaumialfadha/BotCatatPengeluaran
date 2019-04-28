package com.bot.woyhemat.database;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int amount;
    private Date period;
    private String keterangan;

    @ManyToOne
    private User user;

    protected Debt() {
    }


    public Debt(int amount, Date period, User user, String keterangan) {
        this.amount = amount;
        this.period = period;
        this.user = user;
        this.keterangan = keterangan;

    }

//    public int getId() {
//        return id;
//    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public int getAmount() {
        return amount;
    }

//    public void setAmount(int amount) {
//        this.amount = amount;
//    }

    public Date getPeriod() {
        return period;
    }

//    public void setPeriod(Date period) {
//        this.period = period;
//    }

    public User getUser() {
        return user;
    }

//    public void setUser(User user) {
//        this.user = user;
//    }

    public String getKeterangan() {
        return keterangan;
    }

//    public void setKeterangan(String keterangan) {
//        this.keterangan = keterangan;
//    }
}
