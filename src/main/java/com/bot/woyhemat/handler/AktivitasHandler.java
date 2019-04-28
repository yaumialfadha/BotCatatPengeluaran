package com.bot.woyhemat.handler;

import com.bot.woyhemat.database.Expenditure;
import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.User;

import java.sql.Timestamp;
import java.util.TimeZone;

public class AktivitasHandler {

    public void tambahPengeluaran(String katergori, int jumlah, String deskripsi, User thisUser, ExpenditureRepository expenses) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Expenditure pengeluaran = new Expenditure(katergori, deskripsi, timestamp, jumlah, thisUser);
        expenses.save(pengeluaran);
        thisUser.tambahPengeluaranKeTotal(jumlah);
    }
}
