package com.example.videostatus.Fragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videostatus.Activity.AboutUs;
import com.example.videostatus.Activity.ContactUs;
import com.example.videostatus.Activity.Faq;
import com.example.videostatus.Activity.MainActivity;
import com.example.videostatus.Activity.PrivacyPolice;
import com.example.videostatus.R;
import com.example.videostatus.Util.Method;
import com.onesignal.OneSignal;

import java.io.File;
import java.text.DecimalFormat;


public class SettingFragment extends Fragment {

    private Method method;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_fragment, container, false);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.setting));

        method = new Method(getActivity());

        SwitchCompat switchCompat = view.findViewById(R.id.switch_setting);
        TextView textView_shareApp = view.findViewById(R.id.textView_shareApp_setting);
        TextView textView_rateApp = view.findViewById(R.id.textView_rateApp_setting);
        TextView textView_moreApp = view.findViewById(R.id.textView_moreApp_setting);
        TextView textView_privacy_policy = view.findViewById(R.id.textView_privacy_policy_setting);
        TextView textView_aboutUs = view.findViewById(R.id.textView_aboutUs_setting);
        TextView textView_contactUs = view.findViewById(R.id.textView_contactUs_setting);
        TextView textView_faq = view.findViewById(R.id.textView_faq_setting);
        final TextView textViewSize = view.findViewById(R.id.textView_size_setting);
        ImageView imageView_clear = view.findViewById(R.id.imageView_clear_setting);

        double total = 0;
        String root = getActivity().getExternalCacheDir().getAbsolutePath();
        try {
            File file = new File(root);
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    File name = new File(root + "/" + aChildren);
                    total += getFileSizeMegaBytes(name);
                }
            }
        } catch (Exception e) {
            textViewSize.setText("Size " + "0.0" + " mb");
        }
        textViewSize.setText(getResources().getString(R.string.size) + " "
                + new DecimalFormat("##.##").format(total) + " "
                + getResources().getString(R.string.mb));

        imageView_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String root = getActivity().getExternalCacheDir().getAbsolutePath();
                File file = new File(root);
                if (file.isDirectory()) {
                    String[] children = file.list();
                    for (String aChildren : children) {
                        new File(file, aChildren).delete();
                    }
                    Toast.makeText(getActivity(), getResources().getString(R.string.clear_cache_succss), Toast.LENGTH_SHORT).show();
                    textViewSize.setText(getResources().getString(R.string.size) + " "
                            + "0.0" + " "
                            + getResources().getString(R.string.mb));
                }
            }
        });

        if (method.pref.getBoolean(method.notification, true)) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OneSignal.setSubscription(true);
                } else {
                    OneSignal.setSubscription(false);
                }
                method.editor.putBoolean(method.notification, isChecked);
                method.editor.commit();
            }
        });

        textView_shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

        textView_rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
            }
        });

        textView_moreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreApp();
            }
        });

        textView_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutUs.class));
            }
        });

        textView_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrivacyPolice.class));
            }
        });

        textView_contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactUs.class));
            }
        });

        textView_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Faq.class));
            }
        });

        return view;

    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName())));
        }
    }

    private void moreApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_more_app))));
    }

    private void shareApp() {

        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String sAux = "\n" + getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }

    }

    private static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }

}
