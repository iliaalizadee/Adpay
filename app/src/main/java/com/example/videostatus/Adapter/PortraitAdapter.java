package com.example.videostatus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.videostatus.InterFace.InterstitialAdView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.example.videostatus.DataBase.DatabaseHandler;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PortraitAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private DatabaseHandler db;
    private String type;
    private List<SubCategoryList> subCategoryLists;

    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_Ad = 2;

    public PortraitAdapter(Activity activity, List<SubCategoryList> subCategoryLists, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        db = new DatabaseHandler(activity);
        this.subCategoryLists = subCategoryLists;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.portrait_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_Ad) {
            View view = LayoutInflater.from(activity).inflate(R.layout.ad_adapter, parent, false);
            return new AdOption(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (db.checkId_Fav(subCategoryLists.get(position).getId())) {
                viewHolder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
            } else {
                viewHolder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
            }

            Picasso.get().load(subCategoryLists.get(position).getVideo_thumbnail_b())
                    .placeholder(R.drawable.placeholder_landscape).into(viewHolder.imageView);

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    method.interstitialAdShow(position, type, subCategoryLists.get(position).getId());
                }
            });

            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (db.checkId_Fav(subCategoryLists.get(position).getId())) {
                        method.addToFav(db, subCategoryLists, position);
                        viewHolder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
                    } else {
                        db.deleteFav(subCategoryLists.get(position).getId());
                        viewHolder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
                    }
                    Events.HomeNotify homeNotify = new Events.HomeNotify("");
                    GlobalBus.getBus().post(homeNotify);

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
                        adView.setAdSize(AdSize.BANNER);
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
        return subCategoryLists.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0) {
            if (subCategoryLists.size() == position) {
                return VIEW_TYPE_LOADING;
            } else if (subCategoryLists.get(position).getAdView().equals("ad")) {
                return VIEW_TYPE_Ad;
            } else {
                return VIEW_TYPE_ITEM;
            }
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void hideHeader() {
        PortraitAdapter.ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private ImageView imageView, imageView_favourite;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_portrait_adapter);
            imageView_favourite = itemView.findViewById(R.id.imageView_fav_portrait_adapter);
            relativeLayout = itemView.findViewById(R.id.relativeLayout_fav_portrait_adapter);

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

}
