package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import net.skjr.wtq.R;
import net.skjr.wtq.business.BankListBiz;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.databinding.ActivityAddBankcardBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CityListAccount;
import net.skjr.wtq.model.account.DictInfo;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;
import net.skjr.wtq.ui.adapter.SelectAreaAdapter;
import net.skjr.wtq.viewmodel.AddBankCardViewModel;
import net.skjr.wtq.viewmodel.CheckResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/17 10:20
 * 描述	      添加银行卡
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AddBankCardActivity extends BaseToolbarActivity {

    private ActivityAddBankcardBinding mBinding;
    private BankListBiz mBiz;
    boolean isProcessing;
    boolean isCounting;

    //银行列表
    List<DictInfo.ListEntity> bankList;
    boolean isBankPop;

    //省列表
    List<CityListAccount.ListEntity> city1List;
    CityListAccount.ListEntity selectCity1;
    boolean isCity1Pop;

    //市列表
    List<CityListAccount.ListEntity> city2List;
    CityListAccount.ListEntity selectCity2;
    boolean isCity2Pop;
    Subscription timer;
    private ProjectListBiz mCitybiz;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("添加银行卡");
        initVariables();
        loadData();
    }

    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_bankcard);
        AddBankCardViewModel model = new AddBankCardViewModel();
        model.setUsername("持卡人：" );
        mBinding.setModel(model);
    }

    private void initVariables() {
        mBiz = new BankListBiz();
        mCitybiz = new ProjectListBiz();
    }
    private void loadData() {
        //获取银行列表
        loadBankData();

        //获取省列表
        loadCity1Data();
    }
    /**
     * 获取银行列表
     */
    private void loadBankData() {

        Subscription s = mBiz.getBankList()
                .subscribe(new Action1<APIResult<DictInfo>>() {
                    @Override
                    public void call(APIResult<DictInfo> listAPIResult) {
                        onGetBankListComplete(listAPIResult);
                    }
                });

        addSubscription(s);
    }

    /**
     * 获取银行数据完成的通知
     *
     * @param event
     */
    public void onGetBankListComplete(APIResult<DictInfo> event) {
        if (event.isSuccess) {
            DictInfo result = event.result;
            bankList = result.list;
            if (bankList != null && bankList.size() > 0)
                mBinding.bank.setText(bankList.get(0).name);
        } else {
            showToast("无法获取银行数据");
        }
    }


    /**
     * 下拉选择银行
     *
     * @param view
     */
    public void onSelectBankClick(View view) {
        if (isBankPop) {
            isBankPop = false;
            return;
        }
        isBankPop = true;

        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);

        List<String> list = new ArrayList<>();
        for(DictInfo.ListEntity li : bankList) {
            list.add(li.name);
        }
        //设置ListView类型的适配器
        SelectAreaAdapter adapter = new SelectAreaAdapter(AddBankCardActivity.this, list);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                mBinding.bank.setText(bankList.get(position).name);
                mBinding.getModel().setBankCode(bankList.get(position).code);
                isBankPop = false;
            }
        });

        listPopupWindow.setAnchorView(mBinding.bank);
        //设置对话框的宽高
        listPopupWindow.setWidth(250);
        listPopupWindow.setHeight(600);
        listPopupWindow.setModal(false);

        listPopupWindow.show();
    }

    /**
     * 获取省列表
     */
    private void loadCity1Data() {

        Subscription s2 = mCitybiz.getProvinceList(1)
                .subscribe(new Action1<APIResult<CityListAccount>>() {
                    @Override
                    public void call(APIResult<CityListAccount> listAPIResult) {
                        onGetCity1ListComplete(listAPIResult);
                    }
                });
        addSubscription(s2);
    }

    /**
     * 获取省份数据完成的通知
     *
     * @param event
     */
    public void onGetCity1ListComplete(APIResult<CityListAccount> event) {
        if (event.isSuccess) {
            city1List = event.result.list;

            if (city1List != null && city1List.size() > 0)
                setFirstCity1(city1List.get(0));
        } else {
            showToast("无法获取省份数据");
        }
    }

    /**
     * 获取省份数据后，默认显示第一项
     *
     * @param city
     */
    private void setFirstCity1(CityListAccount.ListEntity city) {
        mBinding.city1.setText(city.class_name);
        selectCity1 = city;

        //同时去获取对应的城市数据
        city2List = null;
        selectCity2 = null;

        //获取对应的市数据
        loadCity2Data(selectCity1.class_id);
    }

    /**
     * 下拉选择省
     *
     * @param view
     */
    public void onSelectCity1Click(View view) {
        if (isCity1Pop) {
            isCity1Pop = false;
            return;
        }
        isCity1Pop = true;

        if (city1List == null || city1List.size() == 0)
            return;

        final String items[] = mBiz.getCityItems(city1List);
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);

        //设置ListView类型的适配器
        listPopupWindow.setAdapter(new ArrayAdapter<>(AddBankCardActivity.this, android.R.layout.simple_list_item_1, items));
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                selectCity1 = city1List.get(position);
                mBinding.city1.setText(selectCity1.class_name);

                //获取对应的市数据
                loadCity2Data(selectCity1.class_id);
                isCity1Pop = false;
            }
        });

        listPopupWindow.setAnchorView(mBinding.city1);
        //设置对话框的宽高
        //listPopupWindow.setWidth(cardAddCity1.getWidth());
        listPopupWindow.setHeight(600);
        listPopupWindow.setModal(false);

        listPopupWindow.show();
    }

    /**
     * 获取某个省对应的市数据
     *
     * @param classid 省的classid
     */
    private void loadCity2Data(int classid) {
        Subscription s = mCitybiz.getProvinceList(classid)
                .subscribe(new Action1<APIResult<CityListAccount>>() {
                    @Override
                    public void call(APIResult<CityListAccount> listAPIResult) {
                        onGetCity2ListComplete(listAPIResult);
                    }
                });
        addSubscription(s);
    }

    /**
     * 获取市数据完成的通知
     *
     * @param event
     */
    public void onGetCity2ListComplete(APIResult<CityListAccount> event) {
        if (event.isSuccess) {
            city2List = event.result.list;

            if (city2List != null && city2List.size() > 0)
                setFirstCity2(city2List.get(0));
        } else {
            showToast("无法获取城市数据");
        }
    }

    /**
     * 获取城市数据后，默认显示第一项
     *
     * @param city
     */
    private void setFirstCity2(CityListAccount.ListEntity city) {
        mBinding.city2.setText(city.class_name);
        selectCity2 = city;

    }

    /**
     * 下拉选择市
     *
     * @param view
     */
    public void onSelectCity2Click(View view) {
        if (isCity2Pop) {
            isCity2Pop = false;
            return;
        }
        isCity2Pop = true;

        if (city2List == null || city2List.size() == 0)
            return;

        final String items[] = mBiz.getCityItems(city2List);
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);

        //设置ListView类型的适配器
        listPopupWindow.setAdapter(new ArrayAdapter<>(AddBankCardActivity.this, android.R.layout.simple_list_item_1, items));
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                selectCity2 = city2List.get(position);
                mBinding.city2.setText(selectCity2.class_name);

                isCity2Pop = false;
            }
        });

        listPopupWindow.setAnchorView(mBinding.city2);
        //设置对话框的宽高
        //        listPopupWindow.setWidth(cardAddCity2.getWidth());
        listPopupWindow.setHeight(600);
        listPopupWindow.setModal(false);

        listPopupWindow.show();
    }

    /**
     * 获取短信验证码
     *
     * @param view
     */
    public void onAddCardGetCaptchaClick(View view) {
        if (isProcessing)
            return;

        if (isCounting)
            return;

        CheckResult check = mBinding.getModel().check();
        if(!check.isSuccess) {
            showToast(check.errorMessage);
            return;
        }

        isProcessing = true;
        isCounting = true;
        mBinding.captcha.setText("");

        Subscription s = mBiz.sendAddCardSmsCode(mBinding.getModel().getMobileno())
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onSendSmsCodeComplete(apiResult);
                    }
                });
        addSubscription(s);

        //倒计时
        final int i = Consts.GET_SMSCODE_TIME;
        countDownTimer = new CountDownTimer(i * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.captcha.setTextColor(AddBankCardActivity.this.getResources().getColor(R.color.whole_style_color));
                mBinding.captcha.setText("等待" + millisUntilFinished / 1000 + "秒");
            }

            public void onFinish() {
                mBinding.captcha.setTextColor(AddBankCardActivity.this.getResources().getColor(R.color.whole_style_color));
                mBinding.captcha.setText("重新获取");
                isCounting = false;
            }
        }.start();
    }

    /**
     * 接收验证码请求的结果
     *
     * @param event
     */
    public void onSendSmsCodeComplete(APIResult<Object> event) {
        isProcessing = false;

        if (event.isSuccess) {

            showToast("验证码已发送");
        } else {
            showToast(event.message);
            //countDownTimer.cancel();
        }

    }

    /**
     * 添加银行卡
     *
     * @param view
     */
    public void onAddCardClick(View view) {
        if (isProcessing)
            return;

        AddBankCardViewModel model = mBinding.getModel();
        CheckResult checkResult = model.check();
        if (!checkResult.isSuccess) {
            showToast(checkResult.errorMessage);
            return;
        }
        if(model.getVerifyCode() == null) {
            showToast("请填写验证码");
            return;
        }

        String bankcode = model.getBankCode();
        String bank = model.getBank();
        String account = model.getAccountCard();
        String province = model.getProvince();
        String city = model.getCity();
        String mobile = model.getMobileno();
        String code = model.getVerifyCode();
        showProgressDialog();
        isProcessing = true;
        Subscription s = mBiz.bindingBank(bankcode,bank,account,province,city,mobile,code)
                .subscribe(new Action1<APIResult<Object>>() {
                    @Override
                    public void call(APIResult<Object> apiResult) {
                        onAddBankCardComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    /**
     * 添加银行卡完成的通知
     *
     * @param event
     */
    public void onAddBankCardComplete(APIResult<Object> event) {
        dismissProgressDialog();
        isProcessing = false;

        if (event.isSuccess) {
            //通知银行卡列表界面刷新
            EventBus.getDefault().post(new Event.AddBankCardCompleteEvent(true, ""));
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","添加成功");
            intent.putExtra("toolbartext","添加银行卡");
            startActivity(intent);
            finish();
        } else {
            showToast(event.message);
        }
    }
}
