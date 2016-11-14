package net.skjr.wtq.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.TabhomeAccount;
import net.skjr.wtq.ui.activity.EnterBrandsActivity;
import net.skjr.wtq.ui.activity.InvestGeniusActivity;
import net.skjr.wtq.ui.activity.ProjectDetailsActivity;
import net.skjr.wtq.ui.activity.ProjectListActivity;
import net.skjr.wtq.ui.activity.PublishPreActivity;
import net.skjr.wtq.ui.activity.WealthListActivity;
import net.skjr.wtq.ui.adapter.InvestChouziAdapter;
import net.skjr.wtq.ui.adapter.InvestPagerAdapter;
import net.skjr.wtq.ui.adapter.InvestYureAdapter;
import net.skjr.wtq.ui.widgets.CircleIndicator;
import net.skjr.wtq.ui.widgets.MyListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 投资
 */
public class TabInvestFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ViewPager mInvest_vp;
    private CircleIndicator mInvest_indicator;
    private TextView mInvest_newbee;
    private TextView mInvest_brands;
    private TextView mInvest_genius;
    private TextView mInvest_wealth_list;
    private TextView mViewById;
    private MyListView mPro_chouzi;
    private MyListView mPro_yure;
    private TextView mAll_project1;
    private TextView mAll_project2;
    private ScrollView mMain_scroll;
    private ImageView mInvest_havepro;
    private TabHomeBiz mBiz;
    private ArrayList<String> mBannerImg = new ArrayList<>();
    private TextView mCount_pro;
    private TextView mCount_money;
    private TextView mCount_user;
    private TextView mCount_fenhong;
    private List<TabhomeAccount.ChouziListEntity> mChouziList;
    private List<TabhomeAccount.YureListEntity> mYureList;
    private View mInvest_progress;
    private View mInvest_view;
    private SwipeRefreshLayout mInvest_refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_invest, null);
            initVariables();
            loadData();
            initView();
            initData();
        } else {
            ViewParent oldParent = rootView.getParent();
            if (oldParent != null) {
                if (oldParent != container) {
                    ((ViewGroup) oldParent).removeView(rootView);
                }
            }
        }

        return rootView;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Log.e("huang","initData  "+Thread.currentThread().getName());
        InvestPagerAdapter pagerAdapter = new InvestPagerAdapter(mActivity, mBannerImg);
        mInvest_vp.setAdapter(pagerAdapter);
        mInvest_indicator.setViewPager(mInvest_vp);

        mMain_scroll.smoothScrollTo(0, 0);

        mInvest_wealth_list.setOnClickListener(this);
        mAll_project1.setOnClickListener(this);
        mAll_project2.setOnClickListener(this);
        mInvest_brands.setOnClickListener(this);
        mInvest_havepro.setOnClickListener(this);
        mInvest_genius.setOnClickListener(this);

        mPro_chouzi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mActivity, ProjectDetailsActivity.class);
                intent.putExtra("pid",mChouziList.get(i).stockNO);
                startActivity(intent);
            }
        });
        mPro_yure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mActivity, ProjectDetailsActivity.class);
                intent.putExtra("pid",mYureList.get(i).stockNO);
                startActivity(intent);
            }
        });
    }

    private void initListView() {

        InvestChouziAdapter chouziAdapter = new InvestChouziAdapter(mActivity, mChouziList);
        mPro_chouzi.setAdapter(chouziAdapter);
        InvestYureAdapter yureAdapter = new InvestYureAdapter(mActivity, mYureList);
        mPro_yure.setAdapter(yureAdapter);
    }

    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        Subscription s = mBiz.getTabHomeAccount()
                .subscribe(new Action1<APIResult<TabhomeAccount>>() {
                    @Override
                    public void call(APIResult<TabhomeAccount> accountInfoAPIResult) {
                        onGetHomeAccountComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    /**
     * 处理数据
     */
    private void onGetHomeAccountComplete(APIResult<TabhomeAccount> apiResult) {
        if(apiResult.isSuccess) {
            if(mInvest_refresh.isRefreshing()) {
                mInvest_refresh.setRefreshing(false);
            }

            TabhomeAccount result = apiResult.result;
            List<TabhomeAccount.BannerListEntity> bannerList = result.bannerList;
            mBannerImg.clear();
            for(TabhomeAccount.BannerListEntity list : bannerList) {
                mBannerImg.add(list.imgUrl);
            }
            //筹资
            mChouziList = result.chouziList;
            mYureList = result.yureList;
            InvestPagerAdapter pagerAdapter = new InvestPagerAdapter(mActivity, mBannerImg);
            mInvest_vp.setAdapter(pagerAdapter);
            //轮播图自动轮播
            autoViewPager();
            Log.e("lunbo",mBannerImg.size()+"");
            mInvest_indicator.setViewPager(mInvest_vp);
            initListView();
            //统计
            initAllCount(result);

            mMain_scroll.setVisibility(View.VISIBLE);
            mInvest_progress.setVisibility(View.GONE);

        } else {
            showToast("服务器错误");
        }

    }

    int total = Integer.MAX_VALUE/2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (msg.arg1 != 0) {
                        mInvest_vp.setCurrentItem(msg.arg1);
                    } else {
                        //false 当从末页调到首页是，不显示翻页动画效果，
                        mInvest_vp.setCurrentItem(msg.arg1, false);
                    }
                    break;
            }
        }
    };
    Timer timer;
    boolean isProssing = false;
    private void autoViewPager() {
         if(timer == null) {
             timer = new Timer();
         }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isProssing) {
                    Message message = new Message();
                    message.what = 1;
                    int autoCurrIndex = mInvest_vp.getCurrentItem();
                    if (autoCurrIndex == mBannerImg.size() - 1) {
                        autoCurrIndex = -1;
                    }
                    message.arg1 = autoCurrIndex + 1;
                    mHandler.sendMessage(message);
                    isProssing = true;
                }
            }
        }, 0, 3000);

    }

    /**
     * 项目总计
     */
    private void initAllCount(TabhomeAccount result) {
        TabhomeAccount.CountListEntity countList = result.countList;
        mCount_pro.setText(countList.projectCount+"个");
        mCount_money.setText(countList.stockMoneyCount+"万元");
        mCount_user.setText(countList.userCount+"人");
        mCount_fenhong.setText(countList.bonusCount+"万元");
    }

    private void initView() {
        mInvest_refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.invest_refresh);
        mInvest_vp = (ViewPager) rootView.findViewById(R.id.invest_vp);
        mInvest_indicator = (CircleIndicator) rootView.findViewById(R.id.invest_indicator);
        mInvest_newbee = (TextView) rootView.findViewById(R.id.invest_newbee);
        mInvest_brands = (TextView) rootView.findViewById(R.id.invest_brands);
        mInvest_genius = (TextView) rootView.findViewById(R.id.invest_genius);
        mInvest_wealth_list = (TextView) rootView.findViewById(R.id.invest_wealth_list);
        mPro_chouzi = (MyListView) rootView.findViewById(R.id.invest_chouzi);
        mPro_yure = (MyListView) rootView.findViewById(R.id.invest_yure);
        mAll_project1 = (TextView) rootView.findViewById(R.id.all_project1);
        mAll_project2 = (TextView) rootView.findViewById(R.id.all_project2);
        mMain_scroll = (ScrollView) rootView.findViewById(R.id.main_scroll);
        mInvest_havepro = (ImageView) rootView.findViewById(R.id.invest_havepro);

        mCount_pro = (TextView) rootView.findViewById(R.id.count_pro);
        mCount_money = (TextView) rootView.findViewById(R.id.count_money);
        mCount_user = (TextView) rootView.findViewById(R.id.count_user);
        mCount_fenhong = (TextView) rootView.findViewById(R.id.count_fenhong);

        mInvest_progress = rootView.findViewById(R.id.invest_progress);
        mMain_scroll.setVisibility(View.GONE);

        initRefresh();


    }

    private void initRefresh() {
        mInvest_refresh.setColorSchemeResources(R.color.whole_style_color);
        mInvest_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invest_wealth_list: //财富榜
                startActivity(WealthListActivity.class);
                break;
            case R.id.all_project1:
            case R.id.all_project2:
                startActivity(ProjectListActivity.class);
                break;
            case R.id.invest_brands:
                startActivity(EnterBrandsActivity.class);
                break;
            case R.id.invest_havepro:
                startActivity(PublishPreActivity.class);
                break;
            case R.id.invest_genius:
                startActivity(InvestGeniusActivity.class);
                break;
        }
    }
}
