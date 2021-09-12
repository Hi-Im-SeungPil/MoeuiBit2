package org.jeonfeel.moeuibit2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {

    public static int TYPE_NOT_CONNECTED = 0;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;

    public static int CheckNetwork(Context context){ //해당 context의 서비스를 사용하기위해서 context객체를 받는다.

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){//와이파이 연결된것
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
    }

}
