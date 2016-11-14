package net.skjr.wtq.ui.activity.system;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import net.skjr.wtq.R;
import net.skjr.wtq.application.AppController;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.chat.TabChatFragment;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.Url;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.common.utils.UpdateUtils;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.common.StringUtils;
import net.skjr.wtq.core.utils.sys.PermissionsUtils;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.model.SystemInfo;
import net.skjr.wtq.ui.fragment.TabInvestFragment;
import net.skjr.wtq.ui.fragment.TabMineFragment;
import net.skjr.wtq.ui.fragment.TabShopFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import chat.base.IMClient;
import rx.functions.Action1;

public class MainActivity extends BaseToolbarActivity {

    /**
     * 应该打开那个标签页
     */
    private int shouldOpenTab = -1;

    // 定义一个布局
    private LayoutInflater layoutInflater;

    private TextView toolbarTitle;
    private ImageView toolbarRightImage;

    //按返回键后记录的时间
    private long exitTime = 0;

    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;

    // 定义数组来存放Fragment界面
    private final Class fragmentArray[] = {TabInvestFragment.class,
                                           TabChatFragment.class,
                                           TabShopFragment.class,
                                           TabMineFragment.class};
    /*private final Class fragmentArray[] = {TabChatFragment.class,
                                           TabChatFragment.class,
                                            TabChatFragment.class,
                                            TabChatFragment.class};*/

    // 定义数组来存放按钮图片
    private final int mImageViewArray[] = {R.drawable.tab_invest_selector,
                                           R.drawable.tab_chat_selector,
                                           R.drawable.tab_shop_selector,
                                           R.drawable.tab_mine_selector};

    // Tab选项卡的文字
    private final String mTextviewArray[] = {"投资", "社交", "店铺", "我的"};

    // 当前的Tab页 0为知 1首页 2自选 3账户 4更多
    private int currentTab = 0;
    private ImageView mToolbarback;
    private ImageView mToolbarRight;
    private ImageView mToolbarRight1;
    //Tab页是否初始化完毕
    private boolean isTabInitComplete;
    private View mMain_toolbar;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //首页按返回键的处理
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                doExit();
            }

            //moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出app
     */
    private void doExit() {
        //先清理登录信息
//        exitAccount();

        finish();
        //System.exit(0);
//        FinishActivity.doFinish(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册EventBus
        EventBus.getDefault().register(this);

        initViews();
        initMainApp();
        IMClient.getInstance().startIMServer();
    }

    /**
     * 主界面的一些初始化动作
     */
    private void initMainApp() {
        //设置为已经开启过APP
        MyPreference.setHasOpenApp(true);

        //关闭注册界面
        EventBus.getDefault().post(new Event.CloseRegEvent());

        //检查权限
//        checkPermission();
    }


    /**
     * 检查是否有新版本
     * 如果是6.0以上，应该在授权通过后再检查
     */
    private void checkNewVersion() {
        if (!UpdateUtils.hasNewVersion(MainActivity.this)) {
            return;
        }

        SystemInfo systemInfo = MyPreference.getSystemInfo();
        String message = systemInfo.versionMessage;
        //message = "1.新结构，新页面\n2.自动推荐理财师，主动选择自己喜好\n3.资讯中心内容更有料，投资更有用\n4.优化个基分享功能";
        CommonUtils.showUpdateDialog(MainActivity.this, message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUtils.download(MainActivity.this);
            }
        }, null);
    }

    /**
     * 检查APP权限
     * 6.0及以上需要动态判断权限
     */
    private void checkPermission() {
        RxPermissions.getInstance(this)
                     .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                              Manifest.permission.READ_PHONE_STATE)
                     .subscribe(new Action1<Boolean>() {
                         @Override
                         public void call(Boolean granted) {
                             if (granted) {
                                 //检查更新
                                 checkNewVersion();
                             } else {
                                 //如果用户没有授权，那么提示用户去设置
                                 PermissionsUtils.showPermissionSettingDialog(MainActivity.this);
                             }
                         }
                     });
    }

    @Override
    public void onDestroy() {
        //反注册 EventBus
        EventBus.getDefault().unregister(this);

        //TODO:停止ShareSDK
        //ShareSDK.stopSDK(this);
        IMClient.getInstance().stopIMServer();
        super.onDestroy();
    }

    private void initViews() {
        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        layoutInflater = LayoutInflater.from(this);
        //获取toolbar图片
        mToolbarback = (ImageView) findViewById(R.id.toolbarBack);
        mToolbarRight = (ImageView) findViewById(R.id.toolbarRight);
        mToolbarRight1 = (ImageView) findViewById(R.id.toolbarRight1);
        mMain_toolbar = findViewById(R.id.main_toolbar);

        // 得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                onAppTabChanged(tabId);
            }

        });
        isTabInitComplete = true;
    }

    //主页标签最后的点击时间
    String lastHomeClickTime = "";

    /**
     * 两个时间是否超过1秒
     *
     * @param current
     * @param last
     * @return
     */
    private boolean isTabClickTimeOut(String current, String last) {
        if (TextUtils.isEmpty(current) || TextUtils.isEmpty(last))
            return true;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date backDate = format.parse(last);
            Calendar backCalendar = Calendar.getInstance();
            backCalendar.setTime(backDate);

            Date currentDate = format.parse(current);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);

            long startTim = backCalendar.getTimeInMillis();
            long endTim = currentCalendar.getTimeInMillis();
            long diff = endTim - startTim;
            int second = (int) (diff / 1000);

            if (second > 1) {
                return true;
            }

            L.d("Tab double tap：" + last + "——" + current);
            return false;

        } catch (Exception e) {

        }

        return true;
    }

    /**
     * 主页标签点击事件
     */
    private void onHomeTabClick() {

        String currentDateStr = CommonUtils.getCurrentTimeString();
        boolean isTimeOut = isTabClickTimeOut(currentDateStr, lastHomeClickTime);
        if (!isTimeOut) {
            //双击
            //showToast("double tap");
            EventBus.getDefault().post(new Event.RefreshHomeTabEvent());
        }

        lastHomeClickTime = CommonUtils.getCurrentTimeString();

        mTabHost.setCurrentTab(0);
    }

    /**
     * 账户标签点击切换事件
     * 当想要切换到个人中心标签页时，判断是否需要登录
     */
    private void onMineTabClick() {

        //如果不需要登录
        if (!getMyApp().isNeedLogin()) {
            //进入我的页面
            mTabHost.setCurrentTab(3);
            toolbarTitle.setText("我的");
            mToolbarback.setVisibility(View.VISIBLE);
            mToolbarback.setImageDrawable(getResources().getDrawable(R.drawable.wd_icon_sys));
            mToolbarRight.setVisibility(View.VISIBLE);
            mToolbarRight1.setVisibility(View.VISIBLE);
            return;
        } else {
            startActivity(LoginActivity.class);
            mTabHost.setCurrentTab(0);
            toolbarTitle.setText(R.string.app_name);
            mToolbarback.setVisibility(View.GONE);
            mToolbarRight.setVisibility(View.GONE);
            mToolbarRight1.setVisibility(View.GONE);
        }

    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }


    /**
     * EventBus：接收其他地方传来的切换标签页的通知，在Resume里生效
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenMainTab(Event.OpenMainTabEvent event) {
        //不能直接在这里切换，会导致异常
        //mTabHost.setCurrentTab(event.index);
        shouldOpenTab = event.index;
    }
    @Override
    protected void onResume() {
        super.onResume();

        //如果在某处指定了要打开某个标签页
        if (shouldOpenTab != -1 && mTabHost != null && isTabInitComplete) {
            mTabHost.setCurrentTab(shouldOpenTab);
            shouldOpenTab = -1;
        }

    }


    /**
     * EventBus：接收其他地方传来的切换到首页标签的通知，立刻生效
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenHomeTab(Event.OpenHomeTabEvent event) {
        mTabHost.setCurrentTab(0);
    }

    /**
     * 标签页切换处理，用于进行UI元素的调整
     *
     * @param tabId
     */
    private void onAppTabChanged(String tabId) {
        if (StringUtils.isEquals(tabId, "投资")) {
            mMain_toolbar.setVisibility(View.VISIBLE);
            toolbarTitle.setText(R.string.app_name);
            mToolbarback.setVisibility(View.GONE);
            mToolbarRight.setVisibility(View.GONE);
            mToolbarRight1.setVisibility(View.GONE);
        } else if (TextUtils.equals(tabId, "社交")) {
            mMain_toolbar.setVisibility(View.GONE);
            toolbarTitle.setText("社交");
            mToolbarback.setVisibility(View.GONE);
            mToolbarRight.setVisibility(View.GONE);
            mToolbarRight1.setVisibility(View.GONE);
        } else if (TextUtils.equals(tabId, "店铺")) {
            /*toolbarTitle.setText("店铺");
            mToolbarback.setVisibility(View.GONE);
            mToolbarRight.setVisibility(View.GONE);
            mToolbarRight1.setVisibility(View.GONE);*/
            mMain_toolbar.setVisibility(View.GONE);
        } else if (TextUtils.equals(tabId, "我的")) {
            mMain_toolbar.setVisibility(View.VISIBLE);
            onMineTabClick();

        }
    }

    /**
     * 下载更新完成
     * 部分机型,在UpdateReceiver中可能无法启动安装,转成文件到这这里再执行一次
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadApkComplete(Event.DownloadApkCompleteEvent event) {
        if (event.downloadFileUri == null) {
            return;
        }

        L.d("Event.onDownloadApkComplete");

        Uri uri = event.downloadFileUri;

        try {

            //转换成文件的方式，再安装一次
            if ("content".equals(uri.getScheme())) {
                String[] proj = {MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                cursor.moveToFirst();

                final String filePath = cursor.getString(0);
                cursor.close();
                uri = Uri.fromFile(new File(filePath));
            }

            if ("file".equals(uri.getScheme())) {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");

                startActivity(installIntent);
            }


        } catch (Exception ex) {
            L.e(ex, ex.getMessage());
            ToastUtils.show(AppController.getInstance(), "下载完成");
        }
    }

    /**
     * 消息列表
     */
    public void onMessageClick(View view) {
        WebActivity.open(this, "消息列表", Url.MESSAGE);
    }
}
