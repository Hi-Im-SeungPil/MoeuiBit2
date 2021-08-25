package org.jeonfeel.moeuibit2.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotSigned {

    @PrimaryKey
    @NonNull
    public String market;
    @ColumnInfo
    public String koreanCoinName;
    @ColumnInfo
    public String symbol;
    @ColumnInfo
    public Double notSignedPrice;
    @ColumnInfo
    public Double quantity;

}
