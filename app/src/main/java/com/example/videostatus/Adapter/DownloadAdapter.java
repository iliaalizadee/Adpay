package com.example.videostatus.Adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.example.videostatus.Activity.VideoPlayer;
import com.example.videostatus.DataBase.DatabaseHandler;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.Method;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private DatabaseHandler db;
    private int columnWidth;
    private List<SubCategoryList> downloadLists;

    public DownloadAdapter(Activity activity, List<SubCategoryList> subCategoryLists) {
        this.activity = activity;
        method = new Method(activity);
        db = new DatabaseHandler(activity);
        columnWidth = (method.getScreenWidth());
        this.downloadLists = subCategoryLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.download_adapter, parent, false);

        return new DownloadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView_name.setText(downloadLists.get(position).getVideo_title());
        holder.textView_subName.setText(downloadLists.get(position).getCategory_name());

        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        Picasso.get().load("file://" + downloadLists.get(position).getVideo_thumbnail_b()).into(holder.imageView);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, VideoPlayer.class);
                intent.putExtra("Video_url", downloadLists.get(position).getVideo_url());
                intent.putExtra("video_type", downloadLists.get(position).getVideo_layout());
                activity.startActivity(intent);
            }
        });

        holder.imageView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.delete_video_download(downloadLists.get(position).getId());
                File file = new File(downloadLists.get(position).getVideo_url());
                File file_image = new File(downloadLists.get(position).getVideo_thumbnail_b());
                file_image.delete();
                file.delete();
                downloadLists.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return downloadLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private RelativeLayout relativeLayout;
        private TextView textView_name, textView_subName;
        private ImageView imageView_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_imageView_download_adapter);
            textView_name = itemView.findViewById(R.id.textView_title_download_adapter);
            textView_subName = itemView.findViewById(R.id.textView_sub_title_download_adapter);
            imageView = itemView.findViewById(R.id.imageView_download_adapter);
            imageView_delete = itemView.findViewById(R.id.imageView_delete_download_adapter);

        }
    }
}
