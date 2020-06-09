package com.example.videostatus.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class ContactUs extends AppCompatActivity {

    public Toolbar toolbar;
    private Method method;
    private EditText editText_name, editText_email, editText_message;
    private String name, email, message;
    private LinearLayout linearLayout;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Method.forceRTLIfSupported(getWindow(), ContactUs.this);

        method = new Method(ContactUs.this);

        toolbar = findViewById(R.id.toolbar_contact_us);
        toolbar.setTitle(getResources().getString(R.string.contact_us));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(ContactUs.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText_name = findViewById(R.id.editText_name_contact_us);
        editText_email = findViewById(R.id.editText_email_contact_us);
        editText_message = findViewById(R.id.editText_message_contact_us);

        if (method.pref.getBoolean(method.pref_login, false)) {
            editText_name.setText(method.pref.getString(method.userName, ""));
            editText_email.setText(method.pref.getString(method.userEmail, ""));
        }

        linearLayout = findViewById(R.id.linearLayout_contact_us);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout, ContactUs.this);
        } else {
            method.showNonPersonalizedAds(linearLayout, ContactUs.this);
        }

        Button button_submit = findViewById(R.id.button_contact_us);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = editText_name.getText().toString();
                email = editText_email.getText().toString();
                message = editText_message.getText().toString();

                form();

            }
        });

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void form() {

        editText_name.setError(null);
        editText_email.setError(null);
        editText_message.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editText_name.requestFocus();
            editText_name.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editText_email.requestFocus();
            editText_email.setError(getResources().getString(R.string.please_enter_email));
        } else if (message.equals("") || message.isEmpty()) {
            editText_message.requestFocus();
            editText_message.setError(getResources().getString(R.string.please_enter_message));
        } else {

            editText_name.clearFocus();
            editText_email.clearFocus();
            editText_message.clearFocus();
            imm.hideSoftInputFromWindow(editText_name.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_email.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_message.getWindowToken(), 0);

            if (Method.isNetworkAvailable(ContactUs.this)) {
                contact_us(email, name, message);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void contact_us(String sendEmail, String sendName, String sendMessage) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_contact_us");
        jsObj.addProperty("contact_email", sendEmail);
        jsObj.addProperty("contact_name", sendName);
        jsObj.addProperty("contact_msg", sendMessage);
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
                        String msg = object.getString("msg");
                        String success = object.getString("success");

                        if (success.equals("1")) {

                            editText_name.setText("");
                            editText_email.setText("");
                            editText_message.setText("");

                            Toast.makeText(ContactUs.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ContactUs.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
