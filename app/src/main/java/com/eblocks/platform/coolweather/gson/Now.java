package com.eblocks.platform.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:chenhongsheng
 * @data:2018/12/4 20:47
 * @describe：
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
