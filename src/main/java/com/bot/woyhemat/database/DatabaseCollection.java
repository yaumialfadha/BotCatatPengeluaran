package com.bot.woyhemat.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Singleton Class untuk menyimpan repository
 */
@Component
public class DatabaseCollection {
    @Autowired
    DebtRepository repoDebt;

    @Autowired
    ExpenditureRepository repoExpenditure;

    @Autowired
    UserRepository repoUser;


    public DebtRepository getRepoDebtInstance() {
        return repoDebt;
    }

    public ExpenditureRepository getRepoExpenditureInstance() {
        return repoExpenditure;
    }

    public UserRepository getRepoUserInstance() {
        return repoUser;
    }
}
