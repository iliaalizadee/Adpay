package com.example.videostatus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;
import com.example.videostatus.Util.PrefManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class admobactivity extends AppCompatActivity {
    RewardedVideoAd mRewardedVideoAd;
    ProgressDialog dialog;
    Method method;
    RewardedAd rewardedAd;
    PrefManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admobactivity);
        manager = new PrefManager(this);
        method = new Method(admobactivity.this);
        dialog = ProgressDialog.show(this,"در حال بارگذاری","لطفا صبور باشید");
        rewardedAd = new RewardedAd(this,"ca-app-pub-3940256099942544/5224354917");
        RewardedAdCallback adCallback = new RewardedAdCallback() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                JsonObject object = (JsonObject) new Gson().toJsonTree(new API());
                object.addProperty("method_name","admob_watch");
                object.addProperty("watch_admob",manager.getUserAdmob());
                object.addProperty("user_id",method.pref.getString(method.profileId,null));
                params.put("data",API.toBase64(object.toString()));
                client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(admobactivity.this,"امتیاز تبلیغ اضافه شد!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(admobactivity.this,"Error",Toast.LENGTH_SHORT).show();


                    }
                });
                manager.setUserAdmob(manager.getUserAdmob()+1);


            }

            @Override
            public void onRewardedAdClosed() {
                super.onRewardedAdClosed();
                finish();
            }

            @Override
            public void onRewardedAdFailedToShow(int i) {
                super.onRewardedAdFailedToShow(i);
                Toast.makeText(admobactivity.this,"Error",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        };
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback(){

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                dialog.dismiss();
                rewardedAd.show(admobactivity.this,adCallback);

            }

            @Override
            public void onRewardedAdFailedToLoad(int i) {
                super.onRewardedAdFailedToLoad(i);
                Toast.makeText(admobactivity.this,"Error",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(),adLoadCallback);


}
}



















