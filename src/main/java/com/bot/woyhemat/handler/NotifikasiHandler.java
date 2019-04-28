package com.bot.woyhemat.handler;

import com.bot.woyhemat.database.Expenditure;
import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.User;
import com.bot.woyhemat.database.UserRepository;

import java.util.List;

public class NotifikasiHandler {

    public String targetLebih() {
        return "Pengeluaran sudah melebihi target";
    }

    public boolean kondisiTarget(String userId, UserRepository users, ExpenditureRepository expenses) {
        User thisUser = users.findByUsername(userId);
        int thisTarget = thisUser.getTarget();

        List<Expenditure> thisExpenses = expenses.findByUser(thisUser);

        int total = 0;
        for (int i = 0; i < thisExpenses.size(); i++) {
            total += thisExpenses.get(i).getAmount();
        }
        if(total > thisTarget){
            return true;
        }
        return false;


    }

}

