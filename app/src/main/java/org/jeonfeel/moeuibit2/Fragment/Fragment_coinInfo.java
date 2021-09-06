package org.jeonfeel.moeuibit2.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.moeuibit2.R;

public class Fragment_coinInfo extends Fragment {

    private LinearLayout linear_coinInfo,linear_homepage,linear_twitter,linear_amount,linear_block;
    private DatabaseReference mDatabase;
    private final String market;
    private String homepage,amount,twitter,block,info;

    public Fragment_coinInfo(String market) {
        this.market = market;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coin_info, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FindViewById(rootView);
        getCoinData();

        return rootView;
    }

    private void FindViewById(View rootView){
        linear_coinInfo = rootView.findViewById(R.id.linear_coinInfo);
        linear_homepage = rootView.findViewById(R.id.linear_homepage);
        linear_twitter = rootView.findViewById(R.id.linear_twitter);
        linear_amount = rootView.findViewById(R.id.linear_amount);
        linear_block = rootView.findViewById(R.id.linear_block);
    }

    private void getCoinData(){

        Log.d("qqqq",market);

        mDatabase.child("coinInfo").child(market).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                homepage = snapshot.child("homepage").getValue(String.class);
                amount = snapshot.child("amount").getValue(String.class);
                twitter = snapshot.child("twitter").getValue(String.class);
                block = snapshot.child("block").getValue(String.class);
                info = snapshot.child("content").getValue(String.class);

                LinearLayout[] linearLayouts = {linear_amount,linear_block,linear_coinInfo,linear_homepage,linear_twitter};
                String[] url = {amount,block,info,homepage,twitter};

                for(int i = 0; i < 5; i++){
                    int a = i;

                    linearLayouts[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url[a]));
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}