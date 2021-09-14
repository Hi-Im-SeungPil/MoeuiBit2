package org.jeonfeel.moeuibit2;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jeonfeel.moeuibit2.Fragment.Fragment_Exchange;
import org.jeonfeel.moeuibit2.Fragment.Fragment_coinSite;
import org.jeonfeel.moeuibit2.Fragment.Fragment_investmentDetails;
import org.jeonfeel.moeuibit2.Fragment.Fragment_setting;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMenuBottom();
    }
    private void setMenuBottom(){
        CustomLodingDialog customLodingDialog = new CustomLodingDialog(this);
        Fragment_Exchange fragment_exchange = new Fragment_Exchange(customLodingDialog);
        Fragment_investmentDetails fragment_investmentDetails = new Fragment_investmentDetails(customLodingDialog);
        Fragment_coinSite fragment_coinInfo = new Fragment_coinSite(MainActivity.this);
        Fragment_setting fragment_setting = new Fragment_setting();


        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment_exchange).commit();

        BottomNavigationView menu_bottom = findViewById(R.id.menu_bottom);
        menu_bottom.setBackgroundColor(Color.parseColor("#0F0F5C"));
        menu_bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.tab_exchange){
                    customLodingDialog.show();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_exchange).commit();

                    return true;
                }else if(item.getItemId() == R.id.tab_myPortfolio){
                    customLodingDialog.show();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_investmentDetails).commit();

                    return true;
                }else if(item.getItemId() == R.id.tab_coinInfo){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_coinInfo).commit();

                    return true;
                }else if(item.getItemId() == R.id.tab_setting){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_setting).commit();
                    return true;
                }
                return false;
            }
        });
    }



}