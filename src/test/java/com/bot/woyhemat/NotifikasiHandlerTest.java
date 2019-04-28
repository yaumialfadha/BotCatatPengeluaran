package com.bot.woyhemat;

import com.bot.woyhemat.database.Expenditure;
import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.User;
import com.bot.woyhemat.database.UserRepository;
import com.bot.woyhemat.handler.LaporanHandler;
import com.bot.woyhemat.handler.NotifikasiHandler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;



import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;


@RunWith(SpringRunner.class)
@SpringBootTest

public class NotifikasiHandlerTest {

    NotifikasiHandler notifikasiHandler;
    String userIdTemp;
    String userIdTemp1;

    @Autowired
    UserRepository repoU;

    @Autowired
    ExpenditureRepository repoE;

    @Before
    public void setUp() {
        notifikasiHandler = new NotifikasiHandler();
        userIdTemp = "hehehe";
        userIdTemp1 = "hehehe1";

    }
//    @Test
//    public void contextLoads() {
//    }


    @Test
    public void testkondisiTargetisFalse(){
        repoU.save(new User(userIdTemp, 30000, 0));
        boolean reply = notifikasiHandler.kondisiTarget(userIdTemp, repoU, repoE);
        assertEquals(reply,false );
    }

    @Test
    public void testkondisiTargetisTrue(){
        User user = new User(userIdTemp1, 30, 0);
        repoU.save(user);
        repoE.save(new Expenditure("Makanan", "test",
                Timestamp.valueOf("2019-05-15 12:12:12"),
                1000, user));
        boolean reply = notifikasiHandler.kondisiTarget(userIdTemp1, repoU, repoE);
        assertEquals(reply, true);
    }
    @Test
    public void testtargetLebih(){
        String reply = notifikasiHandler.targetLebih();
        assertEquals(reply, "Pengeluaran sudah melebihi target");
    }
}
