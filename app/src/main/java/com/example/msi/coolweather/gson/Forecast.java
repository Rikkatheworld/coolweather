package com.example.msi.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by msi on 2018/3/26.
 */
//处理返回的未来天气 数组{date，cond{txt}，tmp{max,min}}
public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;


    public class Temperature {
        public String max,min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
