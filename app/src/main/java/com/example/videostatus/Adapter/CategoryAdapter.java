package com.example.videostatus.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.videostatus.InterFace.InterstitialAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.example.videostatus.Item.CategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.Method;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private int columnWidth;
    private String string;
    private List<CategoryList> categoryLists;

    public CategoryAdapter(Activity activity, List<CategoryList> categoryLists, String string, InterstitialAdView interstitialAdView) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        columnWidth = (method.getScreenWidth());
        this.string = string;
        this.categoryLists = categoryLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.category_adapter, parent, false);

        return new CategoryAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 2, columnWidth / 4));
        holder.view_layout.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth / 2, columnWidth / 4));

        if (!categoryLists.get(position).getCategory_image_thumb().equals("")) {
            Picasso.get().load(categoryLists.get(position).getCategory_image_thumb()).into(holder.imageView);
        }
        holder.textView.setText(categoryLists.get(position).getCategory_name() + " "
                + "(" + categoryLists.get(position).getCat_total_video() + ")");

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, string, "");
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private TextView textView;
        private View view_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_category_adapter);
            textView = itemView.findViewById(R.id.textView_category_adapter);
            view_layout = itemView.findViewById(R.id.view_category_adapter);

        }
    }
}
