package com.example.videostatus.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.onesignal.OneSignal;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SplashScreen extends AppCompatActivity {

    // splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private Boolean isCancelled = false;
    private Method method;
    String video_id = "0";
    String payment_withdraw = "false";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        method = new Method(SplashScreen.this);
        method.login();

        Log.d("user_id", String.valueOf(method.pref.getString(method.profileId, null)));

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

        Method.forceRTLIfSupported(getWindow(), SplashScreen.this);

        if (getIntent().hasExtra("video_id")) {
            video_id = getIntent().getStringExtra("video_id");
            Log.d("video_id", video_id);
        }

        if (getIntent().hasExtra("payment_withdraw")) {
            payment_withdraw = getIntent().getStringExtra("payment_withdraw");
            Log.d("payment_withdraw", payment_withdraw);
        }

        if (Method.isNetworkAvailable(SplashScreen.this)) {
            splashScreen();
        } else {
            alertBoxSplashScreen(getResources().getString(R.string.internet_connection));
        }
    }

    public void login(final String sendEmail, final String sendPassword) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_login");
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String success = object.getString("success");
                        if (success.equals("1")) {
                            OneSignal.sendTag("user_id", method.pref.getString(method.profileId, null));
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            OneSignal.sendTag("user_id", "0");
                            method.editor.putBoolean(method.pref_login, false);
                            method.editor.commit();
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finish();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("error", error.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void check() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "app_settings");
        params.put("data", API.toBase64(jsObj.toString()));
        Log.d("error_catch", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String package_name = object.getString("package_name");

                        if (package_name.equals(getApplication().getPackageName())) {
                            splashScreen();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    alertBoxSplashScreen(getResources().getString(R.string.wrong));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                alertBoxSplashScreen(getResources().getString(R.string.wrong));
            }
        });
    }

    public void splashScreen() {

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (!isCancelled) {
                    if (payment_withdraw.equals("true")) {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class)
                                .putExtra("payment_withdraw", payment_withdraw));
                        finishAffinity();
                    } else if (video_id.equals("0")) {
                        if (method.pref.getBoolean(method.pref_login, false)) {
                            Log.d("value", String.valueOf(method.pref.getBoolean(method.pref_login, false)));
                            login(method.pref.getString(method.userEmail, null), method.pref.getString(method.userPassword, null));
                        } else {
                            OneSignal.sendTag("user_id", "0");
                            if (method.pref.getBoolean(method.is_verification, false)) {
                                startActivity(new Intent(SplashScreen.this, Verification.class));
                                finishAffinity();
                            } else {
                                Intent i = new Intent(SplashScreen.this, Login.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    } else {
                        Log.d("video_id", video_id);
                        startActivity(new Intent(SplashScreen.this, NotificationDetail.class).putExtra("video_id", video_id));
                        finish();
                    }
                }

            }
        }, SPLASH_TIME_OUT);

    }

    //---------------Alert Box---------------//

    public void alertBoxSplashScreen(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreen.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finishAffinity();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}


