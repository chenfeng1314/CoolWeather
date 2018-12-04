package com.eblocks.platform.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:chenhongsheng
 * @data:2018/12/4 20:49
 * @describe：
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
