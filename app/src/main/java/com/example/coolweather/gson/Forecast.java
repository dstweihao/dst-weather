package com.example.coolweather.gson;
/*
 * @创建者 Administrator
 * @创建时间 2017/10/9 0009 0:00
 * @描述 ${TODO}
 *
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 * 
 */

import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature mTemperature;

    @SerializedName("cond")
    public More mMore;


    public class Temperature {


        public String max;
        public String min;
    }

    public class More {

        @SerializedName("txt_d")
        public String info;


    }


}
