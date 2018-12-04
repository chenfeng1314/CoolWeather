package com.eblocks.platform.coolweather.base;

import android.app.Application;

import org.litepal.LitePal;

/**
 * author : chenhongsheng
 * e-mail : chenhongshengadam@dingtalk.com
 * date   : 2018/12/4  11:34
 * desc   :
 * version: 1.0
 */
public class MyBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
