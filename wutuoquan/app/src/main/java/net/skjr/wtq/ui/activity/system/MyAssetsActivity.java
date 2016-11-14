package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.business.SystemBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.utils.CommonUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CheckUserAccount;
import net.skjr.wtq.model.account.MyAssetAccount;
import net.skjr.wtq.ui.activity.BankCardListActivity;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.ui.adapter.TradeRecordAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/11 16:54
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MyAssetsActivity extends BaseToolbarActivity {

    private ListView mTrade_record;
    private TextView mAssets_count;
    private TextView mAsset_aval;
    private TextView mAsset_freeze;
    private AccountBiz mBiz;

    //可用资金
    private String mAvail_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myassets);
        EventBus.getDefault().register(this);
        initToolbar("我的资产");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        Subscription s = mBiz.getMyAssets()
                .subscribe(new Action1<APIResult<MyAssetAccount>>() {
                    @Override
                    public void call(APIResult<MyAssetAccount> accountInfoAPIResult) {
                        onGetMyAssetsComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
        //判断是否实名
        isRealName();
    }

    private void onGetMyAssetsComplete(APIResult<MyAssetAccount> apiResult) {
        if(apiResult.isSuccess) {
            MyAssetAccount result = apiResult.result;
            MyAssetAccount.AccountInfoEntity accountInfo = result.accountInfo;
            mAssets_count.setText(accountInfo.totalMoney+"");
            mAsset_aval.setText(accountInfo.availableMoney+"");
            mAsset_freeze.setText(accountInfo.frozenMoney+"");
            mAvail_money = accountInfo.availableMoney+"";
            List<MyAssetAccount.AccountListEntity> accountList = result.accountList;
            TradeRecordAdapter adapter = new TradeRecordAdapter(this, accountList);
            mTrade_record.setAdapter(adapter);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initView() {
        mTrade_record = (ListView) findViewById(R.id.trade_record);
        mAssets_count = (TextView) findViewById(R.id.asset_count);
        mAsset_aval = (TextView) findViewById(R.id.asset_aval);
        mAsset_freeze = (TextView) findViewById(R.id.asset_freeze);
        ImageView bankcard = (ImageView) findViewById(R.id.bankcard);
        bankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(BankCardListActivity.class);
            }
        });
    }

    private void initVariables() {
        mBiz = new AccountBiz();
    }

    /**
     * 提现
     */
    public void getCashClick(View view) {

    }
    /**
     * 充值
     */
    public void rechargeClick(View view) {
        if(mRealName) {
            Intent intent = new Intent(this, RechargeActivity.class);
            intent.putExtra("avail_money", mAvail_money);
            startActivity(intent);
        } else {
            //未实名
            CommonUtils.showDialog(this, "亲，您还没有进行实名认证噢~", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyAssetsActivity.this, RealNameAuthActivity.class);
                    startActivity(intent);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            },"暂不认证","现在认证");
        }
    }

    /**
     * 是否实名
     * @return
     */
    private boolean mRealName = false;
    private void isRealName() {
        SystemBiz biz = new SystemBiz();
        Subscription s = biz.isRealName()
                .subscribe(new Action1<APIResult<CheckUserAccount>>() {
                    @Override
                    public void call(APIResult<CheckUserAccount> apiResult) {
                        if(apiResult.isSuccess) {
                            CheckUserAccount result = apiResult.result;
                            if (result.authStatus == 1) {
                                mRealName = true;
                            }
                        } else {
                            showToast(apiResult.message);
                        }
                    }

                });

        addSubscription(s);
    }

    /**
     * 刷新实名认证状态
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetTradePwdComplete(Event.AuthRealNameInvestorCompleteEvent event) {
        isRealName();
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
