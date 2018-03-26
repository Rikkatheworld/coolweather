package com.example.msi.coolweather.gson;

/**
 * Created by msi on 2018/3/26.
 */

//处理和风天气返回的aqi，里面有city{aqi、pm25}
public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
