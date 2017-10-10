package com.example.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView         mWeatherLayout;
    private TextView           mTitleCity;
    private TextView           mTitleUpdateTime;
    private TextView           mDegreeText;
    private TextView           mWeatherInfoText;
    private LinearLayout       mForecastLayout;
    private TextView           mAqiText;
    private TextView           mPm25Text;
    private TextView           mComfortText;
    private TextView           mCarWashText;
    private TextView           mSportText;
    private ImageView          mBingPic;
    public SwipeRefreshLayout mSwipeRefresh;
    private SharedPreferences  mPrefs;
    private Button             mNavButton;
    public DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 让背景图片和状态栏融合在一起。Android 5.0 以上才支持，所以需要判断版本号。
        // 天气界面的头布局和状态栏紧贴在一起，因为这时状态栏成为布局一部分，没有单独留空间。
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);


        //初始化控件
        initView();

        //查询天气
        initWeather();

        //加载必应图片
        initBingPic();

        //下拉刷新
        initSwipeRefresh();

        //左侧滑动菜单
        initDrawerLayout();


    }

    private void initDrawerLayout() {

        //点击导航键
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // START-设置从左边滑出菜单  END 是右边。
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initView() {

        //滑动菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //导航键
        mNavButton = (Button) findViewById(R.id.nav_button);


        //下拉刷新
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //每日必应图片
        mBingPic = (ImageView) findViewById(R.id.bing_pic_img);

        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mDegreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25Text = (TextView) findViewById(R.id.pm25_text);
        mComfortText = (TextView) findViewById(R.id.comfort_text);
        mCarWashText = (TextView) findViewById(R.id.car_wash_text);
        mSportText = (TextView) findViewById(R.id.sport_text);

    }

    private void initWeather() {
        // sp存储
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = mPrefs.getString("weather", null);

        // final 修饰变量，变量变成常量，且只能被赋值一次。
        final String weatherId;
        if (weatherString != null) {
            //有缓存时，直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.mBasic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时，去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.VISIBLE);
            Log.v("weather_id", weatherId);
            requestWeather(weatherId);
        }
        //设置下拉刷新的监听器，触发下拉刷新，回调onRefresh()方法，请求天气信息
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });


    }

    private void initBingPic() {
        String bingPic = mPrefs.getString("bing_pic", null);
        //如果有缓存，直接使用Glide来加载图片
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mBingPic);
        } else {
            //没有缓存，调用这个方法去请求今日的必应背景图片
            loadBingPic();
        }


    }

    private void initSwipeRefresh() {

        //设置下拉刷新进度颜色
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);


    }


    //根据天气id请求城市天气信息
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=ed8660313886405a9de79a33d7b075c7";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        //请求结束，false表示刷新事件结束，并隐藏刷新进度条
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefresh.setRefreshing(false);
                    }
                });


            }
        });

        //每次请求天气，同时刷新背景图片
        loadBingPic();
    }


    //处理并展示weather 实体类中的数据
    private void showWeatherInfo(Weather weather) {

        // 苏州
        String cityName = weather.mBasic.cityName;
        // 21:58
        String updateTime = weather.mBasic.mUpdate.updateTime.split(" ")[1];
        //
        String degree = weather.mNow.temperature + "℃";
        String weatherInfo = weather.mNow.mMore.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mDegreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.mForecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.mMore.info);
            maxText.setText(forecast.mTemperature.max);
            minText.setText(forecast.mTemperature.min);
            mForecastLayout.addView(view);
        }
        if (weather.mAQI != null) {
            mAqiText.setText(weather.mAQI.mCity.aqi);
            mPm25Text.setText(weather.mAQI.mCity.pm25);

        }

        String comfort = "舒适度：" + weather.mSuggestion.mComfort.info;
        String carWash = "洗车指数：" + weather.mSuggestion.mCarWash.info;
        String sport = "运动建议：" + weather.mSuggestion.mSport.info;
        mComfortText.setText(comfort);
        mCarWashText.setText(carWash);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);

    }

    //加载必应每日一图
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
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic)
                                .into(mBingPic);
                    }
                });
            }
        });
    }
}





























