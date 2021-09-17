package org.jeonfeel.moeuibit2.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jeonfeel.moeuibit2.Activitys.Activity_Login;
import org.jeonfeel.moeuibit2.Activitys.Activity_coinInfo;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.R;

public class Fragment_setting extends Fragment {

    Button btn_sendError,btn_sendSuggestions,btn_appReset,btn_logout;
    Context context;
    MoEuiBitDatabase db;

    public Fragment_setting() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = MoEuiBitDatabase.getInstance(context);

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getActivity();

        btn_appReset = rootView.findViewById(R.id.btn_appReset);
        btn_appReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("앱 초기화")
                        .setMessage("다시 되돌릴 수 없습니다. 그래도 초기화 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.userDAO().deleteAll();
                                db.transactionInfoDAO().deleteAll();
                                db.myCoinDAO().deleteAll();
                                db.favoriteDAO().deleteAll();
                                Toast.makeText(context, "초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("취소",null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }


        });

        btn_sendSuggestions = rootView.findViewById(R.id.btn_sendSuggestions);
        btn_sendSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSffNcVEhGf_wEsQoEmXLPZAu4u5akcibI5va-MPMp5VANwiNA/viewform?usp=sf_link"));
                startActivity(intent);
            }
        });

        btn_sendError = rootView.findViewById(R.id.btn_sendError);
        btn_sendError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScHWxBRoZk4WvhDWDUwlNXjSmbcmOLy2by6SE4lq2VlaJUUTw/viewform?usp=sf_link"));
                startActivity(intent);

            }
        });

        btn_logout = rootView.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Activity_Login.class);
                startActivity(intent);
                ((Activity) context).finish();
            }
        });
        return rootView;
    }
}