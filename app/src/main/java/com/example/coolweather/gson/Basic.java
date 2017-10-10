package com.example.coolweather.gson;
/*
 * @创建者 Administrator
 * @创建时间 2017/10/8 0008 23:32
 * @描述 ${TODO}
 *
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 * 
 */

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public Update mUpdate;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
