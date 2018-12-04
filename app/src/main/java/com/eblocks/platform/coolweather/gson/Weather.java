package com.eblocks.platform.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author:chenhongsheng
 * @data:2018/12/4 20:57
 * @describe：
 */
public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
