package com.example.videostatus.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.example.videostatus.Adapter.AllCommentAdapter;
import com.example.videostatus.Item.CommentList;
import com.example.videostatus.R;
import com.example.videostatus.Util.API;
import com.example.videostatus.Util.Constant_Api;
import com.example.videostatus.Util.Events;
import com.example.videostatus.Util.GlobalBus;
import com.example.videostatus.Util.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class AllComment extends AppCompatActivity {

    public Toolbar toolbar;
    private String videoId;
    private Method method;
    private AllCommentAdapter allCommentAdapter;
    private TextView textViewNoCommentFound;
    private EditText editTextComment;
    private List<CommentList> commentLists;
    private InputMethodManager inputMethodManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);

        Method.forceRTLIfSupported(getWindow(), AllComment.this);

        method = new Method(AllComment.this);

        commentLists = new ArrayList<>();

        Intent intent = getIntent();
        videoId = intent.getStringExtra("videoId");
        commentLists = (List<CommentList>) intent.getSerializableExtra("array");

        toolbar = findViewById(R.id.toolbar_all_comment);
        toolbar.setTitle(getResources().getString(R.string.allcomment));
        setSupportActionBar(toolbar);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewNoCommentFound = findViewById(R.id.textView_noComment_all_Comment);
        editTextComment = findViewById(R.id.EditText_comment_allComment);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_all_comment);

        editTextComment.setClickable(true);
        editTextComment.setFocusable(false);

        textViewNoCommentFound.setVisibility(View.GONE);

        if (commentLists.size() == 0) {
            textViewNoCommentFound.setVisibility(View.VISIBLE);
        }

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AllComment.this);
        recyclerView.setLayoutManager(layoutManager);

        allCommentAdapter = new AllCommentAdapter(AllComment.this, commentLists);
        recyclerView.setAdapter(allCommentAdapter);

        editTextComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (method.pref.getBoolean(method.pref_login, false)) {

                    editTextComment.setFocusable(true);

                    final Dialog dialog = new Dialog(AllComment.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialogbox_comment);
                    dialog.getWindow().setLayout(ViewPager.LayoutParams.FILL_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    ImageView imageView = dialog.findViewById(R.id.imageView_dialogBox_comment);
                    final EditText editText = dialog.findViewById(R.id.editText_dialogbox_comment);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editText.setError(null);
                            String comment = editText.getText().toString();
                            if (comment.equals("") || comment.isEmpty()) {
                                editText.requestFocus();
                                editText.setError(getResources().getString(R.string.please_enter_comment));
                            } else {
                                if (Method.isNetworkAvailable(AllComment.this)) {
                                    editText.clearFocus();
                                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                    Comment(method.pref.getString(method.profileId, null), comment);
                                } else {
                                    Toast.makeText(AllComment.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }
                    });

                    dialog.show();

                } else {
                    Method.loginBack = true;
                    startActivity(new Intent(AllComment.this, Login.class));
                }


            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void Comment(final String userId, final String comment) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_video_comment");
        jsObj.addProperty("comment_text", comment);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("post_id", videoId);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

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
                            Toast.makeText(AllComment.this, msg, Toast.LENGTH_SHORT).show();
                            textViewNoCommentFound.setVisibility(View.GONE);
                            String userImage = method.pref.getString(method.userImage, null);
                            if (userImage == null) {
                                userImage = "";
                            }
                            commentLists.add(0, new CommentList(method.pref.getString(method.profileId, null),
                                    method.pref.getString(method.userName, null),
                                    userImage,
                                    videoId,
                                    comment, getResources().getString(R.string.today)));
                            allCommentAdapter.notifyDataSetChanged();
                            Events.Comment commentNotify = new Events.Comment(method.pref.getString(method.profileId, null),
                                    method.pref.getString(method.userName, null),
                                    userImage,
                                    videoId,
                                    comment, getResources().getString(R.string.today));
                            GlobalBus.getBus().post(commentNotify);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
