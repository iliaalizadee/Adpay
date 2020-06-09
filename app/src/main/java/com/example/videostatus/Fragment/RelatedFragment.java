package com.example.videostatus.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.videostatus.Adapter.FavAdapter;
import com.example.videostatus.Adapter.FavPortraitAdapter;
import com.example.videostatus.InterFace.InterstitialAdView;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RelatedFragment extends Fragment {

    private String typeLayout;
    private FavAdapter relatedAdapter;
    private FavPortraitAdapter relatedAdapterPortrait;
    private List<SubCategoryList> relatedList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        GlobalBus.getBus().register(this);

        Method.search_title = getResources().getString(R.string.related_video);

        relatedList = new ArrayList<>();

        InterstitialAdView interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                SCDetailFragment scDetailFragment = new SCDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", "related");
                bundle.putInt("position", position);
                scDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, relatedList.get(position).getVideo_title()).addToBackStack(relatedList.get(position).getVideo_title()).commitAllowingStateLoss();
            }
        };

        Method method = new Method(getActivity(), interstitialAdView, null, null);

        assert getArguments() != null;
        String type = getArguments().getString("type");
        typeLayout = getArguments().getString("typeLayout");
        relatedList = (List<SubCategoryList>) getArguments().getSerializable("array");

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        ProgressBar progressBar = view.findViewById(R.id.progressbar_category);
        TextView textView_noData_found = view.findViewById(R.id.textView_category);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_category);
        recyclerView.setHasFixedSize(true);

        if (typeLayout.equals("Landscape")) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            if (relatedList.size() == 0) {
                textView_noData_found.setVisibility(View.VISIBLE);
            } else {
                textView_noData_found.setVisibility(View.GONE);
                relatedAdapter = new FavAdapter(getActivity(), relatedList, interstitialAdView, type);
                recyclerView.setAdapter(relatedAdapter);
                recyclerView.setLayoutAnimation(animation);
            }
        } else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(layoutManager);
            if (relatedList.size() == 0) {
                textView_noData_found.setVisibility(View.VISIBLE);
            } else {
                textView_noData_found.setVisibility(View.GONE);
                relatedAdapterPortrait = new FavPortraitAdapter(getActivity(), relatedList, interstitialAdView, type);
                recyclerView.setAdapter(relatedAdapterPortrait);
                recyclerView.setLayoutAnimation(animation);
            }
        }


        progressBar.setVisibility(View.GONE);

        setHasOptionsMenu(true);
        return view;
    }

    @Subscribe
    public void getNotify(Events.HomeNotify homeNotify) {
        if (typeLayout.equals("Landscape")) {
            if (relatedAdapter != null) {
                relatedAdapter.notifyDataSetChanged();
            }
        } else {
            if (relatedAdapterPortrait != null) {
                relatedAdapterPortrait.notifyDataSetChanged();
            }
        }

    }

    @Subscribe
    public void getMessage(Events.RelatedFragmentNotify relatedFragmentNotify) {
        String type = relatedFragmentNotify.getType();
        int position = relatedFragmentNotify.getPosition();
        switch (type) {
            case "like":
                relatedList.get(position).setTotal_likes(relatedFragmentNotify.getRelated_TotalLike());
                relatedList.get(position).setAlready_like(relatedFragmentNotify.getRelated_alreadyLike());
                break;
            case "view":
                relatedList.get(position).setTotal_viewer(relatedFragmentNotify.getRelated_View());
                break;
            default:
                relatedList.get(position).setTotal_likes(relatedFragmentNotify.getRelated_TotalLike());
                relatedList.get(position).setAlready_like(relatedFragmentNotify.getRelated_alreadyLike());
                relatedList.get(position).setTotal_viewer(relatedFragmentNotify.getRelated_View());
                break;
        }
        if (typeLayout.equals("Landscape")) {
            if (relatedAdapter != null) {
                relatedAdapter.notifyDataSetChanged();
            }
        } else {
            if (relatedAdapterPortrait != null) {
                relatedAdapterPortrait.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
