package com.bot.woyhemat.handler;

import java.util.Calendar;
import java.util.List;

import com.bot.woyhemat.database.Debt;
import com.bot.woyhemat.database.DebtRepository;
import com.bot.woyhemat.database.Expenditure;
import com.bot.woyhemat.database.ExpenditureRepository;
import com.bot.woyhemat.database.User;
import com.bot.woyhemat.database.UserRepository;

/**
 * Handler ini berfungsi untuk menampilkan jumlah pengeluaran
 * per bulan dan total utang yang dimiliki
 * 
 * @author Anthony Dewa P
 */
public class HistoriHandler {

    /**
     * Handler yang berfungsi untuk memberikan reply ke pengguna sesuai data
     * yang dimilikinya
     * 
     * @param userId userId dari pengguna yang akan ditampilkan historinya
     * @param userRepository user database repository
     * @param expenditureRepository expense database repository
     * @param debtRepository debt database repository
     * @return String yang akan menjadi reply
     */
    public String getHistoriPengeluaran(String userId,
                                   UserRepository userRepository,
                                   ExpenditureRepository expenditureRepository,
                                   DebtRepository debtRepository) {
        
        User user = userRepository.findByUsername(userId);

        if (user == null) {
            return "Maaf, anda belum terdaftar";
        }

        List<Expenditure> userExpense = expenditureRepository.findByUser(user);
        int[] jumlahPengeluaranPerBulan = getTotalPengeluaran(userExpense);

        String reply = "Total Pengeluaran: \n";
        Boolean memilikiPengeluaran = false;
        for (int i = 0; i < jumlahPengeluaranPerBulan.length; i++) {
            if (jumlahPengeluaranPerBulan[i] != 0) {
                reply += "Pengeluaran bulan ke-" + (i + 1) + ": " + jumlahPengeluaranPerBulan[i] + "\n";
                memilikiPengeluaran = true;
            }
        }

        if (!memilikiPengeluaran) {
            reply += "Anda belum memiliki pengeluaran\n";
        }

        int jumlahUtang = getTotalUtang(userId, debtRepository);

        reply += "\nTotal Utang: " + jumlahUtang;

        return reply;
    }

    /**
     * Method untuk mendapatkan total utang yang dimiliki user
     * 
     * @param userId userId dari pengguna yang akan ditampilkan historinya
     * @param debtRepository debt database repository
     * @return int total utang yang dimiliki
     */
    private int getTotalUtang(String userId, DebtRepository debtRepository) {
        int jumlahUtang = 0;

        for (Debt debt : debtRepository.findAll()) {
            if (debt.getUser().getUsername().equals(userId)) {
                jumlahUtang += debt.getAmount();
            }
        }
        return jumlahUtang;
    }

    /**
     * Method untuk mendapatkan pengeluaran tiap bulan pada tahun ini
     * 
     * @param userExpense data pengeluaran dari pengguna yang akan ditampilkan historinya
     * @return array on int tiap indexnya merepresentasikan jumlah pengeluaran tiap bulan
     */
    private int[] getTotalPengeluaran(List<Expenditure> userExpense) {
        int[] jumlahPengeluaranPerBulan = new int[12];
        Calendar dateNow = Calendar.getInstance();
        
        for (int i = 0; i < userExpense.size(); i++) {
            Expenditure expense = userExpense.get(i);
            Calendar expenseDate = Calendar.getInstance();
            expenseDate.setTime(expense.getTimestamp());
            int expenseMonth = expenseDate.get(Calendar.MONTH);
            int expenseYear = expenseDate.get(Calendar.YEAR);

            if (dateNow.get(Calendar.YEAR) == expenseYear) {
                jumlahPengeluaranPerBulan[expenseMonth] += expense.getAmount();
            }
        }

        return jumlahPengeluaranPerBulan;
    }
}