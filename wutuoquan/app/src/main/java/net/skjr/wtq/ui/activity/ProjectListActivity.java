package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.DictInfo;
import net.skjr.wtq.model.account.ProjectListAccount;
import net.skjr.wtq.model.requestobj.ProjectListObj;
import net.skjr.wtq.ui.adapter.ProjectListAdapter;
import net.skjr.wtq.ui.adapter.SelectAreaAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/23 9:08
 * 描述	     项目列表
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectListActivity extends BaseToolbarActivity implements View.OnClickListener {

    private ListView mProject_list;
//    private ScrollView mProject_scroll;
    private TextView mToolbartitle;
    private TextView mList_area;
    private TextView mList_state;
    private TextView mList_sort;
    private boolean isAreaPop;
    private ProjectListBiz mBiz;
    //行业列表
    private List<String> mDictList = new ArrayList<>();

    private ProjectListObj mObj = new ProjectListObj("all","default","all",0, 10);
    private List<DictInfo.ListEntity> mDictInfoList;
    private View mList_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        initToolbar("全国");
        initView();
        initVariables();
        loadProjectList(mObj);
        loadData();
    }



    /**
     * 获取行业列表
     */
    private void loadData() {
        Subscription s = mBiz.getDictList()
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
            mDictList.add("全部行业");
            for(DictInfo.ListEntity dict : mDictInfoList) {
                mDictList.add(dict.name);
            }

            mProject_list.setVisibility(View.VISIBLE);
            mList_progress.setVisibility(View.GONE);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initVariables() {
        mBiz = new ProjectListBiz();
    }


    private void initView() {
        mProject_list = (ListView) findViewById(R.id.project_list);
//        mProject_scroll = (ScrollView) findViewById(R.id.project_list_scroll);
        mToolbartitle = (TextView) findViewById(R.id.toolbarTitle);
        mList_area = (TextView) findViewById(R.id.list_area);
        mList_state = (TextView) findViewById(R.id.list_state);
        mList_sort = (TextView) findViewById(R.id.list_sort);
        mList_progress = findViewById(R.id.list_progress);

        mProject_list.setVisibility(View.GONE);


        mToolbartitle.setOnClickListener(this);
        mList_area.setOnClickListener(this);
        mList_state.setOnClickListener(this);
        mList_sort.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbarTitle:
                startActivityForResult(new Intent(this, ProjectLocationActivity.class),1);
                break;
            case R.id.list_area:
                //全部行业
                onSelectArea();
                break;
            case R.id.list_state:
                onSelectState();
                break;
            case R.id.list_sort:
                onSelectSort();
                break;
        }
    }

    /**
     * 选择排序方式
     */
    private void onSelectSort() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;
        //--------------------------------------------
        List<String> list = new ArrayList();
        list.add("默认排序");
        list.add("即将下线");
        list.add("最多认购");
        list.add("最多收藏");
        //--------------------------------------------

        initPopWindow(list, mList_sort, 2);
    }

    /**
     * 选择状态
     */
    private void onSelectState() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;
        //--------------------------------------------
        List<String> list = new ArrayList();
        list.add("全部状态");
        list.add("预热中");
        list.add("筹资中");
        list.add("筹资成功");
        list.add("已分红");
        list.add("已解散");
        //--------------------------------------------

        initPopWindow(list, mList_state, 1);
    }

    /**
     * 选择行业
     */
    private void onSelectArea() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;

        initPopWindow(mDictList, mList_area, 0);
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
        //listPopupWindow.setWidth(cardAddCity1.getWidth());
        //        listPopupWindow.setHeight(600);
        listPopupWindow.setModal(false);
        listPopupWindow.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    String city = data.getStringExtra("city");
                    mToolbartitle.setText(city);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 加载项目列表
     */
    private void loadProjectList(ProjectListObj obj) {
        Subscription s = mBiz.getProjectList(obj)
                .subscribe(new Action1<APIResult<ProjectListAccount>>() {
                    @Override
                    public void call(APIResult<ProjectListAccount> accountInfoAPIResult) {
                        onGetProjectListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetProjectListComplete(APIResult<ProjectListAccount> apiResult) {
        if(apiResult.isSuccess) {
            ProjectListAccount result = apiResult.result;
            final List<ProjectListAccount.ListEntity> list = result.list;

            ProjectListAdapter adapter = new ProjectListAdapter(this, list);
            mProject_list.setAdapter(adapter);
//            mProject_scroll.smoothScrollTo(0, 0);

            mProject_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(ProjectListActivity.this, ProjectDetailsActivity.class);
                    intent.putExtra("pid",list.get(i).stockNO);
                    startActivity(intent);
                }
            });
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 操作状态、排序筛选列表
     */
    private void selectProjectList(int position, int type) {
        switch (type) {
            case 0:
                if(position != 0) {
                    mObj.industryType = mDictInfoList.get(position-1).code;
                } else {
                    mObj.industryType = "all";
                }
                break;
            case 1:
                //选择状态
                chooseState(position);
                break;
            case 2:
                chooseSort(position);
                break;
        }
        loadProjectList(mObj);
    }

    //选择状态
    private void chooseState(int position) {
        switch (position) {
            case 0:
                mObj.stockStatus = "all";
                break;
            case 1:
                mObj.stockStatus = "4";
                break;
            case 2:
                mObj.stockStatus = "8";
                break;
            case 3:
                mObj.stockStatus = "10";
                break;
            case 4:
                mObj.stockStatus = "14";
                break;
            case 5:
                mObj.stockStatus = "15";
                break;
        }
    }
    //选择排序方式
    private void chooseSort(int position) {
        switch (position) {
            case 0:
                mObj.orderType = "default";
                break;
            case 1:
                mObj.orderType = "jjxx";
                break;
            case 2:
                mObj.orderType = "zdrg";
                break;
            case 3:
                mObj.orderType = "zdsc";
                break;
        }
    }
}
