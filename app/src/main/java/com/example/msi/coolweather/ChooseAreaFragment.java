package com.example.msi.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msi.coolweather.db.City;
import com.example.msi.coolweather.db.Country;
import com.example.msi.coolweather.db.Province;
import com.example.msi.coolweather.gson.Weather;
import com.example.msi.coolweather.util.HttpUtil;
import com.example.msi.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by msi on 2018/3/25.
 */

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private Province selectedProvince;
    private City selctedCity;       //被选中的城市
    private int currentlevel;       //当前选中的级别

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentlevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentlevel == LEVEL_CITY){
                    selctedCity = cityList.get(position);
                    queryCountries();
                }else if(currentlevel == LEVEL_COUNTRY){
                    String weatherId = countryList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.swipeRefresh.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentlevel == LEVEL_COUNTRY){
                    queryCities();
                }else if(currentlevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    //优先查询数据库，否则查询服务器
    private void queryProvince() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);  //找到所有的Province类型
        if(provinceList.size() > 0){
            datalist.clear();
            for(Province province : provinceList){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_PROVINCE;
        }else {
            String adress = "http://guolin.tech/api/china";
            queryFromServer(adress,"province");
        }
    }

    //从服务器上查询
    private void queryFromServer(String adress, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(adress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               String responseText = response.body().string();
               boolean result =false;
               if("province".equals(type)){
                   result = Utility.handleProvinceResponse(responseText);
               }else if("city".equals(type)){
                   result = Utility.handleCityResponse(responseText,selectedProvince.getId());
               }else if("country".equals(type)){
                   result = Utility.handleCountryResponse(responseText,selctedCity.getId());
               }
              if(result){
                   getActivity().runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           closeProgressDialog();
                           if ("province".equals(type)) {
                               queryProvince();
                           }else if("city".equals(type)){
                               queryCities();
                           }else if("country".equals(type)){
                               queryCountries();
                           }
                       }
                   });
              }
            }

        });

    }

    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    //显示进度对话框
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("少女祈祷中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void queryCountries() {
        titleText.setText(selctedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid = ?",String.valueOf(selctedCity.getId())).find(Country.class);
        if(countryList.size() > 0){
            datalist.clear();
            for(Country country : countryList){
                datalist.add(country.getCountryeName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_COUNTRY;
        }else {
            int Provincecode = selectedProvince.getProvinceCode();
            int Citycode = selctedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + Provincecode + "/" + Citycode;
            queryFromServer(address,"country");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size() > 0){
            datalist.clear();
            for(City city : cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_CITY;
        }else {
            int Provincecode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + Provincecode;
            queryFromServer(address,"city");
        }
    }
}
