package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.LianlianPayResult;
import net.skjr.wtq.model.account.OrderInfoAccount;
import net.skjr.wtq.model.requestobj.OrderInfoObj;
import net.skjr.wtq.ui.activity.system.SuccessActivity;
import net.skjr.wtq.ui.activity.system.WebActivity;
import net.skjr.wtq.ui.widgets.AddMinusView;
import net.skjr.wtq.utils.CommenUtils;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/30 15:03
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ConfirmOrderActivity extends BaseToolbarActivity implements CompoundButton.OnCheckedChangeListener {

    private AddMinusView mBuy_add_minus;
    private ImageView mBuy_img;
    private TextView mBuy_title;
    private TextView mBuy_desc;
    private TextView mBuy_price;
    private TextView mBuy_rate;
    private TextView mBuy_promiss;
    private TextView mBuy_promiss_money;
    private TextView mBuy_aval_money;
    private TextView mBuy_total_price;
    private CheckBox mBuy_apply_lead;
    private CheckBox mBuy_read_protocol;
    private CheckBox mBuy_pay_type1;
    private CheckBox mBuy_pay_type2;
    private CheckBox mBuy_pay_type3;
    private AccountBiz mBiz;
    private OrderInfoAccount.DataEntity mStockInfo;

    /**
     * 支付方式
     * 1.微信支付   2.连连支付    3.余额支付
     */
    private int mPayType = 1;

    //提交订单对象
    private OrderInfoObj mOrderInfo = new OrderInfoObj();
    private View mPay_type3_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        initToolbar("确认订单");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String stockNo = intent.getStringExtra("stockNo");
        mOrderInfo.projectNO = stockNo;
        Subscription s = mBiz.getBuyOrderInfo(stockNo)
                .subscribe(new Action1<APIResult<OrderInfoAccount>>() {
                    @Override
                    public void call(APIResult<OrderInfoAccount> accountInfoAPIResult) {
                        onGetOrderAccountComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetOrderAccountComplete(APIResult<OrderInfoAccount> apiResult) {
        if(apiResult.isSuccess) {
            OrderInfoAccount result = apiResult.result;
            mStockInfo = result.data;
            mOrderInfo.nameGoods = mStockInfo.stockTitle;
            loadView();
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 加载数据
     */
    private void loadView() {
        ImageLoaderUtils.displayImage(this, mStockInfo.stockImg, mBuy_img);
        mBuy_title.setText(mStockInfo.stockTitle);
        mBuy_desc.setText(mStockInfo.sketch);
        mBuy_aval_money.setText(mStockInfo.availableMoney+"");
        mBuy_add_minus.mView_edit.setText((mStockInfo.fraction - mStockInfo.buyFraction)+"");
        Consts.MAX_BUY = mStockInfo.fraction - mStockInfo.buyFraction;
        mOrderInfo.number = Consts.MAX_BUY;
        listenEdittext(mStockInfo.fraction - mStockInfo.buyFraction);
        //根据份数确定金额
        mBuy_add_minus.mView_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0) {
                    return;
                }
                int num = Integer.valueOf(charSequence.toString());
                mOrderInfo.number = num;
                listenEdittext(num);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 根据认购份数确定金额
     * @param num
     */
    private void listenEdittext(double num) {
        double price = mStockInfo.stockMoney/mStockInfo.fraction * num;
        double rate = num/mStockInfo.fraction * mStockInfo.stockShares;
        //将显示的数字格式化为两位小数

        String format_price = CommenUtils.decimalFormat(price, 2);
        String format_rate = CommenUtils.decimalFormat(rate, 2);

        mBuy_price.setText(format_price+"");
        mBuy_rate.setText(format_rate+"%");
        mBuy_promiss.setText(format_price+"元*"+mStockInfo.baozhenMoneyLilv+"%=");
        double money = price*mStockInfo.baozhenMoneyLilv;

        String format_money = CommenUtils.decimalFormat(money, 2);
        mBuy_promiss_money.setText(format_money+"元");
        mBuy_total_price.setText(format_money+"元");
    }

    private void initView() {
        mBuy_img = (ImageView) findViewById(R.id.buy_img);
        mBuy_title = (TextView) findViewById(R.id.buy_title);
        mBuy_desc = (TextView) findViewById(R.id.buy_desc);

        mBuy_price = (TextView) findViewById(R.id.buy_price);
        mBuy_rate = (TextView) findViewById(R.id.buy_rate);
        mBuy_promiss = (TextView) findViewById(R.id.buy_promiss);
        mBuy_promiss_money = (TextView) findViewById(R.id.buy_promiss_money);
        mBuy_aval_money = (TextView) findViewById(R.id.buy_aval_money);
        mBuy_total_price = (TextView) findViewById(R.id.buy_total_price);

        mBuy_apply_lead = (CheckBox) findViewById(R.id.buy_apply_lead);
        mBuy_read_protocol = (CheckBox) findViewById(R.id.buy_read_protocol);
        mBuy_pay_type1 = (CheckBox) findViewById(R.id.buy_pay_type1);
        mBuy_pay_type2 = (CheckBox) findViewById(R.id.buy_pay_type2);
        mBuy_pay_type3 = (CheckBox) findViewById(R.id.buy_pay_type3);

        mBuy_add_minus = (AddMinusView) findViewById(R.id.buy_add_minus);
        mPay_type3_view = findViewById(R.id.pay_type3_view);

        mBuy_pay_type1.setChecked(true);
        mBuy_read_protocol.setChecked(true);
        mOrderInfo.readStatus = 1;
        initCheckBox();
    }

    private void initCheckBox() {
        mBuy_apply_lead.setOnCheckedChangeListener(this);
        mBuy_read_protocol.setOnCheckedChangeListener(this);
        mBuy_pay_type1.setOnCheckedChangeListener(this);
        mBuy_pay_type2.setOnCheckedChangeListener(this);
        mBuy_pay_type3.setOnCheckedChangeListener(this);

        mPay_type3_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBuy_pay_type3.setChecked(true);
            }
        });

    }

    private void initVariables() {
        mBiz = new AccountBiz();
    }

    public void pay(View view) {
        switch (mPayType) {
            case 1:
                weixinPay();   //微信支付
                break;
            case 2:
                lianLianPay();  //连连支付
                break;
            case 3:
                yuEPay();     //余额支付
                break;
        }

    }

    /**
     * 余额支付
     */
    private void yuEPay() {
        Subscription s = mBiz.availPay(mOrderInfo)
                .subscribe(new Action1<APIResult<LianlianPayResult>>() {
                    @Override
                    public void call(APIResult<LianlianPayResult> accountInfoAPIResult) {
                        onAvailPayComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onAvailPayComplete(APIResult<LianlianPayResult> apiResult) {
        if(apiResult.isSuccess) {
            finish();
            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("successtext","支付成功");
            intent.putExtra("toolbartext","支付");
            startActivity(intent);
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 微信支付
     */
    private void weixinPay() {

    }
    /**
     * 连连支付
     */
    private void lianLianPay() {
        Subscription s = mBiz.gotoPay(mOrderInfo)
                .subscribe(new Action1<APIResult<LianlianPayResult>>() {
                    @Override
                    public void call(APIResult<LianlianPayResult> accountInfoAPIResult) {
                        onPayComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }
    private void onPayComplete(APIResult<LianlianPayResult> apiResult) {
        if (apiResult.isSuccess) {
            finish();
            LianlianPayResult result = apiResult.result;
            WebActivity.open(this, "支付", result.paymenUrl+"?req_data="+result.reqData);
        } else {
            showToast(apiResult.message);
        }

    }





    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.buy_apply_lead:    //是否领投
                if(b) {
                    mOrderInfo.firstPersonStatus = 1;
                } else {
                    mOrderInfo.firstPersonStatus = 0;
                }
                break;
            case R.id.buy_read_protocol:  //阅读协议
                if(b) {
                    mOrderInfo.readStatus = 1;
                } else {
                    mOrderInfo.readStatus = 0;
                }
                break;
            case R.id.buy_pay_type1:     //支付方式
                if(b) {
                    mPayType = 1;
                    mBuy_pay_type2.setChecked(false);
                    mBuy_pay_type3.setChecked(false);
                }
                break;
            case R.id.buy_pay_type2:
                if(b) {
                    mPayType = 2;
                    mBuy_pay_type1.setChecked(false);
                    mBuy_pay_type3.setChecked(false);
                }
                break;
            case R.id.buy_pay_type3:
                if(b) {
                    mPayType = 3;
                    mBuy_pay_type2.setChecked(false);
                    mBuy_pay_type1.setChecked(false);
                }
                break;
        }
    }
}
