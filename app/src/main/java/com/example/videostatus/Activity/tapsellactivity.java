package com.example.videostatus.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;
import com.example.videostatus.Util.PrefManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;

public class tapsellactivity extends AppCompatActivity {
    ProgressDialog dialog;
    Method method;
    PrefManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapsellactivity);
        manager = new PrefManager(this);
        method = new Method(this);
        TapsellPlus.initialize(this,"");
         dialog = ProgressDialog.show(this,"در حال بارگذاری","لطفا صبور باشید");
        requestAd();


    }
    private void requestAd(){
        TapsellPlus.requestRewardedVideo(this, "", new AdRequestCallback() {
            @Override
            public void response() {
                super.response();
                dialog.dismiss();
                showAd();
            }

            @Override
            public void response(String s) {
                super.response(s);
            }

            @Override
            public void error(String s) {
                super.error(s);
            }
        });
    }
    private void showAd() {
        TapsellPlus.showAd(this, "", new AdShowListener() {
            @Override
            public void onOpened() {
                super.onOpened();
            }

            @Override
            public void onClosed() {
                super.onClosed();
                finish();
            }

            @Override
            public void onRewarded() {
                super.onRewarded();
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                JsonObject object = (JsonObject) new Gson().toJsonTree(new API());
                object.addProperty("method_name","tapsell_watch");
                object.addProperty("watch_tapsell",manager.getUserTapsell());
                object.addProperty("user_id",method.pref.getString(method.profileId,null));
                params.put("data",API.toBase64(object.toString()));
                client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(tapsellactivity.this,"امتیاز تبلیغ اضافه شد!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
                manager.setUserTapsell(manager.getUserTapsell()+1);
            }

            @Override
            public void onError(String s) {
                super.onError(s);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialog.dismiss();
        finish();
    }

}
