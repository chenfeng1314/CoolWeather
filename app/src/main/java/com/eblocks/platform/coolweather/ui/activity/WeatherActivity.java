package com.eblocks.platform.coolweather.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eblocks.platform.coolweather.R;
import com.eblocks.platform.coolweather.gson.Forecast;
import com.eblocks.platform.coolweather.gson.Weather;
import com.eblocks.platform.coolweather.service.AutoUpdateService;
import com.eblocks.platform.coolweather.util.HttpUtil;
import com.eblocks.platform.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mCityTitle;
    private TextView mUpdateTimeTitle;
    private TextView mTextDegree;
    private TextView mInfoTextWeather;
    private LinearLayout mLayoutForecast;
    private TextView mTextApi;
    private TextView mTextPm25;
    private TextView mTextComfort;
    private TextView mWashTextCar;
    private TextView mTextSport;
    private ScrollView mLayoutWeather;
    private TextView mTextDate;
    private TextView mTextInfo;
    private TextView mTextMax;
    private TextView mTextMin;
    public SwipeRefreshLayout mRefreshSwipe;
    private Button mButtonNav;
    public DrawerLayout mLayoutDrawer;

    public static final String RESULT_OK = "ok";
    public static final String COMFORT_INFO = "舒适度：";
    public static final String CARWASH_INFO = "洗车指数：";
    public static final String SPORT_INFO = "运动建议：";
    private ImageView mPicImageBing;
    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        mRefreshSwipe.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mPicImageBing);
        } else {
            loadBingPic();
        }
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            mLayoutWeather.setVisibility(View.INVISIBLE);
            if (weatherId == null) {
                requestWeather("CN101190901");
            } else {
                requestWeather(weatherId);
            }
        }
        mRefreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }

    /**
     * 加载每日必应一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mPicImageBing);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && RESULT_OK.equals(weather.status)) {
            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split("")[1];
        String degree = weather.now.temperature + "C";
        String weatherInfo = weather.now.more.info;
        mCityTitle.setText(cityName);
        mUpdateTimeTitle.setText(updateTime);
        mTextDegree.setText(degree);
        mInfoTextWeather.setText(weatherInfo);
        mLayoutForecast.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mLayoutForecast, false);
            mTextDate = (TextView) view.findViewById(R.id.date_text);
            mTextInfo = (TextView) view.findViewById(R.id.info_text);
            mTextMax = (TextView) view.findViewById(R.id.max_text);
            mTextMin = (TextView) view.findViewById(R.id.min_text);
            mTextDate.setText(forecast.date);
            mTextInfo.setText(forecast.more.info);
            mTextMax.setText(forecast.temperature.max);
            mTextMin.setText(forecast.temperature.min);
            mLayoutForecast.addView(view);
        }
        if (weather.aqi != null) {
            mTextApi.setText(weather.aqi.city.aqi);
            mTextPm25.setText(weather.aqi.city.pm25);
        }
        String comfort = COMFORT_INFO + weather.suggestion.comfort.info;
        String carWash = CARWASH_INFO + weather.suggestion.carWash.info;
        String sport = SPORT_INFO + weather.suggestion.sport.info;
        mTextComfort.setText(comfort);
        mWashTextCar.setText(carWash);
        mTextSport.setText(sport);
        mLayoutWeather.setVisibility(View.VISIBLE);
    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
        this.weatherId = weatherId;
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=5765d5bd5788441fbaf80b24adee77cd";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mRefreshSwipe.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && RESULT_OK.equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mRefreshSwipe.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCityTitle = (TextView) findViewById(R.id.title_city);
        mUpdateTimeTitle = (TextView) findViewById(R.id.title_update_time);
        mTextDegree = (TextView) findViewById(R.id.degree_text);
        mInfoTextWeather = (TextView) findViewById(R.id.weather_info_text);
        mLayoutForecast = (LinearLayout) findViewById(R.id.forecast_layout);
        mTextApi = (TextView) findViewById(R.id.api_text);
        mTextPm25 = (TextView) findViewById(R.id.pm25_text);
        mTextComfort = (TextView) findViewById(R.id.comfort_text);
        mWashTextCar = (TextView) findViewById(R.id.car_wash_text);
        mTextSport = (TextView) findViewById(R.id.sport_text);
        mLayoutWeather = (ScrollView) findViewById(R.id.weather_layout);
        mPicImageBing = (ImageView) findViewById(R.id.bing_pic_image);
        mRefreshSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mButtonNav = (Button) findViewById(R.id.nav_button);
        mButtonNav.setOnClickListener(this);
        mLayoutDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button:
                if (mLayoutDrawer.isDrawerOpen(GravityCompat.START)) {
                    mLayoutDrawer.closeDrawers();
                } else {
                    mLayoutDrawer.openDrawer(GravityCompat.START);
                }
                break;
            default:
                break;
        }
    }
}
