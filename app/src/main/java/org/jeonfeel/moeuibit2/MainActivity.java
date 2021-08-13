package org.jeonfeel.moeuibit2;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jeonfeel.moeuibit2.Fragment.Fragment_Exchange;
import org.jeonfeel.moeuibit2.Fragment.Fragment_coinInfo;
import org.jeonfeel.moeuibit2.Fragment.Fragment_investmentDetails;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMenuBottom();
    }
    private void setMenuBottom(){

        Fragment_Exchange fragment_exchange = new Fragment_Exchange();
        Fragment_investmentDetails fragment_investmentDetails = new Fragment_investmentDetails();
        Fragment_coinInfo fragment_coinInfo = new Fragment_coinInfo(MainActivity.this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment_exchange).commit();

        BottomNavigationView menu_bottom = findViewById(R.id.menu_bottom);
        menu_bottom.setBackgroundColor(Color.parseColor("#0F0F5C"));
        menu_bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.tab_exchange){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_exchange).commit();

                    return true;
                }else if(item.getItemId() == R.id.tab_myPortfolio){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_investmentDetails).commit();

                    return true;
                }else if(item.getItemId() == R.id.tab_coinInfo){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_coinInfo).commit();

                    return true;
                }
                return false;
            }
        });
    }



}