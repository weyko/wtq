package chat.session.group.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.imlibrary.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.util.TextUtils;
import chat.common.util.ToolsUtils;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ContactBean;
import chat.dialog.CustomBaseDialog;
import chat.image.DisplayImageConfig;
import chat.session.activity.ChatSearchActvity;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.group.bean.SparseArrayList;
import chat.session.util.IMTypeUtil;
import chat.session.util.PageToastUtil;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @author weyko
 * @ClassName: EditMembersActivity
 * @Description: 批量修改群成员列表
 */
public class EditMembersActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, OnClickListener {
    public static final int REQUEST_MEMBERS = 10;
    /**
     * 请求Key
     */
    public static final String REQUEST_KEY = "request_key";
    /**
     * 请求群最大人数Key
     */
    public static final String REQUEST_MAX_MEMEBERS = "request_max_memebers";
    /**
     * 可设置管理员的数目Key
     */
    public static final String SET_ADMIN_LIMIT = "set_admin_limit";

    public int requestMode = ChatGroupInfoAcitivty.REQUEST_ADD;
    /* 标题 */
    private TextView title;
    /**
     * 取消
     */
    private TextView next;
    private View query_members, searchArea_members;
    private List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> mDatas;
    private List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> mSelectMembers;
    private CommonAdapter<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> mAdapter;
    public static boolean reflush = false;
    private String loginID;
    /* 用于传递列表数据集 */
    private RemoteMemberBean remoteMembeBean;
    /**
     * 底部菜单栏
     */
    private View menu_select_members;
    /**
     * 已选择人数
     */
    private TextView count_select_members;
    /**
     * 确认选择
     */
    private TextView ok_select_members;
    /**
     * 提示页
     */
    private View view_toast_members;
    /**
     * 显示选择联系人的容器
     */
    private LinearLayout contain_select_members;
    private PageToastUtil toastUtil;
    private boolean isSetOwner = false;
    /**
     * 群最大人数
     */
    private int maxMembers;
    private final int MAX_NUMS_ADMIN = 5;
    private int curAdminNums = 0;
    private PullToRefreshLayout ph_edit_memebers;
    private PullableListView refresh_list_members;
    private boolean isLoading = false;
    private boolean isLoadMore = false;
    private int pageIndex = 1;
    private int pageSize = 20;
    private String roomId;
    private WBaseModel<ChatGroupInfoResultBean> groupInfoModel = null;
    /**
     * 选择成员的索引
     */
    private HashMap<String, String> selects;
    private HashMap<Integer, Integer> selectIndexs;
    private HashMap<Long, Long> admins;
    private int roleType = IMTypeUtil.RoleType.MEMBERS;
    private boolean isSetManager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_members);
        getIntentParam();
        initEvents();
        initData();
        getMembersInfo();
    }

    private void getIntentParam() {
        roomId = getIntent().getStringExtra("roomId");
        roleType = getIntent().getIntExtra("roleType", roleType);
        requestMode = getIntent().getIntExtra(REQUEST_KEY, requestMode);
        maxMembers = getIntent().getIntExtra(REQUEST_MAX_MEMEBERS, 0);
        if (requestMode == ChatGroupInfoAcitivty.REQUEST_SET_ADMIN) {
            curAdminNums = getIntent().getIntExtra(SET_ADMIN_LIMIT, 0);
        }
    }

    @Override
    protected void initView() {
        title = (TextView) this.findViewById(R.id.titleText);
        next = (TextView) this.findViewById(R.id.next);
        this.findViewById(R.id.query_members).setOnClickListener(this);
        this.findViewById(R.id.searchArea_members).setOnClickListener(this);
        searchArea_members = this.findViewById(R.id.searchArea_members);
//		mListView.setOnRefreshListener(this);
//		mListView.setMode(Mode.DISABLED);
        menu_select_members = this.findViewById(R.id.menu_select_edit_members);
        menu_select_members.setVisibility(View.VISIBLE);
        count_select_members = (TextView) this.findViewById(R.id.count_select_members);
        ok_select_members = (TextView) this.findViewById(R.id.ok_select_members);
        view_toast_members = this.findViewById(R.id.view_toast_members);
        contain_select_members = (LinearLayout) this
                .findViewById(R.id.contain_select_members);
        ph_edit_memebers = (PullToRefreshLayout) this.findViewById(R.id.ph_edit_memebers);
        refresh_list_members = (PullableListView) this.findViewById(R.id.refresh_list_members);
        refresh_list_members.setPullToRefreshMode(Pullable.BOTH);
    }

    @Override
    protected void initEvents() {
        searchArea_members.setOnClickListener(this);
        ok_select_members.setOnClickListener(this);
        next.setOnClickListener(this);
        ph_edit_memebers.setOnRefreshListener(this);
    }
    @Override
    protected void initData() {
        loginID = IMClient.getInstance().getSSOUserId();
        toastUtil = new PageToastUtil(this);
        mDatas = new ArrayList<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData>();
        mSelectMembers = new ArrayList<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData>();
        admins = new HashMap<Long, Long>();
        int titleId = 0;
        switch (requestMode) {
            case ChatGroupInfoAcitivty.REQUEST_ADD:
                titleId = R.string.chat_group_invite_new_title;
                break;
            case ChatGroupInfoAcitivty.REQUEST_DELETE:
                titleId = R.string.chat_group_member_delete;
                break;
            case ChatGroupInfoAcitivty.REQUEST_SET_ADMIN:
                titleId = R.string.chat_group_admin_title;
                isSetManager = true;
                break;
            case ChatGroupInfoAcitivty.REQUEST_SET_OWNER:
                titleId = R.string.chat_group_invite_members;
                isSetOwner = true;
                break;
            default:
                titleId = R.string.chat_group_invite_new_title;
                break;
        }
        if (isSetOwner)
            menu_select_members.setVisibility(View.GONE);
        else {
            count_select_members.setText(TextUtils.getStringFormat(
                    EditMembersActivity.this, R.string.chat_group_search_selected,
                    0));
        }
        title.setText(getString(titleId));
        this.findViewById(R.id.back).setVisibility(View.GONE);
        next.setTextColor(getResources().getColor(R.color.text_color_title));
        next.setText(getString(R.string.cancel));
        selects = new HashMap<String, String>();
        selectIndexs = new HashMap<Integer, Integer>();
    }

    private void setData() {
        if (mAdapter == null) {
            mAdapter = new CommonAdapter<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData>(this, mDatas, R.layout.item_edit_members) {
                @Override
                public void convert(ViewHolder helper, final ChatGroupInfoResultBean.ChatGroupInfoData.MembersData item) {
                    helper.setText(R.id.name_item_edit_members, item.getNickName());
                    helper.setAvatarImageByUrl(R.id.avatar_item_edit_memebers, item.getPhotoUrl());
                    int role = item.getRole();
                    if (role == IMTypeUtil.RoleType.ADMINS) {
                        admins.put(item.getUserId(), item.getUserId());
                    }
                    final boolean isSelected = selects.containsKey(String.valueOf(item.getUserId()));
                    final boolean isEnable = isSetManager ? role < IMTypeUtil.RoleType.ADMINS : role < roleType;
                    ImageView select_item_edit_members = helper.getView(R.id.select_item_edit_members);
                    select_item_edit_members.setImageResource(isEnable ? (isSelected ? R.drawable.ic_checkbox_checked : R.drawable.ic_checkbox_nor) : R.drawable.ic_checkbox_white_circle);
                    helper.getConvertView().setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isEnable)
                                return;
                            if (isSelected) {
                                selects.remove(String.valueOf(item.getUserId()));
                                mSelectMembers.remove(item);
                                if (isSetManager)
                                    admins.remove(item.getUserId());
                            } else {
                                if (isSetManager) {
                                    if (admins.size() == 5) {
                                        ShowUtil.showResutToast(IMClient.getInstance().getContext(), "mx11080017");
                                        return;
                                    } else {
                                        admins.put(item.getUserId(), item.getUserId());
                                    }
                                }
                                if (isSetOwner) {
                                    selects.clear();
                                    mSelectMembers.clear();
                                    setOwner(item.getNickName());
                                }
                                selects.put(String.valueOf(item.getUserId()), "");
                                mSelectMembers.add(item);
                            }
                            updateSeleteView();
                            notifyDataSetChanged();
                        }
                    });
                }
            };
            refresh_list_members.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.query_members || i == R.id.searchArea_members) {
            if (remoteMembeBean == null)
                return;
            Intent intent = new Intent(getApplicationContext(),
                    ChatSearchActvity.class);
            intent.putExtra(ChatSearchActvity.SEARCH_MODE,
                    ChatSearchActvity.SEARCH_CONTACT);
            intent.putExtra(ChatSearchActvity.SEARCH_ISEDIT, true);
            intent.putExtra(ChatSearchActvity.SEARCH_REMOTE, remoteMembeBean);
            startActivityForResult(intent, REQUEST_MEMBERS);

        } else if (i == R.id.ok_select_members) {
            int size = 0;
            if (requestMode == ChatGroupInfoAcitivty.REQUEST_ADD
                    && maxMembers > 0 && size >= maxMembers) {
                showLimitDialog();
                return;
            }
            doBack(false);

        } else if (i == R.id.next) {
            doBack(true);

        } else {
        }
    }

    /**
     * @return void
     * @Title: showLimitDialog
     * @param:
     * @Description: 弹出超限对话框
     */
    private void showLimitDialog() {
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(this, null,
                getString(R.string.chat_group_invite_out_hint),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, this.getString(R.string.chat_group_invite_sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doBack(false);
                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();
    }

    Comparator<ContactBean> comparator = new Comparator<ContactBean>() {
        @Override
        public int compare(ContactBean lhs, ContactBean rhs) {
            String a = lhs.getCatalog();
            String b = rhs.getCatalog();
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    /**
     * @return void
     * @Title: doBack
     * @param:
     * @Description: 执行回滚
     */
    public void doBack(boolean isCancal) {
        RemoteMemberBean memberBean = getRemoteMemberBean(mSelectMembers);
        Intent intent = new Intent();
        intent.putExtra("isCancal", isCancal);
        intent.putExtra(RemoteMemberBean.MEMBES, memberBean);
        setResult(RESULT_OK, intent);
        this.finish();

    }

    /**
     * 获取编辑的成员集合
     *
     * @param members
     * @return
     */
    private RemoteMemberBean getRemoteMemberBean(List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> members) {
        RemoteMemberBean memberBean = new RemoteMemberBean();
        if (members == null)
            return memberBean;
        SparseArrayList<ContactBean> editMembers = new SparseArrayList<ContactBean>();
        int size = members.size();
        int index = 0;
        for (int i = 0; i < size; i++) {
            ChatGroupInfoResultBean.ChatGroupInfoData.MembersData member = members.get(i);
            if (member.getRole() >= roleType)
                continue;
            ContactBean bean = new ContactBean();
            bean.setFriendID((int)member.getUserId());
            bean.setRole(member.getRole());
            bean.setUserImg(member.getPhotoUrl());
            bean.setUserNickname(member.getNickName());
            bean.setCatalog(member.getPingyin());
            editMembers.put(i,bean);
            selectIndexs.put(index, i);
            index++;
        }
        memberBean.setMembsers(editMembers);
        return memberBean;
    }

    @Override
    public void onBackPressed() {
        doBack(true);
        super.onBackPressed();
    }

    /**
     * @return void
     * @Title: setOwner
     * @param:
     * @Description: 设置群主
     */
    private void setOwner(String owner) {
        if (owner == null)
            return;
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(this, TextUtils
                        .getStringFormat(this, R.string.chat_group_owner_set_hint,
                                owner), getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, getString(R.string.chat_group_owner_set_sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doBack(false);
                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();
    }

    /**
     * @return void
     * @Title: updateSeleteView
     * @param:
     * @Description: 更新底部选择菜单
     */
    private void updateSeleteView() {
        boolean enabled = mSelectMembers.size() > 0;
        count_select_members.setText(TextUtils.getStringFormat(
                EditMembersActivity.this, R.string.chat_group_search_selected,
                mSelectMembers.size()));
        ok_select_members.setBackgroundColor(getResources().getColor(
                enabled ? R.color.main_color : R.color.text_color_no_click));
        ok_select_members.setEnabled(enabled);
        if (enabled)
            addItem(mSelectMembers);
        else {
            contain_select_members.removeAllViews();
        }
    }

    ;

    private void addItem(List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> delteMembesBean) {
        if (delteMembesBean == null)
            return;
        contain_select_members.removeAllViews();
        int size = delteMembesBean.size();
        int margin = ToolsUtils.dip2px(this, 8);
        int with = ToolsUtils.dip2px(this, 50);
        for (int i = 0; i < size; i++) {
            ChatGroupInfoResultBean.ChatGroupInfoData.MembersData fansBean = delteMembesBean.get(i);
            ImageView item = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    with, with);
            item.setLayoutParams(params);
            params.leftMargin = margin;
            item.setImageResource(R.drawable.default_head);
            IMClient.sImageLoader.displayThumbnailImage(fansBean.getPhotoUrl(),
                    item, DisplayImageConfig.userLoginItemImageOptions, DisplayImageConfig.headThumbnailSize, DisplayImageConfig.headThumbnailSize);
            contain_select_members.addView(item);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (isLoading) {
            ph_edit_memebers.refreshFinish(PullToRefreshLayout.SUCCEED);
            return;
        }
        isLoading = true;
        isLoadMore = false;
        pageIndex = 1;
        getMembersInfo();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (isLoading) {
            ph_edit_memebers.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            return;
        }
        isLoadMore = true;
        isLoading = true;
        pageIndex++;
        getMembersInfo();
    }

    /**
     * @return void
     * @Title: getMembers
     * @param:
     * @Description: 获取群信息
     */
    public void getMembersInfo() {
        isLoading = true;
        if (groupInfoModel == null) {
            groupInfoModel = new WBaseModel<ChatGroupInfoResultBean>(this,
                    ChatGroupInfoResultBean.class);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("pageIndex", pageIndex);
        parmas.put("pageSize", pageSize);
        groupInfoModel.httpJsonRequest(Request.Method.GET,
                String.format(URLConfig.CHAT_GRUOP_INFO, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        dismissLoadingDialog();
                        if (data != null) {
                            if (data instanceof ChatGroupInfoResultBean) {
                                final ChatGroupInfoResultBean bean = (ChatGroupInfoResultBean) data;
                                if (bean.isResult()) {
                                    if (bean.getData() == null
                                            || bean.getData().size() == 0) {
                                        ShowUtil.showToast(
                                                EditMembersActivity.this,
                                                getString(R.string.warning_service_error));
                                        if (!isLoadMore)
                                            ph_edit_memebers
                                                    .refreshFinish(PullToRefreshLayout.SUCCEED);
                                        else
                                            ph_edit_memebers.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                        return;
                                    }
                                    ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData = bean
                                            .getData().get(0);
                                    if (chatGroupInfoData != null) {
                                        List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> participantList = chatGroupInfoData
                                                .getMemberList();
                                        if (participantList.size() == 0) {
                                            ShowUtil.showToast(IMClient
                                                            .getInstance().getContext(),
                                                    R.string.no_more_data);
                                        } else {
                                            if (!isLoadMore)
                                                mDatas.clear();
                                            mDatas.addAll(participantList);
                                            remoteMembeBean = getRemoteMemberBean(mDatas);
                                            setData();
                                        }
                                    }
                                } else {
                                    if (isLoadMore)
                                        pageIndex--;
                                }
                            } else if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                showResutToast(baseBean.getCode());
                                if (isLoadMore)
                                    pageIndex--;
                            } else {
                                ShowUtil.showHttpRequestErrorResutToast(
                                        IMClient.getInstance().getContext(),
                                        httpStatusCode, data);
                            }
                        } else {
                            ShowUtil.showHttpRequestErrorResutToast(
                                    IMClient.getInstance().getContext(),
                                    httpStatusCode, data);
                            if (isLoadMore)
                                pageIndex--;
                        }
                        isLoading = false;
                        if (!isLoadMore)
                            ph_edit_memebers
                                    .refreshFinish(PullToRefreshLayout.SUCCEED);
                        else
                            ph_edit_memebers.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                });
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == RESULT_OK) {
            if (arg0 == REQUEST_MEMBERS) {
                ImUserBean bean = (ImUserBean) arg2
                        .getSerializableExtra(ChatSearchActvity.SEARCH_ITEM_CONTACT);
                if (bean != null) {
                    try {
                        if (isSetManager) {
                            if (admins.size() == 5) {
                                ShowUtil.showResutToast(IMClient.getInstance().getContext(), "mx1108017");
                                return;
                            } else {
                                admins.put(Long.valueOf(bean.getMxId()), Long.valueOf(bean.getMxId()));
                            }
                        }
                        if(selectIndexs != null){
                            int index = selectIndexs.get(Integer.valueOf(bean.getGender()));
                            refresh_list_members.setSelected(true);
                            if (Build.VERSION.SDK_INT >= 8) {//定位到指定位置
                                refresh_list_members.smoothScrollToPosition(index);
                            } else {
                                refresh_list_members.setSelection(index);
                            }
                            if (isSetOwner) {
                                selects.clear();
                                mSelectMembers.clear();
                                setOwner(bean.getName());
                            }
                            selects.put(bean.getMxId(), "");
                            mSelectMembers.add(mDatas.get(index));
                            updateSeleteView();
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mSelectMembers = null;
        selects = null;
    }
}