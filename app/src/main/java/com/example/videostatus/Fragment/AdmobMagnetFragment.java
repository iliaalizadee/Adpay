package com.example.videostatus.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostatus.Activity.tapsellactivity;
import com.example.videostatus.Adapter.AdmobRecyclerAdapter;
import com.example.videostatus.R;
import com.example.videostatus.models.watch;

import java.util.ArrayList;
import java.util.List;

import ir.tapsell.plus.TapsellPlus;

public class AdmobMagnetFragment extends Fragment {
    List<watch> watchlist = new ArrayList<watch>();
    AdmobRecyclerAdapter adapter;
    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admob_magnet,container,false);
        recyclerView = view.findViewById(R.id.admob_recycler_view);
        adapter = new AdmobRecyclerAdapter(watchlist,getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        watch admob_item= new watch("admobimage","ادموب","مشاهده تبلیغ","admob");
        watchlist.add(admob_item);
        watch tapsell_item= new watch("tapsellimage","تپسل","مشاهده تبلیغ","tapsell");
        watchlist.add(tapsell_item);
        watch tel_item = new watch("telegramimage","عضویت در کانال تلگرام","","tel");
        watch insta_item = new watch("instaimage","فالو کردن پیج اینستاگرام","","insta");
        adapter.notifyDataSetChanged();





        return view;
    }
}
