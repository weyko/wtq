package net.skjr.wtq.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.TabMineAccount;
import net.skjr.wtq.ui.activity.MyCollectedActivity;
import net.skjr.wtq.ui.activity.MyInvestActivity;
import net.skjr.wtq.ui.activity.MyPublishedActivity;
import net.skjr.wtq.ui.activity.system.MyAssetsActivity;
import net.skjr.wtq.ui.activity.system.SettingActivity;
import net.skjr.wtq.ui.activity.system.UserInfoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 账户
 */
public class TabMineFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private TextView mMine_myinvest;
    private TextView mMine_mycollect;
    private TextView mMine_mypublished;
    private TabHomeBiz mBiz;
    private ImageView mMine_head;
    private TextView mMine_name;
    private TextView mMine_tel;
    private TextView mMine_myassets;
    private TextView mMine_fenhong_count;
    private TextView mMine_aval_asset;
    private TextView mMine_commission_count;
    private TextView mMine_toview;
    private View mMine_progress;
    private View mMine_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initVariables();
        Log.e("wode","onCreate");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshTabMine(Event.RefreshTabMine event) {
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_mine, null);
            initView();
        } else {
            ViewParent oldParent = rootView.getParent();
            if (oldParent != null) {
                if (oldParent != container) {
                    ((ViewGroup) oldParent).removeView(rootView);
                }
            }
        }
            initData();
        return rootView;
    }

    private void initData() {
        Subscription s = mBiz.getTabMineAccount()
                .subscribe(new Action1<APIResult<TabMineAccount>>() {
                    @Override
                    public void call(APIResult<TabMineAccount> accountInfoAPIResult) {
                        onGetMineAccountComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetMineAccountComplete(APIResult<TabMineAccount> apiResult) {
        if(apiResult.isSuccess) {
            TabMineAccount result = apiResult.result;
            TabMineAccount.UserInfoAccountEntity info = result.userInfoAccount;
            if(info == null) {
               return;
            }
            if(!TextUtils.isEmpty(info.userImg)) {
                ImageLoaderUtils.displayImage(mActivity, info.userImg,mMine_head);
            }
            mMine_name.setText(info.userNickname);
            mMine_tel.setText(info.phone);
            mMine_aval_asset.setText(info.availableMoney+"");
            TabMineAccount.LeijiCountEntity count = result.leijiCount;
            mMine_fenhong_count.setText(count.fenhongCount+"");
            mMine_commission_count.setText(count.yongjinCount+"");
            mMine_myassets.setText(info.availableMoney+"");

            mMine_view.setVisibility(View.VISIBLE);
            mMine_progress.setVisibility(View.GONE);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    private void initView() {
        ImageView toolbarRight = (ImageView) mActivity.findViewById(R.id.toolbarRight);
        mMine_myinvest = (TextView) rootView.findViewById(R.id.mine_myinvest);
        mMine_mycollect = (TextView) rootView.findViewById(R.id.mine_mycollect);
        mMine_mypublished = (TextView) rootView.findViewById(R.id.mine_mypublished);
        mMine_toview = (TextView) rootView.findViewById(R.id.mine_toview);
        View mine_userinfo = rootView.findViewById(R.id.mine_userinfo);

        mMine_head = (ImageView) rootView.findViewById(R.id.mine_head);
        mMine_name = (TextView) rootView.findViewById(R.id.mine_name);
        mMine_tel = (TextView) rootView.findViewById(R.id.mine_tel);
        mMine_myassets = (TextView) rootView.findViewById(R.id.mine_myassets);
        mMine_fenhong_count = (TextView) rootView.findViewById(R.id.mine_fenhong_count);
        mMine_aval_asset = (TextView) rootView.findViewById(R.id.mine_aval_asset);
        mMine_commission_count = (TextView) rootView.findViewById(R.id.mine_commission_count);

        mMine_progress = rootView.findViewById(R.id.mine_progress);
        mMine_view = rootView.findViewById(R.id.mine_view);
        mMine_view.setVisibility(View.GONE);

        mMine_myinvest.setOnClickListener(this);
        mMine_mycollect.setOnClickListener(this);
        mMine_mypublished.setOnClickListener(this);
        mMine_toview.setOnClickListener(this);
        mine_userinfo.setOnClickListener(this);
        toolbarRight.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_myinvest:
                startActivity(MyInvestActivity.class);
                break;
            case R.id.mine_mycollect:
                startActivity(MyCollectedActivity.class);
                break;
            case R.id.mine_mypublished:
                startActivity(MyPublishedActivity.class);
                break;
            case R.id.mine_toview:
                startActivity(MyAssetsActivity.class);
                break;
            case R.id.toolbarRight:
                startActivity(SettingActivity.class);
                break;
            case R.id.mine_userinfo:
                startActivity(UserInfoActivity.class);
                break;
        }
    }

    /**
     * 充值完成
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetTradePwdComplete(Event.RechargeCompleteEvent event) {
        initData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
