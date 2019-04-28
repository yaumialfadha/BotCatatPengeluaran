package com.bot.woyhemat.database;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenditureRepository extends CrudRepository<Expenditure, Integer> {
    List<Expenditure> findByUser(User User);
}
