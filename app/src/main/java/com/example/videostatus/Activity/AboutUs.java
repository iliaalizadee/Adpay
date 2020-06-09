package com.example.videostatus.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.example.videostatus.R;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class AboutUs extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Method.forceRTLIfSupported(getWindow(), AboutUs.this);

        method = new Method(AboutUs.this);

        toolbar = findViewById(R.id.toolbar_about_us);
        toolbar.setTitle(getResources().getString(R.string.about_us));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView_app_name = findViewById(R.id.textView_app_name_about_us);
        TextView textView_app_version = findViewById(R.id.textView_app_version_about_us);
        TextView textView_app_author = findViewById(R.id.textView_app_author_about_us);
        TextView textView_app_contact = findViewById(R.id.textView_app_contact_about_us);
        TextView textView_app_email = findViewById(R.id.textView_app_email_about_us);
        TextView textView_app_website = findViewById(R.id.textView_app_website_about_us);
        WebView webView = findViewById(R.id.webView_about_us);

        ImageView app_logo = findViewById(R.id.app_logo_about_us);

        linearLayout = findViewById(R.id.linearLayout_about_us);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout, AboutUs.this);
        } else {
            method.showNonPersonalizedAds(linearLayout, AboutUs.this);
        }

        if (Constant_Api.aboutUsList != null) {

            textView_app_name.setText(Constant_Api.aboutUsList.getApp_name());

            Picasso.get().load(Constant_Api.image + Constant_Api.aboutUsList.getApp_logo())
                    .placeholder(R.drawable.about_logo)
                    .into(app_logo);

            textView_app_version.setText(Constant_Api.aboutUsList.getApp_version());
            textView_app_author.setText(Constant_Api.aboutUsList.getApp_author());
            textView_app_contact.setText(Constant_Api.aboutUsList.getApp_contact());
            textView_app_email.setText(Constant_Api.aboutUsList.getApp_email());
            textView_app_website.setText(Constant_Api.aboutUsList.getApp_website());

            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.setFocusableInTouchMode(false);
            webView.setFocusable(false);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            String mimeType = "text/html";
            String encoding = "utf-8";
            String htmlText = Constant_Api.aboutUsList.getApp_description();

            String text = "<html><head>"
                    + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/IRANSansMobile_Medium.ttf\")}body{font-family: MyFont;color: #8b8b8b;text-align:justify; direction:rtl;line-height:1.6}"
                    + "</style></head>"
                    + "<body>"
                    + htmlText
                    + "</body></html>";

            webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
