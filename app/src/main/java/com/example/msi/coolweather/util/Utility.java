package com.example.msi.coolweather.util;

import android.text.TextUtils;

import com.example.msi.coolweather.db.City;
import com.example.msi.coolweather.db.Country;
import com.example.msi.coolweather.db.Province;
import com.example.msi.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by msi on 2018/3/25.
 */
//处理JSON格式的util
public class Utility {
    //解析和处理返回的省份的信息
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
               try {
                   JSONArray allProvince = new JSONArray(response);
                   for(int i = 0;i < allProvince.length();i++) {
                       JSONObject jsonObject = allProvince.getJSONObject(i);
                       Province province = new Province();
                       province.setProvinceName(jsonObject.getString("name"));
                       province.setProvinceCode(jsonObject.getInt("id"));
                       province.save();
                   }
                   return true;
               } catch (JSONException e) {
                   e.printStackTrace();
               }
        }
        return false;
    }

    //解析市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                for(int i = 0; i < allCity.length();i++){
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析县级数据
    public static boolean handleCountryResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountry = new JSONArray(response);
                for(int i = 0;i < allCountry.length();i++){
                    JSONObject jsonObject = allCountry.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(jsonObject.getString("name"));
                    country.setWeatherId(jsonObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析天气JSON数据
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
