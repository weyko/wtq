package net.skjr.wtq.ui.activity.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import net.skjr.wtq.R;
import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyApp;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.core.utils.sys.NetUtils;
import net.skjr.wtq.core.utils.sys.ThreadPoolUtils;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.SystemInfo;

import rx.Subscription;

/**
 * 启动欢迎界面
 */
public class SplashActivity extends AppCompatActivity {

    protected Subscription subscription;

    //最短显示时间/毫秒
    private static final int SHOW_TIME_MIN = 2000;

    //启动时间
    private long startTime;

    //应该启动的Activity 0:GuideActivity 1：LoginActivity 2: MainActivity
    private int shouldStartActivity = 0;
    private static final String SERVER_ERROR = "无法连接服务器，应用将自动退出";
    private static final String NET_ERROR = "无法连接网络，请检查你的手机网络是否正常";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //标记启动开始时间
        startTime = System.currentTimeMillis();

        //代表APP是全新启动
        AppController.getInstance().getMyApp().isNewStart = false;

        beginSplash();

        initLocation();
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            String city = aMapLocation.getCity();
            double latitude = aMapLocation.getLatitude(); //纬度
            double longitude = aMapLocation.getLongitude();//经度
            int locationType = aMapLocation.getLocationType();//来源
            String substring = city.substring(city.length() - 1, city.length());
            if(substring.equals("市")) {
                city = city.substring(0, city.length() - 1);
            }
            MyPreference.setLongitude(String.valueOf(longitude));
            MyPreference.setLatitude(String.valueOf(latitude));
            MyPreference.setCity(city);
            Log.e("location","  city:"+city+"  latitude:"+latitude+"  longitude:"+longitude+"  type:"+locationType);
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onDestroy() {
        //取消rx订阅，如果有的话
        unsubscribe();

        super.onDestroy();
    }

    protected void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void beginSplash() {

        if (!NetUtils.isConnected(this)) {
            ToastUtils.show(getApplication(), NET_ERROR);
            finish();
        }

        //加载主页面需要的信息
//        loadAppInfo();
        startNextActivity();
    }

    /**
     * 启动工作的第一步，先从后台获取App初始化信息
     */
    /*private void loadAppInfo() {
        // 从后台获取App初始化信息
        MyPreference.clearSystemInfo();
        //biz.getSystemInfo();


        subscription = biz.getSystemInfo()
                          .subscribe(new Action1<APIResult<net.skjr.dsd.model.SystemInfo>>() {
                              @Override
                              public void call(APIResult<net.skjr.dsd.model.SystemInfo> apiResult) {
                                  loadAppInfoComplete(apiResult);
                              }
                          });

    }*/

    /**
     * 获取App初始化信息完成
     * @param apiResult
     */
    private void loadAppInfoComplete(APIResult<SystemInfo> apiResult) {
        if (apiResult.isSuccess) {
            SystemInfo result = apiResult.result;

            if (result == null)
                return;

            //保存基地址
            if (!TextUtils.isEmpty(result.serverUrl)) {
                MyPreference.setBaseUrl(result.serverUrl);
            }

            //保存
            MyPreference.setSystemInfo(result);

            //第一次获取后台信息成功后，才能进入
            startNextActivity();
        } else {
            ToastUtils.show(getApplicationContext(), "无法连接服务器，请检查网络");
        }
    }

    /**
     * 启动主界面
     * 根据条件，来进入引导界面/登录界面/主界面
     * 应该启动的Activity
     * shouldStartActivity= 0:GuideActivity 1：MainActivity-匿名 2:MainActivity-实名 3:LoginActivity
     */
    private void startNextActivity() {
        MyApp myApp = AppController.getInstance().getMyApp();

        //如果第一次打开App，那么进入[0引导界面]
        if (!MyPreference.hasOpenApp()) {
            shouldStartActivity = 0;
            endSplash2();
            return;
        }

        //[1主界面-匿名]
        shouldStartActivity = 1;
        boolean hasLoginInfo = myApp.existLoginInfo();

        //如果不存在登录信息，那么跳转到[1主界面-匿名]
        if (!hasLoginInfo) {
            shouldStartActivity = 1;
            endSplash2();
            return;
        }

        // 已经存在登录信息
        shouldStartActivity = 2;
        endSplash2();
        //取消自动登录
        //autoLogin();
    }

    /**
     * 自动登录到后台
     */
    private void autoLogin() {
        //TODO:autologin

    }


    /**
     * 另起线程进行计数
     * 如果不使用背景图Splash方式，而是自己定制界面，那么直接使用endSplash，会造成界面无法绘制
     */
    private void endSplash2() {
        ThreadPoolUtils.execute(new Runnable() {

            @Override
            public void run() {
                // 不足Splash延迟时间的话，补齐时间，避免Splash一闪而过
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {

                    }
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        goToNextUI();
                    }
                });
            }
        });
    }

    /**
     * 根据shouldStartActivity的值，来进入下一个Activity
     * 0:GuideActivity 1：MainActivity-匿名 2:MainActivity-实名 3:LoginActivity
     */
    public void goToNextUI() {
        Intent intent = null;
        Context context = SplashActivity.this;
        switch (shouldStartActivity) {
            case 0:
                startActivity(GuideActivity.class);
                break;
            case 1:
                startActivity(MainActivity.class);
                break;
            case 2:
                startActivity(MainActivity.class);
                break;
            case 3:
                startActivity(MainActivity.class);
                break;
            default:
                startActivity(MainActivity.class);
                break;
        }

        finish();
    }

    /**
     * 启动一个Activity
     *
     * @param activity
     */
    protected void startActivity(Class<? extends Activity> activity) {
        startActivity(new Intent(this, activity));
    }

}
