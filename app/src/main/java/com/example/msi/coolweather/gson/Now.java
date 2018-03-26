package com.example.msi.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by msi on 2018/3/26.
 */
//处理返回的now 有 tmp（温度）、 cond{txt（天气）}
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
