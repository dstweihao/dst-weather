package com.example.coolweather.gson;
/*
 * @创建者 Administrator
 * @创建时间 2017/10/8 0008 23:47
 * @描述 ${TODO}
 *
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 * 
 */

import com.google.gson.annotations.SerializedName;

public class AQI {

    @SerializedName("city")
    public AQICity mCity;

    public class AQICity {
        public String aqi;
        public String pm25;
    }

}
