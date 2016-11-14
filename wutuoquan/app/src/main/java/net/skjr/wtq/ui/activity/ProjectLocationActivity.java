package net.skjr.wtq.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CityListAccount;
import net.skjr.wtq.ui.adapter.SortAdapter;
import net.skjr.wtq.ui.widgets.CharacterParser;
import net.skjr.wtq.ui.widgets.CitySortModel;
import net.skjr.wtq.ui.widgets.PinyinComparator;
import net.skjr.wtq.ui.widgets.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/21 11:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectLocationActivity extends BaseToolbarActivity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private CharacterParser characterParser;
    private List<CitySortModel> SourceDateList;
    private ProjectListBiz mBiz;
    private ArrayList<String> mProvinceList = new ArrayList<>();
    private View mLocation_progress;
    private View mLocation_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_location);
        initVariables();
        getProvinceList();
        initToolbar("项目所在地");
        initViews();
    }

    private void initVariables() {
        mBiz = new ProjectListBiz();
    }

    /**
     * 获取省列表
     */
    private void getProvinceList() {
        Subscription s = mBiz.getProvinceList(1)
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
            List<CityListAccount.ListEntity> list = result.list;
            for(int i = 0; i < list.size(); i++) {
                mProvinceList.add(list.get(i).class_name);
            }
            loadCity();
            mLocation_view.setVisibility(View.VISIBLE);
            mLocation_progress.setVisibility(View.GONE);

        } else {
            showToast(apiResult.message);
        }
    }

    private void initViews() {
        characterParser = CharacterParser.getInstance();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mLocation_progress = findViewById(R.id.location_progress);
        mLocation_view = findViewById(R.id.location_view);
        mLocation_view.setVisibility(View.GONE);
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position + 1);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*Toast.makeText(ProjectLocationActivity.this,
                        ((CitySortModel) adapter.getItem(position - 1)).getName(),
                        Toast.LENGTH_SHORT).show();*/
                Intent in = new Intent();
                if(position == 0) {
                    //TODO:当前定位
                    in.putExtra("city","深圳");
                } else {
                    in.putExtra("city",((CitySortModel) adapter.getItem(position - 1)).getName());
                }
                setResult(RESULT_OK, in);
                finish();
            }
        });
    }

    private void loadCity() {
        /**
         * TODO
         * 传入城市名称
         */
        SourceDateList = filledData(mProvinceList);
        Collections.sort(SourceDateList, new PinyinComparator());
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.addHeaderView(initHeadView());
        sortListView.setAdapter(adapter);
    }


    private View initHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.item_select_city, null);
        TextView tv_catagory = (TextView) headView.findViewById(R.id.tv_catagory);
        TextView tv_city_name = (TextView) headView.findViewById(R.id.tv_city_name);
        tv_catagory.setText("当前定位");
        tv_city_name.setText("深圳");
        tv_city_name.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_city_location), null, null, null);
        tv_city_name.setCompoundDrawablePadding(24);
        return headView;
    }

    private List<CitySortModel> filledData(ArrayList<String> date) {
        List<CitySortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i < date.size(); i++) {
            CitySortModel sortModel = new CitySortModel();
            sortModel.setName(date.get(i));
            String pinyin = characterParser.getSelling(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {

                //对重庆多音字做特殊处理
                if (pinyin.startsWith("zhongqing")) {
                    sortString = "C";
                    sortModel.setSortLetters("C");
                } else {
                    sortModel.setSortLetters(sortString.toUpperCase());
                }

                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }

            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
        sideBar.setIndexText(indexString);
        return mSortList;

    }
}
