package com.example.videostatus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videostatus.InterFace.InterstitialAdView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;


public class MyVideoAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private int columnWidth;
    private String userId, type;
    private ProgressDialog progressDialog;
    private List<SubCategoryList> myVideoLists;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_Ad = 2;

    public MyVideoAdapter(Activity activity, List<SubCategoryList> myVideoLists, String userId, String type, InterstitialAdView interstitialAdView) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        columnWidth = (method.getScreenWidth());
        this.userId = userId;
        this.type = type;
        this.myVideoLists = myVideoLists;
        progressDialog = new ProgressDialog(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.my_video_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_Ad) {
            View view = LayoutInflater.from(activity).inflate(R.layout.ad_adapter, parent, false);
            return new MyVideoAdapter.AdOption(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof ViewHolder) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (userId.equals(method.pref.getString(method.profileId, null))) {
                viewHolder.imageView_delete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageView_delete.setVisibility(View.GONE);
            }

            viewHolder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));

            Picasso.get().load(myVideoLists.get(position).getVideo_thumbnail_b())
                    .placeholder(R.drawable.placeholder_landscape)
                    .into(viewHolder.imageView);

            viewHolder.textView_title.setText(myVideoLists.get(position).getVideo_title());
            viewHolder.textView_sub_title.setText(myVideoLists.get(position).getCategory_name());
            viewHolder.textView_like.setText(method.format(Double.parseDouble(myVideoLists.get(position).getTotal_likes())));
            viewHolder.textView_view.setText(method.format(Double.parseDouble(myVideoLists.get(position).getTotal_viewer())));

            if (myVideoLists.get(position).getAlready_like().equals("true")) {
                viewHolder.imageView_like.setImageDrawable(activity.getResources().getDrawable(R.drawable.like_video_hov));
            } else {
                viewHolder.imageView_like.setImageDrawable(activity.getResources().getDrawable(R.drawable.like_video));
            }

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    method.interstitialAdShow(position, type, myVideoLists.get(position).getId());
                }
            });

            viewHolder.imageView_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Method.isNetworkAvailable(activity)) {
                        delete_video(position, userId);
                    } else {
                        Toast.makeText(activity, activity.getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (holder.getItemViewType() == VIEW_TYPE_Ad) {

            AdOption adOption = (AdOption) holder;

            if (Constant_Api.aboutUsList != null) {
                if (Constant_Api.aboutUsList.isBanner_ad()) {
                    if (adOption.linearLayout.getChildCount() == 0) {
                        AdRequest adRequest;
                        AdView adView = new AdView(activity);
                        if (method.personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        adView.setAdUnitId(Constant_Api.aboutUsList.getBanner_ad_id());
                        adView.setAdSize(AdSize.SMART_BANNER);
                        adOption.linearLayout.addView(adView);
                        adView.loadAd(adRequest);
                    }
                } else {
                    adOption.linearLayout.setVisibility(View.GONE);
                }
            } else {
                adOption.linearLayout.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (myVideoLists.size() != 0) {
            return myVideoLists.size() + 1;
        } else {
            return myVideoLists.size();
        }
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {

        if (position != 0) {
            if (myVideoLists.size() == position) {
                return VIEW_TYPE_LOADING;
            } else if (myVideoLists.get(position).getAdView().equals("ad")) {
                return VIEW_TYPE_Ad;
            } else {
                return VIEW_TYPE_ITEM;
            }
        } else {
            return VIEW_TYPE_ITEM;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private ImageView imageView_like, imageView_delete;
        private TextView textView_title, textView_like, textView_sub_title, textView_view;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_my_video_adapter);
            imageView_like = itemView.findViewById(R.id.imageView_like_myVideo_adapter);
            imageView_delete = itemView.findViewById(R.id.imageView_delete_myVideo_adapter);
            textView_title = itemView.findViewById(R.id.textView_title_myVideo_adapter);
            textView_sub_title = itemView.findViewById(R.id.textView_subtitle_myVideo_adapter);
            textView_view = itemView.findViewById(R.id.textView_view_myVideo_adapter);
            textView_like = itemView.findViewById(R.id.textView_like_myVideo_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public class AdOption extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;

        public AdOption(View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout_ad_adapter);

        }

    }

    private void delete_video(final int position, String userId) {

        progressDialog.setTitle(activity.getResources().getString(R.string.delete));
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_video_delete");
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("video_id", myVideoLists.get(position).getId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

                int m = 1;

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String msg = object.getString("msg");
                        String success = object.getString("success");

                        if (success.equals("1")) {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            Events.UserVideo userVideo = new Events.UserVideo("Landscape");
                            GlobalBus.getBus().post(userVideo);
                        } else {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
            }
        });
    }

}
