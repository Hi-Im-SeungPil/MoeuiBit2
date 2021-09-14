package org.jeonfeel.moeuibit2.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jeonfeel.moeuibit2.Activitys.Activity_Login;
import org.jeonfeel.moeuibit2.R;

public class Fragment_setting extends Fragment {

    Button btn_sendError,btn_sendSuggestions,btn_appReset,btn_logout;
    Context context;

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

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getActivity();
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