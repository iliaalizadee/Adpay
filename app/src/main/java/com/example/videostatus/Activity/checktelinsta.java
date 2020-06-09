package com.example.videostatus.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class checktelinsta extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    Toolbar toolbar;
    Button button;
    Method method;
    RadioGroup radioGroup;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checktelinsta);
        toolbar  = findViewById(R.id.checktool);
        button = findViewById(R.id.checkbutton);
        editText = findViewById(R.id.checkedit);
        radioGroup = findViewById(R.id.checkradiogrp);
        method = new Method(checktelinsta.this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("بررسی عضویت تلگرام/اینستاگرام");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                JsonObject object = (JsonObject) new Gson().toJsonTree(new API());
                if(radioGroup.getCheckedRadioButtonId() == R.id.telradio) {
                    object.addProperty("method_name", "check_telegram");
                }else if(radioGroup.getCheckedRadioButtonId() == R.id.instaradio){
                    object.addProperty("method_name", "check_insta");
                }
                object.addProperty("user_name",editText.getText().toString());
                object.addProperty("user_id",method.pref.getString(method.profileId,null));
                params.put("data",API.toBase64(object.toString()));
                client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(checktelinsta.this,"اطلاعات برای بررسی ارسال شد",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });
    }
}
