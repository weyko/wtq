package net.skjr.wtq.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.BankListBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.BankListAccount;
import net.skjr.wtq.ui.activity.system.AddBankCardActivity;
import net.skjr.wtq.ui.adapter.BankCardListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/18 9:22
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class BankCardListActivity extends BaseToolbarActivity {

    private ListView mBank_list;
    private ImageView mAdd_bank;
    private BankListBiz mBiz;
    boolean isProcessing;

    private List<BankListAccount.ListEntity> mCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard_list);
        EventBus.getDefault().register(this);
        initToolbar("银行卡");
        initView();
        initVariables();
        initData();
    }
    /**
     * EventBus：添加银行卡完成的通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddBankCardComplete(Event.AddBankCardCompleteEvent event) {
        if (event.isSuccess) {
            //如果添加成功，那么刷新列表
            initData();
        }
    }

    private void initVariables() {
        mBiz = new BankListBiz();
    }

    private void initData() {
        isProcessing = true;
        showProgressDialog();
        Subscription s = mBiz.getBankCardList()
                .subscribe(new Action1<APIResult<BankListAccount>>() {
                    @Override
                    public void call(APIResult<BankListAccount> apiResult) {
                        onGetBankListComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onGetBankListComplete(APIResult<BankListAccount> apiResult) {
        if(apiResult.isSuccess) {
            isProcessing = false;
            dismissProgressDialog();

            BankListAccount result = apiResult.result;
            mCardList = result.list;
            BankCardListAdapter adapter = new BankCardListAdapter(this, mCardList);
            mBank_list.setAdapter(adapter);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initView() {
        mBank_list = (ListView) findViewById(R.id.bank_list);
        mAdd_bank = (ImageView) findViewById(R.id.add_bank);
        mAdd_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AddBankCardActivity.class);
            }
        });

    }

    /**
     * 设置提现卡
     * @param view
     * @param position
     */
    public void onSetTypeClick(View view, int position) {
        if (isProcessing)
            return;

        //showToast("设置为提现卡");

        BankListAccount.ListEntity item = mCardList.get(position);
        if (item == null)
            return;

        isProcessing = true;
        showProgressDialog();
        String id = item.accountBankID;
        Subscription s = mBiz.setCashBankCard(id)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        isProcessing = false;
                        dismissProgressDialog();

                        if (apiResult.isSuccess) {
                            //设置成功，刷新列表
                            initData();
                        } else {
                            showToast(apiResult.message);
                        }
                    }
                });

        addSubscription(s);
    }

    /**
     * 删除
     * @param view
     * @param position
     */
    public void onListItemClick(View view, int position) {

        BankListAccount.ListEntity card = mCardList.get(position);

        if(card == null)
            return;

        showDeleteDialog(card);
    }

    /**
     * 显示删除对话框
     *
     */
    private void showDeleteDialog(final BankListAccount.ListEntity card){
        final AppCompatDialog dialog = new AppCompatDialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_delete);

        dialog.getWindow().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                showProgressDialog();
                String id = card.accountBankID;
                Subscription s = mBiz.deleteBankCard(id)
                        .subscribe(new Action1<APIResult<Object>>() {
                            @Override
                            public void call(APIResult<Object> apiResult) {
                                isProcessing = false;
                                dismissProgressDialog();

                                if (apiResult.isSuccess) {
                                    //删除成功，刷新列表
                                    initData();
                                } else {
                                    showToast(apiResult.message);
                                }
                            }
                        });

                addSubscription(s);


            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
