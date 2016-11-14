package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.databinding.ActivityApplyenterBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CityListAccount;
import net.skjr.wtq.model.account.DictInfo;
import net.skjr.wtq.ui.adapter.SelectAreaAdapter;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.SaveApplyViewModel;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/28 13:52
 * 描述	      申请入驻
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ApplyEnterActivity extends BaseToolbarActivity implements View.OnClickListener {

    private AccountBiz mBiz;
    private ActivityApplyenterBinding mBinding;
    private TextView mApply_insdutry;
    private TextView mApply_area;
    private ProjectListBiz mListBiz;

    private List<DictInfo.ListEntity> mDictInfoList;
    //行业列表
    private List<String> mDictList = new ArrayList<>();
    //城市列表
    private List<String> mProvinceList = new ArrayList<>();
    private List<String> mCityList = new ArrayList<>();

    private List<CityListAccount.ListEntity> mProvincelistEntry;
    private List<CityListAccount.ListEntity> mCitylistEntry;

    private String mProjectArea;
    private String mProjectAreaId;
    private boolean isAreaPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("申请入驻");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        //获取行业列表
        getDiceList();
        getProvinceList();
    }

    private void initView() {
        mApply_insdutry = (TextView) findViewById(R.id.apply_insdutry);
        mApply_area = (TextView) findViewById(R.id.apply_area);

        mApply_insdutry.setOnClickListener(this);
        mApply_area.setOnClickListener(this);
    }

    private void initVariables() {
        mBiz = new AccountBiz();
        mListBiz = new ProjectListBiz();
    }

    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_applyenter);
        SaveApplyViewModel model = new SaveApplyViewModel();
        model.setInsdutry("请选择");
        model.setArea("请选择");
        mBinding.setModel(model);
    }

    public void applyNext(View view) {
        SaveApplyViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            Intent intent = new Intent(this, ApplyEnter2Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("applyinfo",model);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            showToast(check.errorMessage);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.apply_insdutry:
                selectInsdutry();
                break;
            case R.id.apply_area:
                selectArea();
                break;
        }
    }

    /**
     * 选择所在地
     */
    private void selectArea() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;
        initPopWindow(mProvinceList, mApply_area,1);
    }

    /**
     * 选择行业
     */
    private void selectInsdutry() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;
        initPopWindow(mDictList, mApply_insdutry,0);
    }

    /**
     * 获取行业列表
     */
    private void getDiceList() {
        Subscription s = mListBiz.getDictList()
                .subscribe(new Action1<APIResult<DictInfo>>() {
                    @Override
                    public void call(APIResult<DictInfo> accountInfoAPIResult) {
                        onGetDictListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }
    private void onGetDictListComplete(APIResult<DictInfo> apiResult) {
        if(apiResult.isSuccess) {
            DictInfo result = apiResult.result;
            mDictInfoList = result.list;
            for(DictInfo.ListEntity dict : mDictInfoList) {
                mDictList.add(dict.name);
            }

        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 获取省列表
     */
    private void getProvinceList() {
        Subscription s = mListBiz.getProvinceList(1)
                .subscribe(new Action1<APIResult<CityListAccount>>() {
                    @Override
                    public void call(APIResult<CityListAccount> accountInfoAPIResult) {
                        onGetProvinceListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetProvinceListComplete(APIResult<CityListAccount> apiResult) {
        if(apiResult.isSuccess) {
            CityListAccount result = apiResult.result;
            mProvincelistEntry = result.list;
            for(CityListAccount.ListEntity li : mProvincelistEntry) {
                mProvinceList.add(li.class_name);
            }

        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 获取市列表
     */
    private void getCityList(int pro) {
        Subscription s = mListBiz.getProvinceList(pro)
                .subscribe(new Action1<APIResult<CityListAccount>>() {
                    @Override
                    public void call(APIResult<CityListAccount> accountInfoAPIResult) {
                        onGetCityListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetCityListComplete(APIResult<CityListAccount> apiResult) {
        if(apiResult.isSuccess) {
            CityListAccount result = apiResult.result;
            mCitylistEntry = result.list;
            mCityList.clear();
            for(CityListAccount.ListEntity li : mCitylistEntry) {
                mCityList.add(li.class_name);
            }
            initPopWindow(mCityList, mApply_area,3);
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 初始化PopWindow
     * @param list
     */
    private void initPopWindow(final List<String> list, final TextView textview, final int type) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);

        //设置ListView类型的适配器
        SelectAreaAdapter adapter = new SelectAreaAdapter(this, list);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                textview.setText(list.get(position));
                isAreaPop = false;
                //筛选项目列表
                selectProjectList(position, type);
            }
        });

        listPopupWindow.setAnchorView(textview);

        //设置对话框的宽高
        listPopupWindow.setWidth(200);
        listPopupWindow.setHeight(400);
        listPopupWindow.setModal(false);
        listPopupWindow.show();
    }
    /**
     * 选择行业和所在地
     * @param position
     * @param type
     */
    private void selectProjectList(int position, int type) {
        SaveApplyViewModel model = mBinding.getModel();
        switch (type) {
            case 0:
                //选择行业
                model.setInsdutry(mDictInfoList.get(position).code);
                break;
            case 1:
                //选择所在地
                //                model.setRegionCity(mCitylist.get(position).class_id);
                int province = mProvincelistEntry.get(position).class_id;
                mProjectArea = mProvincelistEntry.get(position).class_name+" ";
                mProjectAreaId = province+",";
                getCityList(province);
                break;
            default:
                mProjectArea += mCitylistEntry.get(position).class_name;
                mProjectAreaId += mCitylistEntry.get(position).class_id;
                mApply_area.setText(mProjectArea);
                model.setAreaId(mProjectAreaId);
                model.setArea(mProjectArea);
                break;
        }
    }
}
