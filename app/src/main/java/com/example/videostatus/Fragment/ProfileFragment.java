package com.example.videostatus.Fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.example.videostatus.Activity.MainActivity;
import com.example.videostatus.Item.ProfileList;
import com.example.videostatus.Item.UserFollowList;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import cz.msebera.android.httpclient.Header;


public class ProfileFragment extends Fragment {

    private Method method;
    private String user_id, name, email, phone, instagram, youtube, getUser_id;
    private Animation myAnim;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;
    private List<ProfileList> profileLists;
    private Button button_follow;
    private FloatingActionButton fabButton;
    private ImageView imageView_profile, imageView_youtube, imageView_instagram;
    private LinearLayout linearLayout_followings, linearLayout_follower;
    private TextView textViewFollowing, textViewFollower, textView_totalVideo, textViewUserName, textView_noData;
    private boolean isVideo_type = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);

        GlobalBus.getBus().register(this);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));

        profileLists = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());

        assert getArguments() != null;
        String type = getArguments().getString("type");
        getUser_id = getArguments().getString("id");

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        method = new Method(getActivity());

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout_pro);
        textView_noData = view.findViewById(R.id.textView_information_profile);
        progressBar = view.findViewById(R.id.progressbar_profile);
        fabButton = view.findViewById(R.id.fab_profile);
        textViewUserName = view.findViewById(R.id.textView_name_pro);
        imageView_profile = view.findViewById(R.id.imageView_pro);
        imageView_youtube = view.findViewById(R.id.imageView_youtube_pro);
        imageView_instagram = view.findViewById(R.id.imageView_instagram_pro);
        linearLayout_followings = view.findViewById(R.id.linearLayout_followings_pro);
        linearLayout_follower = view.findViewById(R.id.linearLayout_follower_pro);
        textView_totalVideo = view.findViewById(R.id.textView_video_pro);
        textViewFollowing = view.findViewById(R.id.textView_following_pro);
        textViewFollower = view.findViewById(R.id.textView_followers_pro);
        button_follow = view.findViewById(R.id.button_follow_pro);

        progressBar.setVisibility(View.GONE);
        textView_noData.setVisibility(View.GONE);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert type != null;
                if (isVideo_type) {
                    isVideo_type = false;
                    fabButton.setImageDrawable(getResources().getDrawable(R.drawable.landscape_ic));
                    MyVideoFragment myVideoFragment = new MyVideoFragment();
                    Bundle bundle_myVideo = new Bundle();
                    bundle_myVideo.putString("typeLayout", "Landscape");
                    bundle_myVideo.putString("type", "profile_video");
                    bundle_myVideo.putString("id", getUser_id);
                    myVideoFragment.setArguments(bundle_myVideo);
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_profile, myVideoFragment,
                            getResources().getString(R.string.my_video)).commit();
                } else {
                    isVideo_type = true;
                    fabButton.setImageDrawable(getResources().getDrawable(R.drawable.portrait_ic));
                    MyVideoPortraitFragment myVideoPortraitFragment = new MyVideoPortraitFragment();
                    Bundle bundle_myVideo = new Bundle();
                    bundle_myVideo.putString("typeLayout", "Portrait");
                    bundle_myVideo.putString("type", "profile_video");
                    bundle_myVideo.putString("id", getUser_id);
                    myVideoPortraitFragment.setArguments(bundle_myVideo);
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_profile, myVideoPortraitFragment,
                            getResources().getString(R.string.my_video)).commit();
                }
            }
        });

        isVideo_type = false;
        callData();

        setHasOptionsMenu(true);
        return view;

    }

    private void callData() {
        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                if (getActivity() != null) {
                    profile(method.pref.getString(method.profileId, null), getUser_id);
                }
            } else {
                fabButton.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.GONE);
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            fabButton.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.GONE);
            textView_noData.setVisibility(View.VISIBLE);
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void editProfile() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("phone", phone);
        args.putString("instagram", instagram);
        args.putString("youtube", youtube);
        args.putString("user_image", profileLists.get(0).getUser_image());
        args.putString("profileId", method.pref.getString(method.profileId, null));
        editProfileFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, editProfileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commit();
    }

    @Subscribe
    public void getMessage(Events.UserVideo userVideo) {
        if (userVideo.getString().equals("Landscape")) {
            isVideo_type = false;
            fabButton.setImageDrawable(getResources().getDrawable(R.drawable.landscape_ic));
            MyVideoFragment myVideoFragment = new MyVideoFragment();
            Bundle bundle_myVideo = new Bundle();
            bundle_myVideo.putString("typeLayout", "Landscape");
            bundle_myVideo.putString("type", "profile_video");
            bundle_myVideo.putString("id", getUser_id);
            myVideoFragment.setArguments(bundle_myVideo);
            getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_profile, myVideoFragment,
                    getResources().getString(R.string.my_video)).commit();
        } else {
            isVideo_type = true;
            fabButton.setImageDrawable(getResources().getDrawable(R.drawable.portrait_ic));
            MyVideoPortraitFragment myVideoPortraitFragment = new MyVideoPortraitFragment();
            Bundle bundle_myVideo = new Bundle();
            bundle_myVideo.putString("typeLayout", "Portrait");
            bundle_myVideo.putString("type", "profile_video");
            bundle_myVideo.putString("id", getUser_id);
            myVideoPortraitFragment.setArguments(bundle_myVideo);
            getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_profile, myVideoPortraitFragment,
                    getResources().getString(R.string.my_video)).commit();
        }
    }

    public void profile(final String id, final String other_user_id) {

        profileLists.clear();

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        if (method.pref.getString(method.profileId, null).equals(getUser_id)) {
            jsObj.addProperty("method_name", "user_profile");
            jsObj.addProperty("user_id", id);
        } else {
            jsObj.addProperty("method_name", "other_user_profile");
            jsObj.addProperty("other_user_id", other_user_id);
            jsObj.addProperty("user_id", id);
        }
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (getActivity() != null) {

                    Log.d("Response", new String(responseBody));
                    String res = new String(responseBody);

                    String already_follow = null;
                    String total_point = null;

                    try {
                        JSONObject jsonObject = new JSONObject(res);

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            user_id = object.getString("user_id");
                            name = object.getString("name");
                            email = object.getString("email");
                            if (method.pref.getString(method.profileId, null).equals(getUser_id)) {
                                phone = object.getString("phone");
                                total_point = object.getString("total_point");
                            } else {
                                already_follow = object.getString("already_follow");
                            }
                            String user_total_video = object.getString("user_total_video");
                            youtube = object.getString("user_youtube");
                            instagram = object.getString("user_instagram");
                            String user_image = object.getString("user_image");
                            String success = object.getString("success");
                            String user_code = object.getString("user_code");
                            String total_followers = object.getString("total_followers");
                            String total_following = object.getString("total_following");

                            List<UserFollowList> userFollowerLists = new ArrayList<>();
                            JSONArray jsonArray_follower = object.getJSONArray("user_followers");
                            for (int j = 0; j < jsonArray_follower.length(); j++) {

                                JSONObject object_follower = jsonArray_follower.getJSONObject(j);
                                String user_id = object_follower.getString("user_id");
                                String user_name = object_follower.getString("user_name");
                                String user_follower_image = object_follower.getString("user_image");

                                userFollowerLists.add(new UserFollowList(user_id, user_name, user_follower_image));

                            }

                            List<UserFollowList> userFollowingLists = new ArrayList<>();
                            JSONArray jsonArray_following = object.getJSONArray("user_following");
                            for (int k = 0; k < jsonArray_following.length(); k++) {

                                JSONObject object_following = jsonArray_following.getJSONObject(k);
                                String user_id = object_following.getString("user_id");
                                String user_name = object_following.getString("user_name");
                                String user_following_image = object_following.getString("user_image");

                                userFollowingLists.add(new UserFollowList(user_id, user_name, user_following_image));

                            }

                            profileLists.add(new ProfileList(user_id, name, email, phone, user_image, user_total_video, youtube, instagram, user_code, total_point, total_followers, total_following, already_follow, userFollowerLists, userFollowingLists));

                            method.editor.putString(method.userImage, user_image);
                            method.editor.commit();

                        }

                        if (profileLists.size() != 0) {

                            fabButton.setVisibility(View.VISIBLE);

                            if (method.pref.getString(method.profileId, null).equals(other_user_id)) {
                                button_follow.setText(getResources().getString(R.string.edit_profile));
                            } else {
                                if (profileLists.get(0).getAlready_follow().equals("true")) {
                                    button_follow.setText(getResources().getString(R.string.unfollow));
                                } else {
                                    button_follow.setText(getResources().getString(R.string.follow));
                                }
                            }

                            if (!profileLists.get(0).getUser_image().equals("")) {
                                Picasso.get().load(profileLists.get(0).getUser_image())
                                        .placeholder(R.drawable.user_profile).into(imageView_profile);
                            }

                            textViewFollower.setText(method.format(Double.parseDouble(profileLists.get(0).getTotal_followers())));
                            textViewFollowing.setText(method.format(Double.parseDouble(profileLists.get(0).getTotal_following())));
                            textViewUserName.setText(profileLists.get(0).getUser_name());
                            textView_totalVideo.setText(method.format(Double.parseDouble(profileLists.get(0).getUser_total_video())));

                            imageView_youtube.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageView_youtube.startAnimation(myAnim);
                                    String string = profileLists.get(0).getUser_youtube();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_youtube_link));
                                    } else {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(string));
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            method.alertBox(getResources().getString(R.string.wrong));
                                        }
                                    }
                                }
                            });

                            imageView_instagram.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    imageView_instagram.startAnimation(myAnim);
                                    String string = profileLists.get(0).getUser_instagram();
                                    if (string.equals("")) {
                                        method.alertBox(getResources().getString(R.string.user_not_instagram_link));
                                    } else {
                                        Uri uri = Uri.parse(string);
                                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                                        likeIng.setPackage("com.instagram.android");
                                        try {
                                            startActivity(likeIng);
                                        } catch (ActivityNotFoundException e) {
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse(string)));
                                            } catch (Exception e1) {
                                                method.alertBox(getResources().getString(R.string.wrong));
                                            }

                                        }
                                    }
                                }
                            });

                            button_follow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Method.isNetworkAvailable(getActivity())) {
                                        if (method.pref.getString(method.profileId, null).equals(other_user_id)) {
                                            editProfile();
                                        } else {
                                            follow(id, other_user_id);
                                        }
                                    } else {
                                        method.alertBox(getResources().getString(R.string.internet_connection));
                                    }
                                }
                            });

                            linearLayout_followings.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (profileLists.get(0).getUserFollowingList().size() != 0) {
                                        MainActivity.toolbar.setTitle(getResources().getString(R.string.following));
                                        UserFollowFragment userFollowFragment = new UserFollowFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "user_followings");
                                        bundle.putSerializable("array", (Serializable) profileLists.get(0).getUserFollowingList());
                                        userFollowFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, userFollowFragment, getResources().getString(R.string.following)).addToBackStack("sub").commitAllowingStateLoss();
                                    } else {
                                        method.alertBox(getResources().getString(R.string.not_following));
                                    }

                                }
                            });

                            linearLayout_follower.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (profileLists.get(0).getUserFollowerList().size() != 0) {
                                        MainActivity.toolbar.setTitle(getResources().getString(R.string.followers));
                                        UserFollowFragment userFollowFragment = new UserFollowFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "user_followings");
                                        bundle.putSerializable("array", (Serializable) profileLists.get(0).getUserFollowerList());
                                        userFollowFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, userFollowFragment, getResources().getString(R.string.following)).addToBackStack("sub").commitAllowingStateLoss();
                                    } else {
                                        method.alertBox(getResources().getString(R.string.not_follower));
                                    }
                                }
                            });

                            progressBar.setVisibility(View.GONE);

                            MyVideoFragment myVideoFragment = new MyVideoFragment();
                            Bundle bundle_myVideo = new Bundle();
                            bundle_myVideo.putString("typeLayout", "Landscape");
                            bundle_myVideo.putString("id", getUser_id);
                            myVideoFragment.setArguments(bundle_myVideo);
                            getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_profile, myVideoFragment,
                                    getResources().getString(R.string.my_video)).commit();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        textView_noData.setVisibility(View.VISIBLE);
                        fabButton.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                textView_noData.setVisibility(View.VISIBLE);
                fabButton.setVisibility(View.GONE);
            }
        });
    }

    private void follow(final String user_id, final String other_user) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_follow");
        jsObj.addProperty("user_id", other_user);
        jsObj.addProperty("follower_id", user_id);
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
                            String msg = object.getString("msg");
                            String success = object.getString("success");

                            if (success.equals("1")) {
                                method.alertBox(msg);
                                if (msg.equals("Following!")) {
                                    button_follow.setText(getResources().getString(R.string.unfollow));
                                } else {
                                    button_follow.setText(getResources().getString(R.string.follow));
                                }
                                getUser_id = other_user;
                                profile(user_id, other_user);
                            } else {
                                method.alertBox(msg);
                            }

                        }

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
