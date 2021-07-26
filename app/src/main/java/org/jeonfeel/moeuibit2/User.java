package org.jeonfeel.moeuibit2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    public int krw;

    public User(int krw) {
        this.krw = krw;
    }
}
