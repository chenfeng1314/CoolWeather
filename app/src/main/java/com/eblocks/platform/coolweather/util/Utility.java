package com.eblocks.platform.coolweather.util;

import android.text.TextUtils;

import com.eblocks.platform.coolweather.db.City;
import com.eblocks.platform.coolweather.db.County;
import com.eblocks.platform.coolweather.db.Province;
import com.eblocks.platform.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * author : chenhongsheng
 * e-mail : chenhongshengadam@dingtalk.com
 * date   : 2018/12/4  11:43
 * desc   :
 * version: 1.0
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param responce 数据
     * @return 是否处理
     */
    public static boolean handleProvinceResponce(String responce) {
        if (!TextUtils.isEmpty(responce)) {
            try {
                JSONArray allProvince = new JSONArray(responce);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     *
     * @param responce   数据
     * @param provinceId 省级Id
     * @return 是否处理
     */
    public static boolean handleCityResponce(String responce, int provinceId) {
        if (!TextUtils.isEmpty(responce)) {
            try {
                JSONArray cityArray = new JSONArray(responce);
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject cityObject = cityArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
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

    /**
     * 解析和处理服务器返回的县级数据
     *
     * @param responce 数据
     * @param cityId   城市Id
     * @return 是否处理
     */
    public static boolean handleCountyResponse(String responce, int cityId) {
        if (!TextUtils.isEmpty(responce)) {
            try {
                JSONArray countyArray = new JSONArray(responce);
                for (int i = 0; i < countyArray.length(); i++) {
                    JSONObject countyObject = countyArray.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
