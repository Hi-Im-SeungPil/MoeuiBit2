package org.jeonfeel.moeuibit2;

import android.app.Dialog;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CustomLodingDialog extends Dialog {

    Animation animation;
    ImageView iv_loading;

    public CustomLodingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.custom_loading);

        animation = AnimationUtils.loadAnimation(context,R.anim.turn_around);
        iv_loading = findViewById(R.id.iv_loading);
        iv_loading.startAnimation(animation);

    }

}
