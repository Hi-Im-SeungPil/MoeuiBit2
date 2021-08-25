package org.jeonfeel.moeuibit2.Database;

import androidx.room.Dao;
import androidx.room.Query;

import org.jeonfeel.moeuibit2.DTOS.RvTransactionInfoDTO;

import java.util.List;

@Dao
public interface TransactionInfoDAO {

    @Query("INSERT INTO TransactionInfo values(null,:market,:price,:quantity,:transactionAmount,:transactionStatus,:transactionTime)")
    void insert(String market, Double price, Double quantity, long transactionAmount,String transactionStatus,String transactionTime);

    @Query("SELECT * FROM TransactionInfo where market=:market")
    List<TransactionInfo> select(String market);

}
