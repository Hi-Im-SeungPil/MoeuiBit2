package org.jeonfeel.moeuibit2;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyCoin {

    @PrimaryKey
    @NonNull
    public String market;
    @ColumnInfo
    public Double purchasePrice;
    @ColumnInfo
    public String koreanCoinName;
    @ColumnInfo
    public String symbol;
    @ColumnInfo
    public Double quantity;

}
