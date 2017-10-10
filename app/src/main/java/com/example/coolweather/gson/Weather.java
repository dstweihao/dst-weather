package com.example.coolweather.gson;
/*
 * @创建者 Administrator
 * @创建时间 2017/10/9 0009 0:04
 * @描述 ${TODO}
 *
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 * 
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {


    public String     status;

    @SerializedName("basic")
    public Basic      mBasic;

    @SerializedName("aqi")
    public AQI        mAQI;

    @SerializedName("now")
    public Now        mNow;

    @SerializedName("suggestion")
    public Suggestion mSuggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> mForecastList;
}
