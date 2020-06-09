package com.example.videostatus.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.videostatus.InterFace.InterstitialAdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.example.videostatus.Activity.MainActivity;
import com.example.videostatus.Adapter.PortraitHomeAdapter;
import com.example.videostatus.Adapter.SubCategoryAdapter;
import com.example.videostatus.Item.SubCategoryList;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.EndlessRecyclerViewScrollListener;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private Method method;
    private SliderLayout mDemoSlider;
    private ProgressBar progressBar;
    private RecyclerView recyclerView_portrait, recyclerView_landscape;
    private PortraitHomeAdapter portraitHomeAdapter;
    private SubCategoryAdapter landscapeHomeAdapter;
    private TextView textView_noData;
    private RelativeLayout relativeLayout;
    private List<SubCategoryList> portraitArray;
    private List<SubCategoryList> landscapeArray;
    private List<SubCategoryList> sliderArray;
    private InterstitialAdView interstitialAdView;
    private LayoutAnimationController animation;
    private Boolean isOver = false;
    private int pagination_index = 1;
    private int j = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container, false);

        GlobalBus.getBus().register(this);

        HomeMainFragment.floating_home.setVisibility(View.GONE);

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {

                if (type.equals("home_sub")) {
                    String title = landscapeArray.get(position).getVideo_title();
                    SCDetailFragment scDetailFragment = new SCDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("type", type);
                    bundle.putInt("position", position);
                    scDetailFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, title).addToBackStack(title).commitAllowingStateLoss();
                } else if (type.equals(getResources().getString(R.string.portrait_status))) {
                    String title = portraitArray.get(position).getVideo_title();
                    SCDetailFragment scDetailFragment = new SCDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("type", type);
                    bundle.putInt("position", position);
                    scDetailFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, title).addToBackStack(title).commitAllowingStateLoss();
                }
            }
        };
        method = new Method(getActivity(), interstitialAdView, null, null);

        int columnWidth = method.getScreenWidth();
        portraitArray = new ArrayList<>();
        landscapeArray = new ArrayList<>();
        sliderArray = new ArrayList<>();

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        mDemoSlider = view.findViewById(R.id.custom_indicator_home_fragment);
        mDemoSlider.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2 + 100));

        textView_noData = view.findViewById(R.id.textView_home_fragment);
        relativeLayout = view.findViewById(R.id.relativeLayout_home_adapter);
        progressBar = view.findViewById(R.id.progressbar_home_fragment);
        recyclerView_portrait = view.findViewById(R.id.recyclerView_portrait_home_fragment);
        recyclerView_landscape = view.findViewById(R.id.recyclerView_landscape_home_fragment);
        Button button_portrait = view.findViewById(R.id.button_portrait_home_fragment);

        relativeLayout.setVisibility(View.GONE);
        textView_noData.setVisibility(View.GONE);

        recyclerView_portrait.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_portrait.setLayoutManager(layoutManager);
        recyclerView_portrait.setFocusable(false);
        recyclerView_portrait.setNestedScrollingEnabled(false);

        recyclerView_landscape.setHasFixedSize(true);
        LinearLayoutManager layoutManager_landscape = new LinearLayoutManager(getContext());
        recyclerView_landscape.setLayoutManager(layoutManager_landscape);
        recyclerView_landscape.setFocusable(false);
        recyclerView_landscape.setNestedScrollingEnabled(false);

        recyclerView_landscape.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager_landscape) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pagination_index++;
                            callData();
                        }
                    }, 1000);
                } else {
                    landscapeHomeAdapter.hideHeader();
                }
            }
        });

        button_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = getResources().getString(R.string.portrait_status);
                Method.search_title = tag;
                MainActivity.toolbar.setTitle(tag);
                PortraitFragment portraitFragment = new PortraitFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", tag);
                portraitFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, portraitFragment, tag).addToBackStack(tag).commitAllowingStateLoss();
            }
        });

        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                home(method.pref.getString(method.profileId, null));
            } else {
                home("0");
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void callData() {
        if (getActivity() != null) {
            if (Method.isNetworkAvailable(getActivity())) {
                if (method.pref.getBoolean(method.pref_login, false)) {
                    landscape(method.pref.getString(method.profileId, null));
                } else {
                    landscape("0");
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    @Subscribe
    public void getNotify(Events.HomeNotify homeNotify) {
        if (portraitHomeAdapter != null && landscapeHomeAdapter != null) {
            portraitHomeAdapter.notifyDataSetChanged();
            landscapeHomeAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void getMessage(Events.HomeSubCatNotify homeSubCatNotify) {
        if (landscapeHomeAdapter != null) {
            int position = homeSubCatNotify.getPosition();
            switch (homeSubCatNotify.getType()) {
                case "all":
                    landscapeArray.get(position).setTotal_viewer(homeSubCatNotify.getView());
                    landscapeArray.get(position).setTotal_likes(homeSubCatNotify.getTotalLike());
                    landscapeArray.get(position).setAlready_like(homeSubCatNotify.getAlreadyLike());
                    break;
                case "view":
                    landscapeArray.get(position).setTotal_viewer(homeSubCatNotify.getView());
                    break;
                case "like":
                    landscapeArray.get(position).setTotal_likes(homeSubCatNotify.getTotalLike());
                    landscapeArray.get(position).setAlready_like(homeSubCatNotify.getAlreadyLike());
                    break;
            }
            landscapeHomeAdapter.notifyDataSetChanged();
        }
    }

    private void home(String userId) {

        sliderArray.clear();
        portraitArray.clear();
        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "home_videos");
        jsObj.addProperty("user_id", userId);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (getActivity() != null) {

                    Log.d("Response", new String(responseBody));
                    String res = new String(responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(res);

                        JSONObject object = jsonObject.getJSONObject(Constant_Api.tag);

                        JSONArray jsonArray_slider = object.getJSONArray("featured_video");

                        for (int i = 0; i < jsonArray_slider.length(); i++) {

                            JSONObject object_slider = jsonArray_slider.getJSONObject(i);
                            String id = object_slider.getString("id");
                            String cat_id = object_slider.getString("cat_id");
                            String video_title = object_slider.getString("video_title");
                            String video_url = object_slider.getString("video_url");
                            String video_layout = object_slider.getString("video_layout");
                            String video_thumbnail_b = object_slider.getString("video_thumbnail_b");
                            String video_thumbnail_s = object_slider.getString("video_thumbnail_s");
                            String total_likes = object_slider.getString("total_likes");
                            String total_viewer = object_slider.getString("totel_viewer");
                            String category_name = object_slider.getString("category_name");
                            String already_like = object_slider.getString("already_like");

                            sliderArray.add(new SubCategoryList("", id, cat_id, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like));

                        }

                        JSONArray jsonArray_portrait = object.getJSONArray("portrait_video");

                        for (int i = 0; i < jsonArray_portrait.length(); i++) {

                            JSONObject object_portrait = jsonArray_portrait.getJSONObject(i);
                            String id = object_portrait.getString("id");
                            String cat_id = object_portrait.getString("cat_id");
                            String video_title = object_portrait.getString("video_title");
                            String video_url = object_portrait.getString("video_url");
                            String video_layout = object_portrait.getString("video_layout");
                            String video_thumbnail_b = object_portrait.getString("video_thumbnail_b");
                            String video_thumbnail_s = object_portrait.getString("video_thumbnail_s");
                            String total_likes = object_portrait.getString("total_likes");
                            String total_viewer = object_portrait.getString("totel_viewer");
                            String category_name = object_portrait.getString("category_name");
                            String already_like = object_portrait.getString("already_like");

                            portraitArray.add(new SubCategoryList("", id, cat_id, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like));

                        }

                        for (int i = 0; i < sliderArray.size(); i++) {
                            TextSliderView textSliderView = new TextSliderView(getActivity());
                            // initialize a SliderLayout
                            final int finalI = i;
                            textSliderView
                                    .name(sliderArray.get(i).getVideo_title())
                                    .sub_name(method.format(Double.parseDouble(sliderArray.get(i).getTotal_viewer())) + " " + getActivity().getResources().getString(R.string.view))
                                    .image(sliderArray.get(i).getVideo_thumbnail_b())
                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {

                                            String title = sliderArray.get(finalI).getVideo_title();

                                            SCDetailFragment scDetailFragment = new SCDetailFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", sliderArray.get(mDemoSlider.getCurrentPosition()).getId());
                                            bundle.putString("type", "slider");
                                            bundle.putInt("position", mDemoSlider.getCurrentPosition());
                                            scDetailFragment.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, title)
                                                    .addToBackStack(title).commitAllowingStateLoss();
                                        }
                                    })
                                    .setScaleType(BaseSliderView.ScaleType.Fit);
                            mDemoSlider.addSlider(textSliderView);
                        }

                        //mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
                        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
                        mDemoSlider.getPagerIndicator().setDefaultIndicatorColor(getResources().getColor(R.color.selectedColor)
                                , getResources().getColor(R.color.unselectedColor));
                        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                        //mDemoSlider.setDuration(4000);

                        portraitHomeAdapter = new PortraitHomeAdapter(getActivity(), portraitArray, interstitialAdView, getResources().getString(R.string.portrait_status));
                        recyclerView_portrait.setAdapter(portraitHomeAdapter);
                        progressBar.setVisibility(View.GONE);

                        callData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        textView_noData.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                textView_noData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void landscape(String userId) {

        if (landscapeHomeAdapter == null) {
            landscapeArray.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "landscape_videos");
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("page", pagination_index);
        client.cancelAllRequests(true);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (getActivity() != null) {

                    Log.d("Response", new String(responseBody));
                    String res = new String(responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(res);

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String cat_id = object.getString("cat_id");
                            String video_title = object.getString("video_title");
                            String video_url = object.getString("video_url");
                            String video_layout = object.getString("video_layout");
                            String video_thumbnail_b = object.getString("video_thumbnail_b");
                            String video_thumbnail_s = object.getString("video_thumbnail_s");
                            String total_likes = object.getString("total_likes");
                            String total_viewer = object.getString("totel_viewer");
                            String category_name = object.getString("category_name");
                            String already_like = object.getString("already_like");

                            if (j % Constant_Api.AD_LIST_VIEW == 0) {
                                landscapeArray.add(new SubCategoryList("ad", "", "", "", "", "", "", "", "", "", "", ""));
                                j++;
                            }
                            landscapeArray.add(new SubCategoryList("", id, cat_id, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like));
                            j++;

                        }

                        if (jsonArray.length() == 0) {
                            if (landscapeHomeAdapter != null) {
                                isOver = true;
                                landscapeHomeAdapter.hideHeader();
                            }
                        }

                        if (landscapeHomeAdapter == null) {
                            if (landscapeArray.size() != 0) {
                                landscapeHomeAdapter = new SubCategoryAdapter(getActivity(), landscapeArray, interstitialAdView, "home_sub");
                                recyclerView_landscape.setAdapter(landscapeHomeAdapter);
                                recyclerView_landscape.setLayoutAnimation(animation);
                                textView_noData.setVisibility(View.GONE);
                            } else {
                                textView_noData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            landscapeHomeAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        isOver = true;
                        progressBar.setVisibility(View.GONE);
                        textView_noData.setVisibility(View.VISIBLE);
                        if (landscapeHomeAdapter != null) {
                            isOver = true;
                            landscapeHomeAdapter.hideHeader();
                            textView_noData.setVisibility(View.GONE);
                        }
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                textView_noData.setVisibility(View.VISIBLE);
            }
        });
    }


    @Subscribe
    public void getSlider_notify(Events.FragmentSliderNotify fragmentSliderNotify) {

        int position = fragmentSliderNotify.getPosition();
        sliderArray.get(position).setTotal_viewer(fragmentSliderNotify.getSlider_notify());

        if (mDemoSlider != null) {
            mDemoSlider.removeAllSliders();
            for (int i = 0; i < sliderArray.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout
                final int finalI = i;
                textSliderView
                        .name(sliderArray.get(i).getVideo_title())
                        .sub_name(method.format(Double.parseDouble(sliderArray.get(i).getTotal_viewer())) + " " + getResources().getString(R.string.view))
                        .image(sliderArray.get(i).getVideo_thumbnail_b())
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {

                                String title = sliderArray.get(finalI).getVideo_title();

                                SCDetailFragment scDetailFragment = new SCDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", sliderArray.get(mDemoSlider.getCurrentPosition()).getId());
                                bundle.putString("type", "slider");
                                bundle.putInt("position", mDemoSlider.getCurrentPosition());
                                scDetailFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, title)
                                        .addToBackStack(title).commitAllowingStateLoss();
                            }
                        })
                        .setScaleType(BaseSliderView.ScaleType.Fit);
                mDemoSlider.addSlider(textSliderView);
            }

            //mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
            mDemoSlider.getPagerIndicator().setDefaultIndicatorColor(getResources().getColor(R.color.selectedColor)
                    , getResources().getColor(R.color.unselectedColor));
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            //mDemoSlider.setDuration(4000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
