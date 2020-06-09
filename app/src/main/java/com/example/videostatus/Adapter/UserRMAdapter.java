package com.example.videostatus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.videostatus.InterFace.InterstitialAdView;
import com.example.videostatus.Item.UserRMList;
import com.example.videostatus.R;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Method;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserRMAdapter extends RecyclerView.Adapter<UserRMAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private String type;
    private List<UserRMList> userRMLists;

    public UserRMAdapter(Activity activity, List<UserRMList> rewardPointLists, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        this.userRMLists = rewardPointLists;
        this.type = type;
        method = new Method(activity, interstitialAdView);
    }

    @NonNull
    @Override
    public UserRMAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_rm_adapter, parent, false);

        return new UserRMAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserRMAdapter.ViewHolder holder, final int position) {

        holder.textView_point.setText(activity.getResources().getString(R.string.user_point) + " " + userRMLists.get(position).getUser_points());
        holder.textView_date.setText(userRMLists.get(position).getRequest_date());

        if (Constant_Api.aboutUsList != null) {
            holder.textView_price.setText(userRMLists.get(position).getRedeem_price() + " " + Constant_Api.aboutUsList.getRedeem_currency());
        } else {
            holder.textView_price.setText(userRMLists.get(position).getRedeem_price());
        }

        switch (userRMLists.get(position).getStatus()) {
            case "0":
                holder.textView_status.setText(activity.getResources().getString(R.string.pending));
                holder.relativeLayout_status.setBackground(activity.getResources().getDrawable(R.drawable.button_background));
                break;
            case "1":
                holder.textView_status.setText(activity.getResources().getString(R.string.approve));
                holder.relativeLayout_status.setBackground(activity.getResources().getDrawable(R.drawable.approve_bg));
                break;
            default:
                holder.textView_status.setText(activity.getResources().getString(R.string.reject));
                holder.relativeLayout_status.setBackground(activity.getResources().getDrawable(R.drawable.reject_bg));
                break;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            float scale = activity.getResources().getDisplayMetrics().density;
            int dpAsPixels_left = (int) (15 * scale + 0.5f);
            int dpAsPixels_top = (int) (8 * scale + 0.5f);

            holder.relativeLayout_status.setPadding(dpAsPixels_left, dpAsPixels_top, dpAsPixels_left, dpAsPixels_top);

        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, activity.getResources().getString(R.string.reward_point), userRMLists.get(position).getRedeem_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userRMLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout, relativeLayout_status;
        private TextView textView_point, textView_date, textView_price, textView_status;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_rm_adapter);
            relativeLayout_status = itemView.findViewById(R.id.relativeLayout_status_rm_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_rm_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_rm_adapter);
            textView_price = itemView.findViewById(R.id.textView_price_rm_adapter);
            textView_status = itemView.findViewById(R.id.textView_status_rm_adapter);

        }
    }
}
