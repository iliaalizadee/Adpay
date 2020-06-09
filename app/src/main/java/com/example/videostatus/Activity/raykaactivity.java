package com.example.videostatus.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.videostatus.R;
import com.raykaad.AdListener;
import com.raykaad.Banner;
import com.raykaad.Raykaad;
import com.raykaad.VideoAdListener;

public class raykaactivity extends AppCompatActivity {
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raykaactivity);
        dialog = ProgressDialog.show(this,"در حال بارگذاری ","لطفا صبور باشید");

        Raykaad.cacheVideo(raykaactivity.this );
        Raykaad.setVideoListener(new VideoAdListener() {
            @Override
            public void onRequest() {

            }

            @Override
            public void onReady() {
                dialog.dismiss();
                Raykaad.showVideo(raykaactivity.this);
            }

            @Override
            public void onFail(String s) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onClick() {

            }
        });


    }
    @Override
    public void onBackPressed() {
        dialog.dismiss();
        finish();
    }
}
