package net.skjr.wtq.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.TabHomeBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.BrandsAccount;
import net.skjr.wtq.ui.adapter.BrandsAdapter;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/28 11:36
 * 描述	      入驻品牌
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class EnterBrandsActivity extends BaseToolbarActivity {

    private ListView mBrands_list;
    private TabHomeBiz mBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_brands);
        initToolbar("入驻品牌");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        Subscription s = mBiz.getBrandList(1,10)
                .subscribe(new Action1<APIResult<BrandsAccount>>() {
                    @Override
                    public void call(APIResult<BrandsAccount> apiResult) {
                        onGetBrandsComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onGetBrandsComplete(APIResult<BrandsAccount> apiResult) {
        if(apiResult.isSuccess) {
            BrandsAccount result = apiResult.result;
            List<BrandsAccount.ListEntity> list = result.list;
            BrandsAdapter adapter = new BrandsAdapter(this, list);
            mBrands_list.setAdapter(adapter);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initVariables() {
        mBiz = new TabHomeBiz();
    }

    private void initView() {
        mBrands_list = (ListView) findViewById(R.id.brands_list);
    }

    public void applyEnter(View v) {
        startActivity(ApplyEnterActivity.class);
    }
}
