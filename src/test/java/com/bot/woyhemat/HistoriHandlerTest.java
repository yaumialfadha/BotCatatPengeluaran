package com.bot.woyhemat;

import java.sql.Timestamp;

import com.bot.woyhemat.database.Debt;
import com.bot.woyhemat.database.DebtRepository;
import com.bot.woyhemat.database.Expenditure;
import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.User;
import com.bot.woyhemat.database.UserRepository;
import com.bot.woyhemat.handler.HistoriHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest

public class HistoriHandlerTest {
    HistoriHandler historiHandler = new HistoriHandler();
    String userId = "abc";
    private static boolean isDone = false;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    ExpenditureRepository expenditureRepository;

    @Autowired
    DebtRepository debtRepository;

    
    @Before
    public void setUp() {
        if (isDone) {
            return;
        }
        userRepository.save(new User(userId, 30000, 0));

        isDone = true;
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testBelumTerdaftar() {
        String reply = historiHandler.getHistoriPengeluaran("ccc",
                                                            userRepository,
                                                            expenditureRepository,
                                                            debtRepository);
        assertEquals(reply, "Maaf, anda belum terdaftar");
    }

    @Test
    public void  testBelumPunyaPengeluaran() {
        userRepository.save(new User("ccc", 30000, 0));
        String reply = historiHandler.getHistoriPengeluaran("ccc",
                                                            userRepository,
                                                            expenditureRepository,
                                                            debtRepository);
        assertEquals(reply, "Total Pengeluaran: \n" +
                            "Anda belum memiliki pengeluaran\n\n" +
                            "Total Utang: 0");
    }

    @Test
    public void testPunyaPengeluaran() {
        User user = userRepository.findByUsername(userId);
        expenditureRepository.save(new Expenditure("Makanan", "test",
                                                    Timestamp.valueOf("2019-05-15 12:12:12"),
                                                    1000, user));
        String reply = historiHandler.getHistoriPengeluaran(userId,
                                                            userRepository,
                                                            expenditureRepository,
                                                            debtRepository);
        assertThat(reply, containsString("Pengeluaran bulan ke-5: 1000"));
    }

    @Test
    public void testPunyaPengeluaranBedaBulan() {
        User user = userRepository.findByUsername(userId);
        expenditureRepository.save(new Expenditure("kebutuhan", "teszt",
                                                    Timestamp.valueOf("2019-05-18 10:10:10"),
                                                    2000, user));
        expenditureRepository.save(new Expenditure("Hiburan", "tesxt",
                                                    Timestamp.valueOf("2019-06-15 01:02:03"),
                                                    3000, user));
        String reply = historiHandler.getHistoriPengeluaran(userId,
                                                            userRepository,
                                                            expenditureRepository,
                                                            debtRepository);
        assertThat(reply, containsString("Pengeluaran bulan ke-5: 3000\n" + 
                                         "Pengeluaran bulan ke-6: 3000"));
    }

    @Test
    public void testPunyaUtang() {
        User user = userRepository.findByUsername(userId);
        debtRepository.save(new Debt(5000, Timestamp.valueOf("2019-06-15 01:02:03"), user, "Tes"));
        debtRepository.save(new Debt(500, Timestamp.valueOf("2019-01-25 01:02:03"), user, "Test"));
        String reply = historiHandler.getHistoriPengeluaran(userId,
                                                            userRepository,
                                                            expenditureRepository,
                                                            debtRepository);
        assertThat(reply, containsString("Total Utang: 5500"));
    }
}