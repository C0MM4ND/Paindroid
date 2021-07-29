package com.c0mm4nd.paindroid.utils;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    @Nullable
    @TypeConverter
    public static Date stringToDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance(); // get now
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Calendar getTodayCalender() {
        Calendar calendar = Calendar.getInstance(); // get now
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    @TypeConverter
    public static int dateToTimestamp(Date date) {
        return (int) (date.getTime() / 1_000);
    }


    public static int YMDToTimestamp(int year, int month, int day) {
        Calendar calender = getTodayCalender();
        calender.set(year, month, day);
        return dateToTimestamp(calender.getTime());
    }

    public static String YMDToString(int year, int month, int day) {
        Calendar calender = getTodayCalender();
        calender.set(year, month, day);

        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calender.getTime());
    }

    public static String timestampToString(int timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) timestamp * 1_000);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }
}
