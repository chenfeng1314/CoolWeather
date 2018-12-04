package com.eblocks.platform.coolweather.db;

import org.litepal.LitePal;

/**
 * author : chenhongsheng
 * e-mail : chenhongshengadam@dingtalk.com
 * date   : 2018/12/4  11:24
 * desc   :
 * version: 1.0
 */
public class City extends LitePal {

    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public City(int id, String cityName, int cityCode, int provinceId) {
        this.id = id;
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.provinceId = provinceId;
    }

    public City() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
