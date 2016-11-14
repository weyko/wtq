package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.business.SystemBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CheckUserAccount;
import net.skjr.wtq.model.account.ProjectDetailAccount;
import net.skjr.wtq.ui.activity.system.DiscussListActivity;
import net.skjr.wtq.ui.activity.system.InvestorAuthActivity;
import net.skjr.wtq.ui.activity.system.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;
import rx.functions.Action1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/26 10:34
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectDetailsActivity extends BaseToolbarActivity {

    private ProjectListBiz mBiz;
    private ImageView mDetail_pic;
    private ImageView mDetail_collect_img;
    private TextView mDetail_title;
    private TextView mDetail_status;
    private TextView mDetail_industry;
    private TextView mDetail_area;
    private TextView mDetail_stable_income;
    private TextView mDetail_results_income;
    private TextView mDetail_honour_income;
    private TextView mDetail_ensure_money;
    private TextView mDetail_promise_buy;
    private ImageView mDetail_lead_img;
    private TextView mDetail_lead_money;
    private TextView mDetail_lead_mood;
    private TextView mDetail_lead_name;
    private ImageView mDetail_user_img;
    private TextView mDetail_user_name;
    private TextView mDetail_user_area;
    private TextView mDetail_discuss;
    private TextView mDetail_collect;
    private TextView mDetail_order;
    private TextView mDetail_yure_money;
    private TextView mDetail_yure_day;
    private TextView mDetail_chouzi_speed;
    private TextView mDetail_chouzi_already;
    private TextView mDetail_chouzi_aim;
    private TextView mDetail_chouzi_day;
    private ProgressBar mDetail_chouzi_progress;
    private TextView mDetail_fenhong_new;
    private TextView mDetail_fenhong_all;
    private TextView mDetail_fenhong_num;
    private TextView mDetail_fenhong_date;
    private View mDetail_yure;
    private View mDetail_chouzi;
    private View mDetail_fenhong;
    private View mDetail_progress;
    private View mDetail_view;
    private String mPid;
    private View mDetail_leadman;
    private int mStockID;
    private SystemBiz mSystemBiz;
    private View mDetail_favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        EventBus.getDefault().register(this);
        initToolbar("项目详情");
        initVariables();
        initView();
        initData();
    }

    private void initView() {
        mDetail_favorite = findViewById(R.id.detail_favorite);
        mDetail_pic = (ImageView) findViewById(R.id.detail_pic);
        mDetail_collect_img = (ImageView) findViewById(R.id.detail_collect_img);
        mDetail_title = (TextView) findViewById(R.id.detail_title);
        mDetail_status = (TextView) findViewById(R.id.detail_status);
        mDetail_industry = (TextView) findViewById(R.id.detail_industry);
        mDetail_area = (TextView) findViewById(R.id.detail_area);
        mDetail_leadman = findViewById(R.id.detail_leadman);
        //标签
        mDetail_stable_income = (TextView) findViewById(R.id.detail_stable_income);
        mDetail_results_income = (TextView) findViewById(R.id.detail_results_income);
        mDetail_honour_income = (TextView) findViewById(R.id.detail_honour_income);
        mDetail_ensure_money = (TextView) findViewById(R.id.detail_ensure_money);
        mDetail_promise_buy = (TextView) findViewById(R.id.detail_promise_buy);

        mDetail_lead_img = (ImageView) findViewById(R.id.detail_lead_img);
        mDetail_lead_money = (TextView) findViewById(R.id.detail_lead_money);
        mDetail_lead_mood = (TextView) findViewById(R.id.detail_lead_mood);
        mDetail_lead_name = (TextView) findViewById(R.id.detail_lead_name);

        mDetail_user_img = (ImageView) findViewById(R.id.detail_user_img);
        mDetail_user_name = (TextView) findViewById(R.id.detail_user_name);
        mDetail_user_area = (TextView) findViewById(R.id.detail_user_area);

        mDetail_discuss = (TextView) findViewById(R.id.detail_discuss);
        mDetail_collect = (TextView) findViewById(R.id.detail_collect);
        //认购或者预约
        mDetail_order = (TextView) findViewById(R.id.detail_order);
        //预热part
        mDetail_yure_money = (TextView) findViewById(R.id.detail_yure_money);
        mDetail_yure_day = (TextView) findViewById(R.id.detail_yure_day);
        //筹资part
        mDetail_chouzi_speed = (TextView) findViewById(R.id.detail_chouzi_speed);
        mDetail_chouzi_already = (TextView) findViewById(R.id.detail_chouzi_already);
        mDetail_chouzi_aim = (TextView) findViewById(R.id.detail_chouzi_aim);
        mDetail_chouzi_day = (TextView) findViewById(R.id.detail_chouzi_day);
        mDetail_chouzi_progress = (ProgressBar) findViewById(R.id.detail_chouzi_progress);
        //已分红 已解散
        mDetail_fenhong_new = (TextView) findViewById(R.id.detail_fenhong_new);
        mDetail_fenhong_all = (TextView) findViewById(R.id.detail_fenhong_all);
        mDetail_fenhong_num = (TextView) findViewById(R.id.detail_fenhong_num);
        mDetail_fenhong_date = (TextView) findViewById(R.id.detail_fenhong_date);

        mDetail_yure = findViewById(R.id.detail_yure);
        mDetail_chouzi = findViewById(R.id.detail_chouzi);
        mDetail_fenhong = findViewById(R.id.detail_fenhong);

        mDetail_progress = findViewById(R.id.detail_progress);
        mDetail_view = findViewById(R.id.detail_view);

    }

    private void initData() {
        mDetail_progress.setVisibility(View.VISIBLE);
        mDetail_view.setVisibility(View.GONE);
        Intent intent = getIntent();
        mPid = intent.getStringExtra("pid");
        //获取证件详情
        getDetailShow();
        //获取是否收藏
        loadCollectView();
        Subscription s = mBiz.getProjectDetail(mPid)
                .subscribe(new Action1<APIResult<ProjectDetailAccount>>() {
                    @Override
                    public void call(APIResult<ProjectDetailAccount> accountInfoAPIResult) {
                        onGetProjectDetailComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetProjectDetailComplete(APIResult<ProjectDetailAccount> apiResult) {
        if(apiResult.isSuccess) {
            ProjectDetailAccount result = apiResult.result;
            //获取项目id
            mStockID = result.projectInfo.stockID;
            //如果领投人id为空，则不显示
            if(result.projectInfo.leadUserID == 0) {
                mDetail_leadman.setVisibility(View.GONE);
            } else {
                mDetail_leadman.setVisibility(View.VISIBLE);
            }
            loadView(result);
            mDetail_progress.setVisibility(View.GONE);
            mDetail_view.setVisibility(View.VISIBLE);
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 加载页面
     */
    private void loadView(ProjectDetailAccount result) {
        ProjectDetailAccount.ProjectInfoEntity info = result.projectInfo;
        ImageLoaderUtils.displayImage(this, info.stockImg, mDetail_pic);
        //显示状态
        initStatus(info);
        mDetail_title.setText(info.stockTitle);
        mDetail_industry.setText(info.industryName);
        mDetail_area.setText(info.cityName);
        loadTabInfo(info);

        mDetail_lead_name.setText(info.leadUserNickname);
        mDetail_lead_money.setText("领投 ￥"+info.sunLeadTenderMoney);
        mDetail_lead_mood.setText(info.userMood);
        mDetail_user_name.setText(info.userNickname);
        mDetail_user_area.setText(info.userCityName);
        mDetail_discuss.setText("评论（"+info.pinglunCount+"）");
        mDetail_collect.setText(info.favoriteCount+"");

    }

    private void loadTabInfo(ProjectDetailAccount.ProjectInfoEntity info) {
        if(info.stableIncome == 0) {
            mDetail_stable_income.setText("稳定收益 无");
            mDetail_stable_income.setBackgroundResource(R.drawable.detail_tag_none_shape);
            mDetail_stable_income.setTextColor(getResources().getColor(R.color.project_type_no));
        } else {
            mDetail_stable_income.setText("稳定收益"+info.stableIncome*100+"%");
            mDetail_stable_income.setBackgroundResource(R.drawable.detail_tag_shape);
            mDetail_stable_income.setTextColor(getResources().getColor(R.color.project_type_yes));
        }
        if(info.resultsIncome == 0) {
            mDetail_results_income.setText("业绩收益 无");
            mDetail_results_income.setBackgroundResource(R.drawable.detail_tag_none_shape);
            mDetail_results_income.setTextColor(getResources().getColor(R.color.project_type_no));
        } else {
            mDetail_results_income.setText("业绩收益"+info.resultsIncome*100+"%");
            mDetail_results_income.setBackgroundResource(R.drawable.detail_tag_shape);
            mDetail_results_income.setTextColor(getResources().getColor(R.color.project_type_yes));
        }
        if(info.honourIncome == 0) {
            mDetail_honour_income.setText("尊享收益 无");
            mDetail_honour_income.setBackgroundResource(R.drawable.detail_tag_none_shape);
            mDetail_honour_income.setTextColor(getResources().getColor(R.color.project_type_no));
        } else {
            mDetail_honour_income.setText("尊享收益 有");
            mDetail_honour_income.setBackgroundResource(R.drawable.detail_tag_shape);
            mDetail_honour_income.setTextColor(getResources().getColor(R.color.project_type_yes));
        }
        if(info.ensureMoney == 0) {
            mDetail_ensure_money.setText("保证金 无");
            mDetail_ensure_money.setBackgroundResource(R.drawable.detail_tag_none_shape);
            mDetail_ensure_money.setTextColor(getResources().getColor(R.color.project_type_no));
        } else {
            mDetail_ensure_money.setText("保证金"+info.ensureMoney*100+"%");
            mDetail_ensure_money.setBackgroundResource(R.drawable.detail_tag_shape);
            mDetail_ensure_money.setTextColor(getResources().getColor(R.color.project_type_yes));
        }
        if(info.promiseBuy == 1) {
            mDetail_promise_buy.setText("承诺回购 有");
            mDetail_promise_buy.setBackgroundResource(R.drawable.detail_tag_shape);
            mDetail_promise_buy.setTextColor(getResources().getColor(R.color.project_type_yes));
        } else {
            mDetail_promise_buy.setText("承诺回购 无");
            mDetail_promise_buy.setBackgroundResource(R.drawable.detail_tag_none_shape);
            mDetail_promise_buy.setTextColor(getResources().getColor(R.color.project_type_no));
        }
    }

    /**
     * 显示不同的状态，预热中，筹资中。。。
     * @param
     */
    private void initStatus(ProjectDetailAccount.ProjectInfoEntity info) {
        switch (info.stockStatus) {
            case Consts.YURE:
                yureView(info);
                break;
            case Consts.CHOUZI:
                chouziView(info);
                break;
            case Consts.FENHONG:
                fenhongView(info);
                break;
            case Consts.JIESAN:
                jiesanView(info);
                break;
        }
    }

    /**
     * 已解散状态
     */
    private void jiesanView(ProjectDetailAccount.ProjectInfoEntity info) {
        mDetail_status.setText("已解散");
        mDetail_status.setBackgroundResource(R.drawable.project_jiesan_shape);
        mDetail_fenhong.setVisibility(VISIBLE);
        mDetail_yure.setVisibility(GONE);
        mDetail_chouzi.setVisibility(GONE);
        mDetail_order.setBackgroundColor(getResources().getColor(R.color.status_fenhong));
        mDetail_order.setFocusable(false);
    }
    /**
     * 已分红状态
     */
    private void fenhongView(ProjectDetailAccount.ProjectInfoEntity info) {
        mDetail_status.setText("已分红");
        mDetail_status.setBackgroundResource(R.drawable.project_fenhong_shape);
        mDetail_fenhong_new.setText("");
        mDetail_fenhong.setVisibility(VISIBLE);
        mDetail_yure.setVisibility(GONE);
        mDetail_chouzi.setVisibility(GONE);
        mDetail_order.setBackgroundColor(getResources().getColor(R.color.status_fenhong));
        mDetail_order.setFocusable(false);
    }
    /**
     * 筹资中状态
     */
    private void chouziView(ProjectDetailAccount.ProjectInfoEntity info) {
        mDetail_status.setText("筹资中");
        mDetail_fenhong.setVisibility(GONE);
        mDetail_yure.setVisibility(GONE);
        mDetail_chouzi.setVisibility(VISIBLE);
        mDetail_status.setBackgroundResource(R.drawable.project_chouzi_shape);
        mDetail_chouzi_speed.setText(info.speed+"%");
        mDetail_chouzi_day.setText(info.surplusTimeDay+"天");
        mDetail_chouzi_progress.setProgress(info.speed);
        mDetail_chouzi_already.setText("");
        mDetail_chouzi_aim.setText(info.stockMoneyFormat+"万元");
    }
    /**
     * 预热中状态
     */
    private void yureView(ProjectDetailAccount.ProjectInfoEntity info) {
        mDetail_status.setText("预热中");
        mDetail_fenhong.setVisibility(GONE);
        mDetail_yure.setVisibility(VISIBLE);
        mDetail_chouzi.setVisibility(GONE);
        mDetail_status.setBackgroundResource(R.drawable.project_yure_shape);
        mDetail_yure_money.setText(info.stockMoneyFormat+"万");
        mDetail_yure_day.setText(info.surplusTimeDay+"天");
        mDetail_order.setText("我要预约");
        mDetail_order.setBackgroundColor(getResources().getColor(R.color.status_yure));
    }

    private void initVariables() {
        mBiz = new ProjectListBiz();
        mSystemBiz = new SystemBiz();
    }

    public void onBuy(View view) {
        if(getMyApp().isNeedLogin()) {
            CommonUtils.showDialog(this, "亲，您还没有登录噢~", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProjectDetailsActivity.this, LoginActivity.class);
                    intent.putExtra("page", Consts.DETAIL);
                    startActivity(intent);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            },"暂不登录","现在登录");
        } else if(!hasAuthInvestor) {
            CommonUtils.showDialog(this, "亲，您还没有认证投资人噢~", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProjectDetailsActivity.this, InvestorAuthActivity.class);
                    intent.putExtra("page", Consts.DETAIL);
                    startActivity(intent);
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            },"暂不认证","现在认证");
        } else {
            Intent intent = new Intent(this, ConfirmOrderActivity.class);
            intent.putExtra("stockNo",mPid);
            startActivity(intent);
        }
    }

    /**
     * 证件展示详情
     */
    private void getDetailShow() {
        Subscription s = mBiz.getDetailShow(mPid)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> accountInfoAPIResult) {
                        onGetDetailShowComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetDetailShowComplete(APIResult<Object> apiResult) {
        if(apiResult.isSuccess) {

        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 初始化收藏按钮
     */
    private void loadCollectView() {
        if(!getMyApp().isNeedLogin()) {
            isCollect();
            //TODO:登录成功返回后，还要执行此方法
            isAuthInvestor();
        }
        mDetail_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getMyApp().isNeedLogin()) {
                    CommonUtils.showDialog(ProjectDetailsActivity.this, "亲，您还没有登录噢~", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProjectDetailsActivity.this, LoginActivity.class);
                            intent.putExtra("page", Consts.DETAIL);
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    },"暂不登录","现在登录");
                } else {
                    if(isFavorite) {
                        cancelCollectPro();  //取消收藏
                    } else {
                        collectPro();   //添加收藏
                    }
                }
            }
        });
    }


    /**
     * 评论列表
     */
    public void discussClick(View view) {
        Intent intent = new Intent(this, DiscussListActivity.class);
        intent.putExtra("stockid",mStockID);
        startActivity(intent);
    }

    /**
     * 是否认证投资人
     */
    private boolean hasAuthInvestor = false;
    private void isAuthInvestor() {
        Subscription s = mSystemBiz.isInvestor()
                .subscribe(new Action1<APIResult<CheckUserAccount>>() {
                    @Override
                    public void call(APIResult<CheckUserAccount> apiResult) {
                        if(apiResult.isSuccess) {
                            CheckUserAccount result = apiResult.result;
                            if (result.authStatus == 1) {
                                hasAuthInvestor = true;
                            }
                        } else {
                            showToast(apiResult.message);
                        }
                    }

                });

        addSubscription(s);
    }
    /**
     * 是否被收藏
     */
    private boolean isFavorite = false;
    private void isCollect() {
        Subscription s = mSystemBiz.isFavorite(mPid)
                .subscribe(new Action1<APIResult<CheckUserAccount>>() {
                    @Override
                    public void call(APIResult<CheckUserAccount> apiResult) {
                        if(apiResult.isSuccess) {
                            CheckUserAccount result = apiResult.result;
                            if (result.authStatus == 1) {
                                isFavorite = true;
                                Log.e("thread",Thread.currentThread().getName());
//                                mDetail_collect.setCompoundDrawables(getResources().getDrawable(R.drawable.mine_coll),null,null,null);
                                mDetail_collect_img.setImageDrawable(ProjectDetailsActivity.this.getResources().getDrawable(R.drawable.mine_mycoll));
                            } else {
                                isFavorite = false;
                                mDetail_collect_img.setImageDrawable(ProjectDetailsActivity.this.getResources().getDrawable(R.drawable.mine_uncoll));
                            }
                        } else {
                            showToast(apiResult.message);
                        }
                    }

                });

        addSubscription(s);
    }

    /**
     * 收藏项目
     */
    private void collectPro() {
        Subscription s = mBiz.collectProject(mPid)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        if(apiResult.isSuccess) {
                            showToast("收藏成功");
                            isCollect();
                        } else {
                            showToast(apiResult.message);
                        }
                    }

                });

        addSubscription(s);
    }
    /**
     * 取消收藏
     */
    private void cancelCollectPro() {
        Subscription s = mBiz.cancelCollect(mPid)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        if(apiResult.isSuccess) {
                            showToast("已取消");
                            isCollect();
                        } else {
                            showToast(apiResult.message);
                        }
                    }

                });

        addSubscription(s);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 登录完成后，需要重新验证投资人
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginComplete(Event.LoginCompleteEvent event) {
        isAuthInvestor();
    }

}
