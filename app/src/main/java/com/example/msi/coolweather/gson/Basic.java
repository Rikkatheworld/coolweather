package com.example.msi.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by msi on 2018/3/26.
 */

//用来处理和风天气返回的basic，里面有city、id、uapdate{loc}
public class Basic {

    @SerializedName("city")
    public String cityname;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
