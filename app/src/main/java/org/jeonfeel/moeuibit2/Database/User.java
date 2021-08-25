package org.jeonfeel.moeuibit2.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    public long krw;

    public User(long krw) {
        this.krw = krw;
    }

    public long getKrw() {
        return krw;
    }
    public void setKrw(long krw) {
        this.krw = krw;
    }
}
