package com.example.videostatus.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.example.videostatus.Activity.VideoPlayer;
import com.example.videostatus.DataBase.DatabaseHandler;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.Method;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DownloadPortraitAdapter extends RecyclerView.Adapter<DownloadPortraitAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private DatabaseHandler db;
    private int columnWidth;
    private Animation myAnim;
    private List<SubCategoryList> downloadLists;

    public DownloadPortraitAdapter(Activity activity, List<SubCategoryList> subCategoryLists) {
        this.activity = activity;
        method = new Method(activity);
        db = new DatabaseHandler(activity);
        columnWidth = (method.getScreenWidth());
        this.downloadLists = subCategoryLists;
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.download_portrait_adapter, parent, false);

        return new DownloadPortraitAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Picasso.get().load("file://" + downloadLists.get(position).getVideo_thumbnail_b()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, VideoPlayer.class);
                intent.putExtra("Video_url", downloadLists.get(position).getVideo_url());
                intent.putExtra("video_type", downloadLists.get(position).getVideo_layout());
                activity.startActivity(intent);
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageView_delete.startAnimation(myAnim);
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
        private ImageView imageView_delete;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_download_porAdapter);
            imageView_delete = itemView.findViewById(R.id.imageView_delete_download_porAdapter);
            relativeLayout = itemView.findViewById(R.id.relativeLayout_download_porAdapter);

        }
    }
}
