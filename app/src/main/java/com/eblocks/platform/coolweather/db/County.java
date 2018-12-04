package com.eblocks.platform.coolweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * author : chenhongsheng
 * e-mail : chenhongshengadam@dingtalk.com
 * date   : 2018/12/4  11:24
 * desc   :
 * version: 1.0
 */
public class County extends LitePalSupport {

    private int id;
    private String countyName;
    private String weatherId;
    private int cityId;

    public County(int id, String countyName, String weatherId, int cityId) {
        this.id = id;
        this.countyName = countyName;
        this.weatherId = weatherId;
        this.cityId = cityId;
    }

    public County() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
