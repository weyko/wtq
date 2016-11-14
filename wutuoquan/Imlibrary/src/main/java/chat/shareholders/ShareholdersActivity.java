package chat.shareholders;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.List;

import chat.base.BaseActivity;
import chat.common.util.ToolsUtils;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;

/**
 * Description: 股东会列表
 * Created  by: weyko on 2016/5/26.
 */
public class ShareholdersActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private ImageView back;
    private TextView titleText;
    private PullToRefreshLayout ph_layout;
    private PullableListView pl_layout;
    private List<ShareholdersBean> list;
    private CommonAdapter<ShareholdersBean> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_refresh);
    }

    @Override
    protected void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        titleText = (TextView) this.findViewById(R.id.titleText);
        ph_layout = (PullToRefreshLayout) this.findViewById(R.id.ph_layout);
        pl_layout = (PullableListView) this.findViewById(R.id.pl_layout);
        pl_layout.setPullToRefreshMode(Pullable.TOP);
        pl_layout.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_line)));
        pl_layout.setDividerHeight(ToolsUtils.dip2px(this,0.5f));
    }

    @Override
    protected void initEvents() {
        back.setOnClickListener(this);
        ph_layout.setOnRefreshListener(this);
        pl_layout.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        titleText.setText(R.string.chat_tab_shareholders);
        list = new ArrayList<ShareholdersBean>();
        initShareholdersData();
        adapter = new CommonAdapter<ShareholdersBean>(this, list, R.layout.item_shareholder) {
            @Override
            public void convert(ViewHolder helper, ShareholdersBean item) {
                helper.setText(R.id.name_item_shareholder, item.getName());
                helper.setAvatarImageByUrl(R.id.avatar_item_shareholder, item.getAvatar());
            }
        };
        pl_layout.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        ph_layout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }

    private void initShareholdersData() {
        for (int i = 0; i < 5; i++) {
            ShareholdersBean bean = new ShareholdersBean();
            bean.setName("股东会" + i);
            list.add(bean);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
