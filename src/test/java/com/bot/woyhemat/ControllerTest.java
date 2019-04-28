package com.bot.woyhemat;

import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.UserRepository;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;



@RunWith(SpringRunner.class)
@SpringBootTest

public class ControllerTest {


    // Buat Check println
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    String userIdTemp = "asdf";

    Source mockSourceSudahDaftar = new Source() {
        @Override
        public String getUserId() {
            return "123";
        }

        @Override
        public String getSenderId() {
            return "456";
        }
    };

    Source mockSourceBelumDaftar = new Source() {
        @Override
        public String getUserId() {
            return "999";
        }

        @Override
        public String getSenderId() {
            return "789";
        }
    };

    Source mockSourceLihatUtang = new Source() {
        @Override
        public String getUserId() {
            return "111";
        }

        @Override
        public String getSenderId() {
            return "789";
        }
    };


    Source mockSourceHapusUtang1 = new Source() {
        @Override
        public String getUserId() {
            return "233";
        }

        @Override
        public String getSenderId() {

            return "789";
        }
    };

    Source mockSourceLaporan = new Source() {
        @Override
        public String getUserId() {
            return "435";

        }

        @Override
        public String getSenderId() {

            return "789";
        }
    };

    Source mockSourceHapusUtang2 = new Source() {
        @Override
        public String getUserId() {
            return "322";
        }

        @Override
        public String getSenderId() {
            return "789";

        }
    };

    @Autowired
    UserRepository repo;

    @Autowired
    ExpenditureRepository repoE;

    @Autowired
    LineMessagingClient lineMessagingClient;

    @Autowired
    Controller controller;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }


    @Test
    public void testTargetCannotBeNegative() {
        String reply = controller.newUser("qwer", -200);
        assertEquals(reply, "Maaf target tidak boleh kurang dari 0");

    }


    @Test
    public void testSaveUser() {
        controller.newUser(userIdTemp, 1000);
        assertNotNull(repo.findAll());
    }

    @Test
    public void testSaveUserAlreadyRegistered() {
        String reply = controller.newUser(userIdTemp, 1000);
        String reply2 = controller.newUser(userIdTemp, 1000);
        assertEquals(reply2, "Maaf Anda sudah terdaftar");
    }

    @Test
    public void testTambahPengeluaran() {
        controller.tambahAktivitas(userIdTemp, "makanan", 1000, "ayam goreng");
        assertNotNull(repoE.findAll());
    }

    @Test
    public void testTambahPengeluaranTargetUdahMelebihiTarget() {
        controller.newUser("dany", 10000);
        String reply = controller.tambahAktivitas("dany", "makanan", 20000, "ayam goreng");
        assertEquals(reply, "Pengeluaran berhasil di tambah. \n WARNING: total pengeluaran sudah melebihi target");
    }

    @Test
    public void testInfoTambah() {
        String reply = controller.infoTambah();
        assertEquals(reply, "Cara menggunakan fitur tambah:\n" +
                "*kategogori* *jumlahuang* *deskripsi");
    }


    @Test
    public void testInfoUtang() {
        String reply = controller.infoUtang();
        assertEquals(reply, "Cara menggunakan fitur Utang:\n" +
                "*kategogori* *jumlahuang* *deskripsi");
    }

    @Test
    public void testTambahPengeluaranWithoutUser() {
        String reply = controller.tambahAktivitas("akndn", "makanan", 10000, "ayam goreng");
        assertEquals(reply, "Maaf Anda belom terdaftar");
    }

    @Test
    public void testJumlahPengeluaranCannotBeNegative() {
        controller.newUser("aaa", 100000);
        String reply = controller.tambahAktivitas("aaa", "hiburan", -200, "nonton endgame");
        assertEquals(reply, "Maaf jumlah pengeluaran tidak boleh kurang dari 0");
    }

    @Test
    public void testKategoriInfo() {
        String reply = controller.kategori();
        assertEquals(reply, "Pilih satu kategori: \n - Makanan/Minuman \n - Hiburan \n - Lainnya  \n Seterusnya ketik *kategori yang dipilih* *total pengeluaran* *deskripsi*");
    }

    @Test
    public void testSetTarget() {
        controller.newUser("cersei", 3000);
        String reply = controller.setTarget("cersei", 1000);
        assertEquals(reply, "Anda telah menentukan target pengeluaran sebesar 1000");
    }


    // @Test
    // public void addObserver() throws Exception{

    @Test
    public void testInfo() {
        String reply = controller.info();
        assertEquals(reply, "Berikut fitur-fitur yang terdapat pada WoyHemat! :\n" +
                "- info\n" +
                "- target [jumlah]\n" +
                "- tambah pengeluaran\n" +
                "- laporan\n" +
                "- daftar [target]\n" +
                "- utang [jumlah utang] [berapa hari] [keterangan]\n"+
                "- hapusutang [nomor utang]\n"+
                "- lihatutang\n" +
                "- histori");
    }


    @Test
    public void testSalah() {
        String hasil = new Controller().salah();

        assertEquals("Fitur tidak tersedia", hasil);
    }

    @Test
    public void testUtangInputVerifierTrue() {
        Boolean hasil = controller.utangInputVerifier(new String[]{"utang", "500", "4"});
        assertTrue(hasil);
    }

    @Test
    public void testUtangInputVerifierFalseNegatif() {
        Boolean hasil = controller.utangInputVerifier(new String[]{"utang", "-500", "-4"});
        assertFalse(hasil);
    }

    @Test
    public void testUtangInputVerifierFalseChar() {
        Boolean hasil = controller.utangInputVerifier(new String[]{"utang", "5a00", "4"});
        assertFalse(hasil);
    }

    @Test
    public void testEventInfo() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "info"), Instant.now()));
        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");
        String expected = "Berikut fitur-fitur yang terdapat pada WoyHemat! :" +
                "- info" +
                "- target [jumlah]" +
                "- tambah pengeluaran" +
                "- laporan" +
                "- daftar [target]" +
                "- utang [jumlah utang] [berapa hari] [keterangan]"+
                "- hapusutang [nomor utang]"+
                "- lihatutang" +
                "- histori";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);
    }

    @Test
    public void testEventCommandSalah() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "salahwoy"), Instant.now()));
        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");
        String expected = "Fitur tidak tersedia";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);


    }

    @Test
    public void testEventUtangSalah() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "utang -1 -2"), Instant.now()));
        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "Maaf, perintah tidak dikenali. Mungkin maksud anda  utang <jumlah utang> " +
                "<berapa hari hingga deadline> <deskripsi>. Tulis \"info\" untuk informasi lebih lanjut";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);


    }

    @Test
    public void testEventUtangBelumDaftar() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceBelumDaftar,
                new TextMessageContent("123", "utang 1 2"), Instant.now()));
        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "Maaf, anda belum terdaftar";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);


    }

    @Test
    public void testEventUtangSudahDaftarBerhasil() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "daftar 25000"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "utang 10 3"), Instant.now()));
        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "Berhasil menambahkan utang sebesar 10, jatuh tempo dalam 3 hari, tulis" +
                " 'lihatutang' tanpa tanda kutip untuk melihat semua utang";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);

    }

    @Test
    public void testEventUtangSudahDaftarSalah() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "daftar 25000"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "utang -10 -3"), Instant.now()));
        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "Maaf, perintah tidak dikenali. Mungkin maksud anda  utang <jumlah utang> " +
                "<berapa hari hingga deadline> <deskripsi>. Tulis \"info\" untuk informasi lebih lanjut";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);


    }

    @Test
    public void testLihatUtangSudahDaftar() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceLihatUtang,
                new TextMessageContent("123", "daftar 25000"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceLihatUtang,
                new TextMessageContent("123", "utang 10 3"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceLihatUtang,
                new TextMessageContent("123", "lihatutang"), Instant.now()));

        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;
        String compare = compareList[lastElement - 1].trim().replace("\n", "");
        String comparePreDate = compare.substring(0, 37);


        String expectedPreDate = "[Utang] : 1) Jumlah: 10 Jatuh Tempo: ";
//        String a = compare.substring(0, 30);

        System.err.println(outContent.toString());
        assertEquals(comparePreDate, expectedPreDate);


    }

    @Test
    public void testDateToString() {
        String hasil = controller.dateToString(new Date(1));
        System.out.println(hasil);
        assertEquals("01-01-1970", hasil);
    }

    @Test
    public void testCobaHapusUtangBerhasil() {
        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang1,
                new TextMessageContent("123", "daftar 25000"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang1,
                new TextMessageContent("123", "utang 10 3 satu"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang1,
                new TextMessageContent("123", "utang 20 2 dua"), Instant.now()));

        controller.hapusUtang(1, "233");
        String[] compareList = outContent.toString().split("&");
        int lastElement = compareList.length - 1;


        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "dua";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);
    }

    @Test
    public void testCobaHapusUtangWithMessageBerhasil() {

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang2,
                new TextMessageContent("123", "daftar 25000"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang2,
                new TextMessageContent("123", "utang 10 3 satu"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang2,
                new TextMessageContent("123", "utang 20 2 dua"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang2,
                new TextMessageContent("123", "hapusutang 1"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceHapusUtang2,
                new TextMessageContent("123", "lihatutang"), Instant.now()));


        String[] compareList = outContent.toString().split("#");
        int lastElement = compareList.length - 1;


        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "[Utang] : 1) Jumlah: 20 Jatuh Tempo: 16-05-2019  Keterangan: dua";

        System.err.println(outContent.toString());
        assertEquals(expected.substring(0,37), compare.substring(0, 37));
    }

    @Test
    public void testCobaHapusUtangWithMessageSalahNoUtang() {
        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "daftar 25000"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "utang 10 3 satu"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "utang 20 2 dua"), Instant.now()));

        controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceSudahDaftar,
                new TextMessageContent("123", "hapusutang 99"), Instant.now()));


        String[] compareList = outContent.toString().split("&");
        int lastElement = compareList.length - 1;


        String compare = compareList[lastElement - 1].trim().replace("\n", "");

        String expected = "Tidak ada utang dengan nomor 99";

        System.err.println(outContent.toString());
        assertEquals(expected, compare);
    }


     @Test
     public void testLaporanPengeluaran() {
         controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceLaporan,
                 new TextMessageContent("123", "daftar 30000"), Instant.now()));

         controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceLaporan,
                 new TextMessageContent("123", "makanan 5000 enak"), Instant.now()));

         controller.messageEventHandleText(new MessageEvent<>("1234", mockSourceLaporan,
                 new TextMessageContent("123", "laporan"), Instant.now()));

         String result = outContent.toString().split("#")[outContent.toString().split("#").length - 2];
         String currentDate = new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date());
         String currentTime = new SimpleDateFormat("HH:mm").format(new Date());

         assertEquals(
                 "Laporan pengeluaran sebulan terakhir: \n" +
                         "\n" + currentDate +"\n" +
                         "1)\tKategori: makanan" +
                         "\n\t\tDeskripsi: enak " +
                         "\n\t\tJumlah: 5000" +
                         "\n\t\tJam: " + currentTime + "\n\n" +
                         "Total pengeluaran bulan ini: 5000" +
                         "\nTarget pengeluaran anda: 30000" +
                         "\n\nSisa pengeluaran yang bisa anda lakukan: 25000"
                 , result);
     }
}
