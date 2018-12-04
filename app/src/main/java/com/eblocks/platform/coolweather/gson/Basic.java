package com.eblocks.platform.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:chenhongsheng
 * @data:2018/12/4 20:43
 * @describe：
 */
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
