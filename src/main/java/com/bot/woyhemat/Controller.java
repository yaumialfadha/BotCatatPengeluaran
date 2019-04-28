package com.bot.woyhemat;

import com.bot.woyhemat.database.DatabaseCollection;
import com.bot.woyhemat.database.Debt;
import com.bot.woyhemat.database.User;
import com.bot.woyhemat.handler.*;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

@LineMessageHandler
public class Controller {

    @Autowired
    LineMessagingClient lineMessagingClient;
    // REFACTOR THIS
    AktivitasHandler aktivitasHandler = new AktivitasHandler();
    LaporanHandler laporanHandler = new LaporanHandler();
    UtangHandler utangHandler = new UtangHandler();
    HistoriHandler historiHandler = new HistoriHandler();
    NotifikasiHandler notifikasiHandler = new NotifikasiHandler();

    @Autowired
    Controller control;

    @Autowired
    DatabaseCollection database;


    /**
     * Convert localDate to Date object
     *
     * @param localDate objek yang akan di convert
     * @return Date hasil konversi
     */
    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @EventMapping
    public void messageEventHandleText(MessageEvent<TextMessageContent> event) {


        System.out.println("JALAN OI"); //LOG
        String userId = event.getSource().getUserId();
        TextMessageContent message = event.getMessage();
        String messageString = message.getText().toLowerCase();
        String[] splitMessageString = messageString.split(" ");
        System.out.println(messageString); //LOG
        System.out.println(splitMessageString[0]); //LOG
        if (splitMessageString[0].equals("daftar")) {
            String reply = newUser(userId, Integer.parseInt(splitMessageString[1]));
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));

        } else if (splitMessageString[0].equals("tambah") && splitMessageString[1].equals("pengeluaran")) {
            String reply = kategori();
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));


        } else if (splitMessageString[0].equals("makanan") || splitMessageString[0].equals("hiburan") || splitMessageString[0].equals("lainnya") || splitMessageString[0].equals("minuman")) {
            // example: makanan 10000 Ayam Goreng
            String kategori = splitMessageString[0];
            int jumlah = Integer.parseInt(splitMessageString[1]);
            String deskripsi = "";
            for (int i = 2; i < splitMessageString.length; i++) {
                deskripsi += splitMessageString[i] + " ";
            }
            String reply = tambahAktivitas(userId, kategori, jumlah, deskripsi);
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));

        } else if (splitMessageString[0].equals("info")) {

            String reply = info();
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));
            System.out.println("#" + reply + "#");


        } else if (splitMessageString[0].equals("laporan")) {
            String reply = showPengeluaranSebulan(userId);
            System.out.println("#" + reply + "#");
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));

        } else if (splitMessageString[0].equals("target")) {
            String reply = setTarget(userId, Integer.parseInt(splitMessageString[1]));
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));
        }
        // utang <jumlah> <Deadline utang dalma hari> <keterangan>
        // utang 5000 5 aqua ke paijo
        else if (splitMessageString[0].equals("utang")) {
            System.out.println("masuk utang"); // LOG
            String balasan;

            // Verify input
            if (!utangInputVerifier(splitMessageString)) {
                balasan = "Maaf, perintah tidak dikenali. Mungkin maksud anda \n utang <jumlah utang> <berapa hari " +
                        "hingga deadline> <deskripsi>. \nTulis \"info\" untuk informasi lebih lanjut ";
            } else {

                LocalDate tanggalNow = LocalDate.now(); // Mengambil tanggal sekarang
                tanggalNow = tanggalNow.plusDays(Integer.parseInt(splitMessageString[2])); // Menambahkan tanggal sekarang dengan waktu jatuh tempo
                Date tanggalDate = asDate(tanggalNow); // Mengubah object LocalDate ke Date agar bisa masuk ke repository


                String keterangan = getKeteranganUtang(splitMessageString);

                Boolean adaUser = tambahUtang(Integer.parseInt(splitMessageString[1]), tanggalDate, userId, keterangan);

                if (!adaUser) {
                    balasan = "Maaf, anda belum terdaftar";
                } else {
                    balasan = "Berhasil menambahkan utang sebesar " + splitMessageString[1] + ", jatuh tempo dalam "
                            + splitMessageString[2] + " hari, tulis 'lihatutang' tanpa tanda kutip untuk melihat semua utang";
                }
            }
            System.out.println("#" + balasan + "#");
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(balasan)));
        }

        // lihatutang
        else if (splitMessageString[0].equals("lihatutang") && splitMessageString.length == 1) {
            System.out.println("masuk lihatutang"); // LOG
            ArrayList<Debt> debts = utangHandler.getUtangUserArray(userId, database.getRepoDebtInstance());

            String balasan = lihatUtang(debts, userId);
            System.out.println("#" + balasan + "#"); // Untuk test
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(balasan)));
        } else if (splitMessageString[0].equals("hapusutang") && splitMessageString.length == 2) {
            String balasan = "";
            if (splitMessageString[1].matches("[0-9]+")) {
                balasan = hapusUtang(Integer.parseInt(splitMessageString[1]), userId);

            } else {
                balasan = "Maaf perintah hapusutang salah, seharusnya hapusutang <no utang>";
            }

            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(balasan)));


        } else if (splitMessageString[0].equals("histori")) {
            System.out.println("histori"); //Log
            String balasan = historiHandler.getHistoriPengeluaran(userId, database.getRepoUserInstance(), database.getRepoExpenditureInstance(), database.getRepoDebtInstance());
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(balasan)));
        } else {
            String reply = salah();
            System.out.println("#" + reply + "#");
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(reply)));
        }


    }

    /**
     * method untuk mengecek apakah input untuk utang sudah benar atau belum
     * contoh input yang benar
     * utang <jumlah utang> <tenggat waktu berapa hari> <deskripsi>
     * utang 500 5 utang ke paijo
     *
     * @param strings string input yang akan dicek
     * @return true jika input tidak ada masalah, false jika input salah
     */
    public Boolean utangInputVerifier(String[] strings) {
        if (strings.length > 2) {
            if (strings[1].matches("[0-9]+") && strings[2].matches("[0-9]+")) {
                return Integer.parseInt(strings[1]) > 0 && Integer.parseInt(strings[2]) > 0;
            }
        }

        return false;
    }

    /**
     * Mengambil keterangan utang dari pesan,
     * misal utang 300 5 beli aqua dari paijo belom bayar
     *
     * @param string array pesan yang sudah di split
     * @return akan me return string "beli aqua dari paijo belom bayar"
     */
    public String getKeteranganUtang(String[] string) {
        String hasil = "-";
        for (int i = 3; i < string.length; i++) {
            hasil += string[i] + " ";
        }

        if (!hasil.equals("-")) {
            hasil = hasil.substring(1);
        }

        return hasil;
    }


    public String tambahAktivitas(String userId, String kategori, int jumlah, String deskripsi) {
        if (database.getRepoUserInstance().findByUsername(userId) == null) {
            return "Maaf Anda belom terdaftar";
        } else if (jumlah < 0) {
            return "Maaf jumlah pengeluaran tidak boleh kurang dari 0";
        } else {
            User thisUser = database.getRepoUserInstance().findByUsername(userId);
            aktivitasHandler.tambahPengeluaran(kategori, jumlah, deskripsi, thisUser, database.getRepoExpenditureInstance());
            if(kondisiTarget(userId)) {
                return "Pengeluaran berhasil di tambah. \n WARNING: total pengeluaran sudah melebihi target";
            }
            return "Pengeluaran berhasil di tambah";
        }
    }


    // register template : daftar *target*
    // eg daftar 1000000
    public String newUser(String userId, int target) {
        if (database.getRepoUserInstance().findByUsername(userId) != null) {
            return "Maaf Anda sudah terdaftar";
        } else if (target <= 0) {
            return "Maaf target tidak boleh kurang dari 0";
        } else {
            User newUser = new User(userId, target, 0);
            database.getRepoUserInstance().save(newUser);
            return "Anda telah terdaftar!";
        }
    }

    public String lihatUtang(ArrayList<Debt> debts, String userId) {
        int counter = 1;
        String balasan = "[Utang] : \n";
        for (Debt debt : debts) {
            System.out.println("Loop getUtangUser"); // LOG

            String tanggalJatuhTempoString = dateToString(debt.getPeriod());

            if (debt.getUser().getUsername().equals(userId)) {
                balasan += counter + ") Jumlah: " + debt.getAmount() + "\n Jatuh Tempo: " + tanggalJatuhTempoString +
                        " \n Keterangan: " + debt.getKeterangan() + "\n";
                counter++;
            }

        }
        return balasan;
    }

    /**
     * Mengubah object Date jadi string dengan pola dd-mm-yyyy
     *
     * @param date object date yang akan diubah
     * @return string date yang sudah diubah
     */
    public String dateToString(Date date) {
        String hasil = new SimpleDateFormat("dd-MM-yyyy").format(date);
        return hasil;
    }

    // method yang dijalankan ketika menambahkan utang ke suatu user
    // method ini menggunakan utangHandler
    public Boolean tambahUtang(int jumlah, Date waktu, String username, String keterangan) {

        System.out.println("JALAN TAMBAH UTANG"); // LOG
        User theUser = database.getRepoUserInstance().findByUsername(username);

        // Jika tidak belum terdaftar, return null
        if (theUser == null) {
            System.out.println("USER NULL"); // LOG
            return false;
        }

        UtangHandler utangHandlerObj = utangHandler;
        utangHandlerObj.tambahUtang(jumlah, waktu, theUser, keterangan, database.getRepoDebtInstance());
        System.out.println("USER NOT NULL"); // LOG
        System.out.println(theUser);
        return true;
    }

    public String hapusUtang(int number, String userId) {
        User theUser = database.getRepoUserInstance().findByUsername(userId);
        ArrayList<Debt> utangs = utangHandler.getUtangUserArray(theUser.getUsername(), database.getRepoDebtInstance());

        if (number > utangs.size() || number < 0) {
            System.out.println("&Tidak ada utang dengan nomor " + number + "&"); // LOG
            return "Tidak ada utang dengan nomor " + number;
        }

        number--;
        Debt deleteDebt = utangs.get(number);


        utangHandler.hapusUtang(deleteDebt, database.getRepoDebtInstance());
        System.out.println("&" + utangHandler.getUtangUserArray(theUser.getUsername(), database.getRepoDebtInstance()).get(0).getKeterangan() + "&"); // LOG
        return "Utang berhasil dihapus";
    }

    /**
     * Mengembalikan pengeluaran user dalam satu bulan.
     *
     * @param username username/id user
     * @return string data pengeluaran user dalam satu bulan
     */
    public String showPengeluaranSebulan(String username) {
        String reply = laporanHandler.showPengeluaranSebulan(username, database.getRepoUserInstance(), database.getRepoExpenditureInstance());
        return reply;
    }

    public boolean kondisiTarget(String username) {
        boolean reply = notifikasiHandler.kondisiTarget(username, database.getRepoUserInstance(), database.getRepoExpenditureInstance());
        return reply;
    }

    public String targetLebih() {
        //String reply = notifikasiHandler.targetLebih();
        return "Pengeluaran berlebih";
    }

    public String info() {
        return "Berikut fitur-fitur yang terdapat pada WoyHemat! :\n" +
                "- info\n" +
                "- target [jumlah]\n" +
                "- tambah pengeluaran\n" +
                "- laporan\n" +
                "- daftar [target]\n" +
                "- utang [jumlah utang] [berapa hari] [keterangan]\n"+
                "- hapusutang [nomor utang]\n"+
                "- lihatutang\n" +
                "- histori";
    }

    public String infoTambah() {
        return "Cara menggunakan fitur tambah:\n" +
                "*kategogori* *jumlahuang* *deskripsi";
    }

    public String infoUtang() {
        return "Cara menggunakan fitur Utang:\n" +
                "*kategogori* *jumlahuang* *deskripsi";
    }


    public String salah() {

        return "Fitur tidak tersedia";
    }

    public String setTarget(String userId, int jumlah) {
        User user = database.getRepoUserInstance().findByUsername(userId);
        user.setTarget(jumlah);
        return "Anda telah menentukan target pengeluaran sebesar " + jumlah;
    }

    public String kategori() {
        return "Pilih satu kategori: \n - Makanan/Minuman \n - Hiburan \n - Lainnya  \n Seterusnya ketik *kategori yang dipilih* *total pengeluaran* *deskripsi*";
    }


}
