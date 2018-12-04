package com.eblocks.platform.coolweather.gson;

/**
 * @author:chenhongsheng
 * @data:2018/12/4 20:45
 * @describe：
 */
public class AQI {
    public AQICity city;

    public class AQICity {
        public String api;

        public String pm25;
    }
}
