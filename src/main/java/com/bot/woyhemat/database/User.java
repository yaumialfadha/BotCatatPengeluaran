package com.bot.woyhemat.database;

import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Unique
    private String username;
    private int target;
    private int totalPengeluaran;

    protected User() {
    }

    public User(String username, int target, int totalPengeluaran) {
        this.username = username;
        this.target = target;
        this.totalPengeluaran = totalPengeluaran;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, username='%s', target='%d', totalPengeluaran=%d]",
                id, username, target, totalPengeluaran);
    }

    public void tambahPengeluaranKeTotal(int amount) {
        this.totalPengeluaran = this.totalPengeluaran + amount;
    }

    public String getUsername() {
        return username;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTotalPengeluaran(){return this.totalPengeluaran ;}
}
