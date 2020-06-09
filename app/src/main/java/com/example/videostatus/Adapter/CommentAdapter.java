package com.example.videostatus.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Activity activity;
    private List<SubCategoryList> scdLists;

    public CommentAdapter(Activity activity, List<SubCategoryList> scdLists) {
        this.activity = activity;
        this.scdLists = scdLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!scdLists.get(0).getCommentLists().get(position).getUser_image().equals("")) {
            Glide.with(activity).load(scdLists.get(0).getCommentLists().get(position).getUser_image()).into(holder.circleImageView);
        }

        holder.textView_Name.setText(scdLists.get(0).getCommentLists().get(position).getUser_name());
        holder.textView_date.setText(scdLists.get(0).getCommentLists().get(position).getComment_date());
        holder.textView_comment.setText(scdLists.get(0).getCommentLists().get(position).getComment_text());

    }

    @Override
    public int getItemCount() {
        if (scdLists.get(0).getCommentLists().size() == 0) {
            return 0;
        } else if (scdLists.get(0).getCommentLists().size() == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView textView_Name, textView_date, textView_comment;

        public ViewHolder(View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.imageView_comment_adapter);
            textView_Name = itemView.findViewById(R.id.textView_userName_comment_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_comment_adapter);
            textView_comment = itemView.findViewById(R.id.textView_comment_adapter);
        }
    }
}
