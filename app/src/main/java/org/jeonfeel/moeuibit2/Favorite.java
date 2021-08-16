package org.jeonfeel.moeuibit2;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favorite {

    @PrimaryKey
    @NonNull
    public String market;

    public Favorite(@NonNull String market) {
        this.market = market;
    }

    @NonNull
    public String getMarket() {
        return market;
    }

    public void setMarket(@NonNull String market) {
        this.market = market;
    }
}
