package org.jeonfeel.moeuibit2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyCoinDAO {

    @Query("SELECT * FROM MYCOIN")
    List<MyCoin> getAll();

    @Insert
    void insert(MyCoin myCoin);

    @Query("UPDATE MYCOIN SET purchasePrice = :price WHERE market = :market")
    void updatePurchasePrice(String market,Double price);

    @Query("UPDATE MYCOIN SET purchasePrice = :price WHERE market = :market")
    void updatePurchasePriceInt(String market,int price);

    @Query("UPDATE MYCOIN SET quantity = quantity + :afterQuantity  WHERE market = :market")
    void updatePlusQuantity(String market,Double afterQuantity);

    @Query("UPDATE MYCOIN SET quantity = quantity - :afterQuantity  WHERE market = :market")
    void updateMinusQuantity(String market,Double afterQuantity);

    @Query("UPDATE MYCOIN SET quantity = :afterQuantity  WHERE market = :market")
    void updateQuantity(String market,Double afterQuantity);

    @Query("SELECT * FROM MyCoin where market = :checkMarket")
    MyCoin isInsert(String checkMarket);

}
