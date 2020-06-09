package com.example.videostatus.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videostatus.Activity.admobactivity;
import com.example.videostatus.Activity.checktelinsta;
import com.example.videostatus.Activity.raykaactivity;
import com.example.videostatus.Activity.tapsellactivity;
import com.example.videostatus.Fragment.AdmobMagnetFragment;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.PrefManager;
import com.example.videostatus.models.watch;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class AdmobRecyclerAdapter extends RecyclerView.Adapter<AdmobRecyclerAdapter.viewholder> {
    List<watch> list;
    Activity activity;
    PrefManager manager;
    int interval = 0;

    public AdmobRecyclerAdapter(List<watch> list, Activity activity){
        this.list=list;
        this.activity=activity;
    }
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_list_adapter,parent,false);

        return new viewholder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        watch watchgroup = list.get(position);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name","get_ad_interval");
        params.put("data",API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        interval = object.getInt("interval");

                    }
                }catch (JSONException ex){

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        manager = new PrefManager(activity);
        holder.textView.setText(watchgroup.getTitle());
        holder.button.setText(watchgroup.getButtonNum());
        if((!watchgroup.getImageURI().equals("")) || watchgroup.getImageURI() != null){
            Glide.with(activity).load(watchgroup.getImageURI()).into(holder.imageview);
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (watchgroup.getTag()){
                    case "admob":
                        {
                            if(manager.getShowAlert() == true){
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setCancelable(true)
                                        .setMessage("برای مشاهده آگهی ادموب به فیلترشکن نیاز دارید")
                                        .setTitle("توجه")
                                        .setPositiveButton("ادامه", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                 init(v);
                                            }
                                        }).setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                }).setNeutralButton("دیگر نشان نده", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        manager.setShowAlert(false);
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }else{
                                init(v);
                            }

                            break;
                    }
                    case "tapsell":
                        {
                        Intent intent = new Intent(activity, tapsellactivity.class);
                        activity.startActivity(intent);
                            v.setEnabled(false);

                            Timer buttonTimer = new Timer();
                            buttonTimer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                        }
                                    });
                                }
                            }, interval*1000);
                        break;
                    }
                    case "rayka":
                    {
                        Intent intent = new Intent(activity, raykaactivity.class);
                        activity.startActivity(intent);
                        v.setEnabled(false);

                        Timer buttonTimer = new Timer();
                        buttonTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        v.setEnabled(true);
                                    }
                                });
                            }
                        }, interval*1000);
                        break;
                    }
                    case "tel":{
                        Intent intent = new Intent(activity, checktelinsta.class);
                        activity.startActivity(intent);
                    }
                    case "insta":{
                        Intent intent = new Intent(activity,checktelinsta.class);
                        activity.startActivity(intent);
                    }


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
        ImageView imageview;
        TextView textView;
        Button button;


        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.admob_image_view);
            button = itemView.findViewById(R.id.admob_button);
            textView = itemView.findViewById(R.id.admob_textview);
        }
    }
    public void init(View v){
        Intent intent = new Intent(activity, admobactivity.class);
        activity.startActivity(intent);
        v.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                });

            }
        }, interval*1000);
    }
}
