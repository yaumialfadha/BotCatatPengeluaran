package com.bot.woyhemat.handler;

import com.bot.woyhemat.database.Debt;
import com.bot.woyhemat.database.DebtRepository;
import com.bot.woyhemat.database.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class Handler yang menangani semua yang berhubungan dengan utang seperti
 * melihat semua utang dan menambahkan utang user
 *
 * @author Kevin Raikhan Zain
 */
public class UtangHandler {

    /**
     * Menambahkan objek Debt ke DebtRepository
     *
     * @param amount jumlah utang
     * @param period objek Date yang menunjukan kapan utang jatuh tempo
     * @param user   objek user yang berhutang
     * @param repo   repository objek yang di pass dari Controller
     */
    public void tambahUtang(int amount, Date period, User user, String keterangan, DebtRepository repo) {
        Debt utang = new Debt(amount, period, user, keterangan);
        repo.save(utang);
    }


    public void hapusUtang(Debt debt, DebtRepository repo) {
        repo.delete(debt);
    }

    public ArrayList<Debt> getUtangUserArray(String userId, DebtRepository repo) {

        ArrayList<Debt> debts = new ArrayList<>();
        int counter = 1;
        for (Debt debt : repo.findAll()) {
            System.out.println("Loop getUtangUser"); // LOG

            if (debt.getUser().getUsername().equals(userId)) {
                debts.add(debt);
                counter++;
            }

        }
        return debts;
    }


}