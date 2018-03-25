package com.example.msi.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by msi on 2018/3/23.
 */

public class Country extends DataSupport {
    private int id;
    private String countryName;
    private String weatherId;
    private int cityId;
    public  int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getCountryeName(){
        return countryName;
    }
    public void setCountryName(String countryName){
        this.countryName = countryName;
    }
    public String  getWeatherId(){
        return weatherId;
    }
    public void setWeatherId(String weatherId){
        this.weatherId = weatherId;
    }
    public int getCityId(){
        return cityId;
    }
    public void setCityId(int cityId){
        this.cityId = cityId;
    }
}
