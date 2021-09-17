package org.jeonfeel.moeuibit2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jeonfeel.moeuibit2.Fragment.Fragment_Exchange;
import org.jeonfeel.moeuibit2.Fragment.Fragment_coinSite;
import org.jeonfeel.moeuibit2.Fragment.Fragment_investmentDetails;
import org.jeonfeel.moeuibit2.Fragment.Fragment_setting;

public class MainActivity extends FragmentActivity {

    String currentFragment = "";
    private AdView mAdView;
    private long backBtnTime = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnCheckPermission();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if(adRequest != null)
        mAdView.loadAd(adRequest);

        setMenuBottom();
    }
    private void setMenuBottom(){
        CustomLodingDialog customLodingDialog = new CustomLodingDialog(this);
        Fragment_Exchange fragment_exchange = new Fragment_Exchange(customLodingDialog,mAdView);
        Fragment_investmentDetails fragment_investmentDetails = new Fragment_investmentDetails(customLodingDialog);
        Fragment_coinSite fragment_coinInfo = new Fragment_coinSite(MainActivity.this);
        Fragment_setting fragment_setting = new Fragment_setting();


        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment_exchange).commit();
        currentFragment = "fragment_exchange";

        BottomNavigationView menu_bottom = findViewById(R.id.menu_bottom);
        menu_bottom.setBackgroundColor(Color.parseColor("#0F0F5C"));
        menu_bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.tab_exchange && !currentFragment.equals("fragment_exchange")){
                    customLodingDialog.show();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_exchange).commit();
                    currentFragment = "fragment_exchange";

                    return true;
                }else if(item.getItemId() == R.id.tab_myPortfolio && !currentFragment.equals("fragment_investmentDetails")){
                    customLodingDialog.show();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_investmentDetails).commit();
                    currentFragment = "fragment_investmentDetails";

                    return true;
                }else if(item.getItemId() == R.id.tab_coinInfo && !currentFragment.equals("fragment_coinInfo")){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_coinInfo).commit();
                    currentFragment = "fragment_coinInfo";

                    return true;
                }else if(item.getItemId() == R.id.tab_setting && !currentFragment.equals("fragment_setting")){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment_setting).commit();
                    currentFragment = "fragment_setting";

                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }
    public void OnCheckPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "앱 실행을 위해서는 권한을 꼭!! 설정해야 합니다", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},

                        PERMISSIONS_REQUEST);

            } else {

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},

                        PERMISSIONS_REQUEST);

            }
        }
    }


    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST:

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_LONG).show();

                }

                break;

        }
    }

}