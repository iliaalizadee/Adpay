package com.example.videostatus.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "video";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_LIKE = "user_like";
    private static final String USER_VIEW = "user_view";
    private static final String USER_DL = "user_dl";
    private static final String USER_UP = "user_up";
    private static final String LAST_DATE = "last_date";
    private static final String USER_AD = "user_add";
    private static final String SHOW_ALERT = "show_alert";
    private static final String FREQ_DAYS = "freq_days";
    private static final String WATCH_TAPSELL = "watch_tapsell";
    private static final String WATCH_ADMOB = "watch_admob";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
    public void setUserLike(int maxLike){
        editor.putInt(USER_LIKE,maxLike);
        editor.commit();
    }
    public int getUserLike(){
        return pref.getInt(USER_LIKE,0);
    }

    public  int getUserDl() {
        return pref.getInt(USER_DL,0);
    }
    public void setUserDl(int UserDl){
        editor.putInt(USER_DL,UserDl);
        editor.commit();
    }
    public  int getUserView() {
        return pref.getInt(USER_VIEW,0);
    }
    public void setUserView(int UserView){
        editor.putInt(USER_DL,UserView);
        editor.commit();
    }
    public  int getUserUp() {
        return pref.getInt(USER_UP,0);
    }
    public void setUserUp(int UserUp){
        editor.putInt(USER_UP,UserUp);
        editor.commit();
    }
    public  String getLastDate() {
        return pref.getString(LAST_DATE,DateHandler.gettime());
    }
    public void setLastDate(String LastDate){
        editor.putString(LAST_DATE,LastDate);
        editor.commit();
    }
    public  int getUserAd() {
        return pref.getInt(USER_AD,0);
    }
    public void setUserAd(int UserAd){
        editor.putInt(USER_AD,UserAd);
        editor.commit();
    }
    public int getUserTapsell(){
        return pref.getInt(WATCH_TAPSELL,0);
    }
    public void setUserTapsell(int UserTapsell){
        editor.putInt(WATCH_TAPSELL,UserTapsell);
        editor.commit();
    }
    public int getUserAdmob(){
        return pref.getInt(WATCH_TAPSELL,0);
    }
    public void setUserAdmob(int UserAdmob){
        editor.putInt(WATCH_ADMOB,UserAdmob);
        editor.commit();
    }
    public void setAllZero(){
        setUserDl(0);
        setUserLike(0);
        setUserView(0);
        setUserUp(0);
        setUserAd(0);
        setFreqDays(0);
        setUserTapsell(0);
        setUserAdmob(0);

    }
    public boolean getShowAlert(){
        return pref.getBoolean(SHOW_ALERT,true);
    }
    public void setShowAlert(Boolean bool){
        editor.putBoolean(SHOW_ALERT,bool);
        editor.commit();
    }
    public int getFreqDays(){
        return pref.getInt(FREQ_DAYS,0);
    }
    public void setFreqDays(int freqDays){
        editor.putInt(FREQ_DAYS,freqDays);
        editor.commit();
    }

}
