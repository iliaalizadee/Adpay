package com.example.videostatus.Util;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class DateHandler {
    static Calendar cal;
    public static String gettime(){
         cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMddyyyy");
        return dateFormat.format(cal.getTime());

    }
    static Date sdv;
    public static int diffdays(String startDateValue){
         cal = Calendar.getInstance();
         Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMddyyyy");

        try {
            sdv = sdf.parse(startDateValue);
        }catch (Exception ex){}
        long diff = date.getTime() - sdv.getTime();
        int diffs =(int) TimeUnit.DAYS.convert(diff,TimeUnit.DAYS);
        return diffs;
    }
}
