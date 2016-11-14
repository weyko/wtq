package net.skjr.wtq.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.ui.adapter.IncomeListAdapter;
import net.skjr.wtq.ui.adapter.InvestListAdapter;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/23 9:31
 * 描述	      财富榜
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class WealthListActivity extends BaseToolbarActivity {

    private ListView mInvest_order;
    private ListView mIncome_order;
    private RadioGroup mList_rg;
    private RadioButton mList_rb1;
    private RadioButton mList_rb2;
    private ScrollView mList_scroll;
    private TextView mAssets_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wealthlist);
        initToolbar("乌托圈财富榜");
        initView();
    }

    private void initView() {
        mInvest_order = (ListView) findViewById(R.id.invest_order);
        mIncome_order = (ListView) findViewById(R.id.income_order);
        mList_rg = (RadioGroup) findViewById(R.id.wealth_list_rg);
        mList_rb1 = (RadioButton) findViewById(R.id.wealth_list_rb1);
        mList_rb2 = (RadioButton) findViewById(R.id.wealth_list_rb2);
        mList_scroll = (ScrollView) findViewById(R.id.list_scrollview);
        mAssets_count = (TextView) findViewById(R.id.wealth_assets_count);

        mList_rb1.setChecked(true);
        mList_scroll.smoothScrollTo(0, 0);
        InvestListAdapter adapter = new InvestListAdapter(this);
        mInvest_order.setAdapter(adapter);
        IncomeListAdapter adapter1 = new IncomeListAdapter(this);
        mIncome_order.setAdapter(adapter1);

        initRadioButton();
    }

    //处理RadioButton
    private void initRadioButton() {
        mList_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.wealth_list_rb1:
                        mInvest_order.setVisibility(View.VISIBLE);
                        mIncome_order.setVisibility(View.GONE);
                        mAssets_count.setText("资产总额：￥6,222,152.00");
                        mList_scroll.smoothScrollTo(0, 0);
                        break;
                    case R.id.wealth_list_rb2:
                        mIncome_order.setVisibility(View.VISIBLE);
                        mInvest_order.setVisibility(View.GONE);
                        mAssets_count.setText("投资回报率：45.32%");
                        mList_scroll.smoothScrollTo(0, 0);
                        break;
                }
            }
        });
    }
}
