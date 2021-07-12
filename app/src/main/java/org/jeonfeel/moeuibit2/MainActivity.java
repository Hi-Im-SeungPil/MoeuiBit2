package org.jeonfeel.moeuibit2;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends FragmentActivity {

    Fragment_Exchange fragment_exchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMenuBottom();

    }
    private void setMenuBottom(){

        fragment_exchange = new Fragment_Exchange();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_exchange).commit();
        BottomNavigationView menu_bottom = findViewById(R.id.menu_bottom);
        menu_bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.tab_exchange){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment_exchange).commit();

                    return true;
                }
                return false;
            }
        });
    }
}