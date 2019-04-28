package com.bot.woyhemat;

import com.bot.woyhemat.database.Expenditure;
import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.User;
import com.bot.woyhemat.database.UserRepository;
import com.bot.woyhemat.handler.LaporanHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import java.sql.Timestamp;

@RunWith(SpringRunner.class)
@SpringBootTest

public class LaporanHandlerTest{
    LaporanHandler laporanHandler;

    String userIdTemp;

    @Autowired
    UserRepository repoU;

    @Autowired
    ExpenditureRepository repoE;

    @Before
    public void setUp() {
        laporanHandler = new LaporanHandler();
        userIdTemp = "asdfghjkl";

    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testShowLaporanUserBelumTerdaftar() {
        String reply = laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE);
        assertEquals(reply, "Maaf, anda belum terdaftar.");
    }

    @Test
    public void testShowLaporanBelumAdaPengeluaran() {
        repoU.save(new User(userIdTemp, 30000, 0));
        String reply = laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE);
        assertEquals(reply, "Anda belum memiliki pengeluaran bulan ini.");
    }

    @Test
    public void testShowLaporanDenganPengeluaran() {
        User thisUser = repoU.findByUsername(userIdTemp);
        repoE.save(new Expenditure("makanan", "deskripsi", new Timestamp(System.currentTimeMillis()), 2000, thisUser));

        assertThat(laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE),
                    containsString("Kategori: makanan" +
                                    "\n\t\tDeskripsi: deskripsi" +
                                    "\n\t\tJumlah: 2000")
        );  
    }

    @Test
    public void testShowLaporanDenganPengeluaranBedaHari() {
        User thisUser = repoU.findByUsername(userIdTemp);
        repoE.save(new Expenditure("hiburan", "deskripsi2", Timestamp.valueOf("2019-05-31 12:12:12"), 250, thisUser));

        assertThat(laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE),
                    containsString("Friday, 31 May 2019")
        );  

        assertThat(laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE),
                    containsString("Kategori: hiburan" +
                                    "\n\t\tDeskripsi: deskripsi2" +
                                    "\n\t\tJumlah: 250")
        );  
    }

    @Test
    public void testShowLaporanPengeluaranBedaBulan() {
        User thisUser = repoU.findByUsername(userIdTemp);
        repoE.save(new Expenditure("lainnya", "deskripsi", Timestamp.valueOf("2019-06-10 12:12:12"), 400, thisUser));

        assertThat(laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE),
                    not(containsString("Kategori: lainnya" +
                                    "\n\t\tDeskripsi: deskripsi" +
                                    "\n\t\tJumlah: 400")
        ));  
    }

    @Test
    public void testShowLaporanDenganPengeluaranMelebihiTarget() {
        User thisUser = repoU.findByUsername(userIdTemp);
        repoE.save(new Expenditure("hiburan", "deskripsi", new Timestamp(System.currentTimeMillis()), 40000, thisUser));

        assertThat(laporanHandler.showPengeluaranSebulan(userIdTemp, repoU, repoE),
                    containsString("Total pengeluaran anda bulan ini sudah melebihi target anda!")
        );
    }

}
