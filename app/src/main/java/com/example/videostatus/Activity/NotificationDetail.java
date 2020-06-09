package com.example.videostatus.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.videostatus.Fragment.SCDetailFragment;
import com.example.videostatus.R;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class NotificationDetail extends AppCompatActivity {

    public Toolbar toolbar;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        Method.forceRTLIfSupported(getWindow(), NotificationDetail.this);

        Method method = new Method(NotificationDetail.this);


        toolbar = findViewById(R.id.toolbar_notification_detail);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_notification_detail);

        String video_id = getIntent().getStringExtra("video_id");
        SCDetailFragment scDetailFragment = new SCDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", video_id);
        bundle.putString("type", "notification");
        bundle.putInt("position", 0);//dummy value
        scDetailFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main_notification_detail, scDetailFragment, getResources().getString(R.string.notification)).commitAllowingStateLoss();

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout, NotificationDetail.this);
        } else {
            method.showNonPersonalizedAds(linearLayout, NotificationDetail.this);
        }

        checkPer();

    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(NotificationDetail.this, "android.permission.WRITE_EXTERNAL_STORAGE"
                + "android.permission.WRITE_INTERNAL_STORAGE" + "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.WRITE_INTERNAL_STORAGE",
                                "android.permission.READ_EXTERNAL_STORAGE"},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                Method.allowPermitionExternalStorage = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                    Method.allowPermitionExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(NotificationDetail.this, getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                    Method.allowPermitionExternalStorage = false;
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Events.StopPlay stopPlay = new Events.StopPlay("");
        GlobalBus.getBus().post(stopPlay);
        startActivity(new Intent(NotificationDetail.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finishAffinity();
        super.onBackPressed();
    }
}
