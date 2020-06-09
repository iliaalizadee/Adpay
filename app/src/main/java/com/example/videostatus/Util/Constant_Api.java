package com.example.videostatus.Util;

import android.os.Environment;

import com.example.videostatus.BuildConfig;
import com.example.videostatus.Item.AboutUsList;

import java.io.File;
import java.util.List;

public class Constant_Api {

    //main server api url
    public static String url = BuildConfig.My_api + "api.php";

    //main server api url
    public static String video_upload_url = BuildConfig.My_api + "api_video_upload.php";

    //main server api tag
    public static String tag = "ANDROID_REWARDS_APP";

    //Image url
    public static String image = url + "images/";

    //Status path
    public static String status_path = "WhatsApp/Media/.Statuses";

    //Status Download path
    public static String download_status_path = Environment.getExternalStorageDirectory() + "/Video_Status/" + "/status_saver/";

    public static int AD_COUNT = 0;
    public static int AD_COUNT_SHOW = 0;

    public static int REWARD_VIDEO_AD_COUNT = 0;
    public static int REWARD_VIDEO_AD_COUNT_SHOW = 0;

    public static int AD_LIST_VIEW = 4;// landscape video list ad show position

    public static int AD_LIST_VIEW_PORTRAIT = 5;//minimum value start from 3,5,7,9,11 ... (portrait video ad show position)

    public static int VIDEO_FILE_SIZE = 30;//set file size
    public static long VIDEO_FILE_DURATION = 60;//set video duration in second
    public static int CATEGORY_SHOW = 10;//show category add plus one set CATEGORY_SHOW ex:-6 category add 6+1=7 (set minimum number to 1)

    public static AboutUsList aboutUsList;

    public static List<File> imageFilesList;
    public static List<File> videoFilesList;

    public static List<File> downloadImageFilesList;
    public static List<File> downloadVideoFilesList;

}
