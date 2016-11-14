package chat.session.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.util.output.ShowUtil;
import chat.manager.ChatLoadManager;
import chat.manager.ChatMembersManager;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.session.util.PageToastUtil;
import chat.view.ActionSheet;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableGridView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @ClassName: MoreGroupMembersActivity
 * @Description: 更多群成员
 */
public class MoreGroupMembersActivity extends BaseActivity implements
        PullToRefreshLayout.OnRefreshListener, ActionSheet.MenuItemClickListener {
    private TextView titleText;
    /* 成员集 */
    private PullableGridView gv_members_group_invite;
    private PullToRefreshLayout ph_chat_groupinfo;
    private List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> participantVoList;
    private CommonAdapter<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> adapter;
    private WBaseModel<ChatGroupInfoResultBean> groupInfoModel = null;
    private String roomId;
    /**
     * 当前用户在该的角色
     */
    private int roleType = IMTypeUtil.RoleType.MEMBERS;
    /**
     * 当前编辑的成员
     */
    private ChatGroupInfoResultBean.ChatGroupInfoData.MembersData currentMember;
    private String myUserId;
    /**
     * 开方 类型:0私有，1：公共
     */
    private String cacheGroupInfo;
    private int members;
    private int admins;
    // /是否回滚
    private boolean isOverrun;
    private int currentPosition;
    private StringBuilder editIds;
    private WBaseModel<WBaseBean> operatorModel = null;

    private String title;
    private int pageIndex = 1;
    private int pageSize = 20;
    private boolean isLoading = false;
    private boolean isLoadMore = false;

    private boolean hasChange = false;
    private PageToastUtil pageUtil;
    private View view_toast_group_more;
    /**
     * 只有查看模式
     */
    private boolean isOnlyLook = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        getIntentData();
        initEvents();
        initData();
        showLoading();
        getGroupInfo();
    }

    private void getIntentData() {
        roomId = getIntent().getStringExtra("roomId");
        roleType = getIntent().getIntExtra("roleType", roleType);
        members = getIntent().getIntExtra("members", members);
        isOnlyLook = getIntent().getBooleanExtra("isOnlyLook", false);
    }

    /**
     * @return void
     * @Title: getMembers
     * @param:
     * @Description: 获取群信息
     */
    public void getGroupInfo() {
        isLoading = true;
        if (groupInfoModel == null) {
            groupInfoModel = new WBaseModel<ChatGroupInfoResultBean>(this,
                    ChatGroupInfoResultBean.class);
        }
        if (pageIndex == 2) {
            pageIndex = 3;
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("pageIndex", pageIndex);
        parmas.put("pageSize", pageIndex == 1 ? 2 * pageSize : pageSize);
        groupInfoModel.httpJsonRequest(Method.GET,
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
                                                MoreGroupMembersActivity.this,
                                                getString(R.string.warning_service_error));
                                        if (isLoadMore) {
                                            ph_chat_groupinfo.loadmoreFinish(PullToRefreshLayout.FAIL);
                                        } else
                                            ph_chat_groupinfo
                                                    .refreshFinish(PullToRefreshLayout.FAIL);
                                        return;
                                    }
                                    ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData = bean
                                            .getData().get(0);
                                    members = chatGroupInfoData.getNowCnt();
                                    admins = ChatMembersManager.getInstance()
                                            .getAdminsOfGroup(roomId);
                                    titleText.setText(title + "(" + members
                                            + ")");
                                    //if (chatGroupInfoData != null) {
                                        List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> participantList = chatGroupInfoData
                                                .getMemberList();
                                        if (participantList.size() == 0) {
                                            ShowUtil.showToast(IMClient
                                                            .getInstance().getContext(),
                                                    R.string.no_more_data);
                                        } else
                                            setMembers(participantList);
                                    //}
                                    ChatLoadManager
                                            .getInstance()
                                            .updateChatGroupInfo(null, roomId,
                                                    bean, chatGroupInfoData,
                                                    true, cacheGroupInfo, false);
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
                                showErrorPage();
                                ShowUtil.showHttpRequestErrorResutToast(
                                        IMClient.getInstance().getContext(),
                                        httpStatusCode, data);
                            }
                        } else {
                            showErrorPage();
                            ShowUtil.showHttpRequestErrorResutToast(
                                    IMClient.getInstance().getContext(),
                                    httpStatusCode, data);
                            if (isLoadMore)
                                pageIndex--;
                        }
                        isLoading = false;
                        if (isLoadMore) {
                            ph_chat_groupinfo.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        } else
                            ph_chat_groupinfo
                                    .refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                });
    }
    @Override
    protected void initData() {
        title = getString(R.string.chat_group_info_member);
        if (participantVoList == null) {
            participantVoList = new ArrayList<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData>();
        }
        titleText.setText(title + "(" + members + ")");
        editIds = new StringBuilder();
        pageUtil = new PageToastUtil(this);
    }

    @Override
    protected void initView() {
        titleText = (TextView) this.findViewById(R.id.titleText);
        gv_members_group_invite = (PullableGridView) this
                .findViewById(R.id.gv_members_group_invite);
        ph_chat_groupinfo = (PullToRefreshLayout) this
                .findViewById(R.id.ph_chat_groupinfo);
        view_toast_group_more = this.findViewById(R.id.view_toast_group_more);
        gv_members_group_invite.setPullToRefreshMode(Pullable.BOTH);
    }

    @Override
    protected void initEvents() {
        this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                reback();
            }
        });
        ph_chat_groupinfo.setOnRefreshListener(this);
    }

    /**
     * @return void
     * @Title: setMembers
     * @param:
     * @Description: 设置群成员
     */
    private void setMembers(List<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData> list) {
        if (list == null)
            return;
        if (participantVoList == null) {
            participantVoList = new ArrayList<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData>();
        }
        if (pageIndex == 1) {
            participantVoList.clear();
        }
        participantVoList.addAll(list);
        if (adapter == null) {
            adapter = new CommonAdapter<ChatGroupInfoResultBean.ChatGroupInfoData.MembersData>(this, participantVoList,
                    R.layout.item_members) {
                @Override
                public void convert(ViewHolder helper, final ChatGroupInfoResultBean.ChatGroupInfoData.MembersData item) {
                    helper.setAvatarImageByUrl(R.id.avatar_members_item,
                            item.getPhotoUrl());
                    helper.setText(R.id.name_members_item, item.getNickName());
                    ImageView tag_members_item = helper
                            .getView(R.id.tag_members_item);
                    switch (item.getRole()) {
                        case IMTypeUtil.RoleType.OWNERS:
                            tag_members_item.setVisibility(View.VISIBLE);
                            tag_members_item
                                    .setImageResource(R.drawable.ic_head_group_ower);
                            break;
                        case IMTypeUtil.RoleType.ADMINS:
                            tag_members_item.setVisibility(View.VISIBLE);
                            tag_members_item
                                    .setImageResource(R.drawable.ic_head_group_admin);
                            break;
                        case IMTypeUtil.RoleType.MEMBERS:
                            tag_members_item.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                    helper.getConvertView().setOnClickListener(
                            new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    if (isOnlyLook) {
                                        lookInfo(String.valueOf(item.getUserId()));
                                        return;
                                    }
                                    currentPosition = adapter.getPosition(item);
                                    isOverrun = admins >= ChatGroupInfoAcitivty.GROUP_ADMIN_MAX
                                            && currentPosition >= ChatGroupInfoAcitivty.GROUP_ADMIN_MAX
                                            && item.getRole() != IMTypeUtil.RoleType.ADMINS;
                                    currentMember = item;
                                    showMenuDialog(item);
                                }
                            });
                }
            };
            gv_members_group_invite.setAdapter(adapter);
        } else {
            adapter.setList(participantVoList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示菜单栏对话框
     */
    private void showMenuDialog(ChatGroupInfoResultBean.ChatGroupInfoData.MembersData item) {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle(getString(R.string.cancel));
        menuView.setCurrentItems("");
        setMenuItems(menuView, item);
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    /**
     * @return void
     * @Title: setMenuItems
     * @param:
     * @Description: 设置弹出框显示内容
     */
    private void setMenuItems(ActionSheet menuView, ChatGroupInfoResultBean.ChatGroupInfoData.MembersData item) {
        String info = getString(R.string.chat_group_info_menu_info);
        String infoSelf = getString(R.string.chat_group_info_menu_info_self);
        String chat = getString(R.string.chat_group_info_menu_chat);
        String userId = item.getUserId() + "";
        switch (roleType) {// 根据角色显示不同内容
            case IMTypeUtil.RoleType.OWNERS:
                if (isSelf(userId)) {
                    menuView.addItems(infoSelf);
                } else {
                    //if (item != null) {
                        if (isOverrun) {
                            menuView.addItems(info, chat,
                                    getString(R.string.chat_group_info_menu_remove));
                        } else {
                            String setManager = getString(item.getRole() == IMTypeUtil.RoleType.ADMINS ? R.string.chat_group_info_menu_admin_cancal
                                    : R.string.chat_group_info_menu_admin);
                            menuView.addItems(info, chat, setManager,
                                    getString(R.string.chat_group_info_menu_remove));
                        }
                    //}
                }
                break;
            case IMTypeUtil.RoleType.ADMINS:
                if (isSelf(userId))
                    menuView.addItems(infoSelf);
                else if (item.getRole() > IMTypeUtil.RoleType.MEMBERS)// 同级别或高级别不能移除
                    menuView.addItems(info, chat);
                else
                    menuView.addItems(info, chat,
                            getString(R.string.chat_group_info_menu_remove));
                break;
            case IMTypeUtil.RoleType.MEMBERS:
                if (isSelf(userId))
                    menuView.addItems(infoSelf);
                else
                    menuView.addItems(info, chat);
                break;
            case IMTypeUtil.RoleType.OUTCASTS:
                menuView.addItems(info, chat);
                break;
            default:
                break;
        }
    }

    /**
     * @return boolean
     * @Title: isSelf
     * @param:
     * @Description: 是否为自己
     */
    public boolean isSelf(String userId) {
        return IMClient.getInstance().getSSOUserId().equals(userId);
    }

    @Override
    public void onBackPressed() {
        reback();
        super.onBackPressed();
    }

    /**
     * @return void
     * @Title: reback
     * @param:
     * @Description: 回滚
     */
    private void reback() {
        Intent intent = new Intent();
        intent.putExtra("hasChange", hasChange);
        intent.putExtra("members", members);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (isLoading) {
            return;
        }
        isLoadMore = false;
        pageIndex = 1;
        getGroupInfo();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (isLoading) {
            return;
        }
        isLoadMore = true;
        pageIndex++;
        getGroupInfo();
    }

    @Override
    public void onActionSheetItemClick(int itemPosition) {
        switch (itemPosition) {
            case 0:
                if (currentMember != null)
                    lookInfo(String.valueOf(currentMember.getUserId()));
                break;
            case 1:
                goToChat();
                break;
            case 2:
                if (isOverrun || roleType == IMTypeUtil.RoleType.ADMINS) {
                    removeMember(false);
                } else {
                    setManager(currentMember,
                            currentMember.getRole() == IMTypeUtil.RoleType.ADMINS);
                }
                break;
            case 3:
                removeMember(false);
                break;
            default:
                break;
        }
    }

    /**
     * @return void
     * @Title: lookInfo
     * @param:
     * @Description: 查看用户信息
     */
    private void lookInfo(String userId) {

    }

    /**
     * @return void
     * @Title: goToChat
     * @param:
     * @Description: 聊天
     */
    private void goToChat() {
        if (currentMember == null)
            return;
        ImUserBean userBean = new ImUserBean();
        userBean.setAvatar(currentMember.getPhotoUrl());
        userBean.setMxId(String.valueOf(currentMember.getUserId()));
        userBean.setName(currentMember.getNickName());
        ChatUtil.gotoChatRoom(this, userBean);
    }

    /**
     * @return void
     * @Title: removeMember
     * @param:
     * @Description: 移除当前群成员
     */
    private void removeMember(boolean isOutGroup) {
        deleteMember(currentMember);
    }

    /**
     * @return void
     * @Title: getMembers
     * @param:
     * @Description: 删除群成员
     */
    private void deleteMember(final ChatGroupInfoResultBean.ChatGroupInfoData.MembersData member) {
        if (member == null)
            return;
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        List<HashMap<String, Object>> participantList = new ArrayList<HashMap<String, Object>>();
        if (editIds == null) {
            editIds = new StringBuilder();
        }
        editIds.delete(0, editIds.length());
        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put("role", member.getRole());
        String userId = String.valueOf(member.getUserId());
        editIds.append(userId);
        editIds.append(",");
        item.put("userId", userId);
        item.put("mtalkDomain", member.getMtalkDomain());
        participantList.add(item);
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("memberList", participantList);
        operatorModel.httpJsonRequest(Method.POST,
                String.format(URLConfig.CHAT_GRUOP_DELETE_MEMBER, roomId),
                parmas, new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                if (baseBean.isResult()) {
                                    hasChange = true;
                                    ChatMembersManager.getInstance()
                                            .removeMember(
                                                    roomId,
                                                    String.valueOf(member
                                                            .getUserId()));
                                    participantVoList.remove(currentMember);
                                    members--;
                                    titleText.setText(title + "(" + members
                                            + ")");
                                    adapter.notifyDataSetChanged();
                                }
                                showResutToast(baseBean.getCode());
                            } else {
                                showErrorPage();
                                ShowUtil.showHttpRequestErrorResutToast(
                                        IMClient.getInstance().getContext(),
                                        httpStatusCode, data);
                            }
                        } else {
                            showErrorPage();
                            ShowUtil.showHttpRequestErrorResutToast(
                                    IMClient.getInstance().getContext(),
                                    httpStatusCode, data);
                        }
                        dissmisLoading();
                    }

                });
    }

    /**
     * @return void
     * @Title: setManager
     * @param:
     * @Description: 设置管理员
     */
    private void setManager(final ChatGroupInfoResultBean.ChatGroupInfoData.MembersData member, final boolean isRemove) {
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        List<HashMap<String, Object>> addList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> object = new HashMap<String, Object>();
        String userId = String.valueOf(member.getUserId());
        object.put("userId", userId);
        object.put("mtalkDomain", member.getMtalkDomain());
        addList.add(object);
        member.setRole(isRemove ? IMTypeUtil.RoleType.MEMBERS : IMTypeUtil.RoleType.ADMINS);
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        if (isRemove) {
            parmas.put("removeList", addList);
        } else
            parmas.put("addList", addList);
        operatorModel.httpJsonRequest(Method.PUT,
                String.format(URLConfig.CHAT_GROUP_SET_MANAGER, roomId),
                parmas, new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null && data instanceof WBaseBean) {
                            WBaseBean baseBean = (WBaseBean) data;
                            if (baseBean.isResult()) {
                                hasChange = true;
                                member.setRole(isRemove ? IMTypeUtil.RoleType.MEMBERS
                                        : IMTypeUtil.RoleType.ADMINS);
                                ChatMembersManager.getInstance()
                                        .updateGroupRole(
                                                roomId,
                                                member.getRole(),
                                                String.valueOf(member
                                                        .getUserId()));
                                admins = ChatMembersManager.getInstance()
                                        .getAdminsOfGroup(roomId);
                                int size = participantVoList.size();
                                participantVoList.remove(currentMember);
                                int insertPosition = isRemove ? admins + 1 : 1;
                                if (insertPosition < size)
                                    participantVoList.add(insertPosition,
                                            member);
                                else
                                    participantVoList.add(member);
                                adapter.notifyDataSetChanged();
                            }
                            showResutToast(baseBean.getCode());
                        } else {
                            showErrorPage();
                            ShowUtil.showHttpRequestErrorResutToast(
                                    IMClient.getInstance().getContext(),
                                    httpStatusCode, data);
                        }
                        dissmisLoading();
                    }

                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (groupInfoModel != null) {
            groupInfoModel.cancelRequest();
            groupInfoModel = null;
        }
        if (operatorModel != null) {
            operatorModel.cancelRequest();
            operatorModel = null;
        }
    }

    /**
     * @return void
     * @Title: showErrorPage
     * @param:
     * @Description: 显示空白页面
     */
    private void showErrorPage() {
        ph_chat_groupinfo.setVisibility(View.GONE);
        pageUtil.showToast(view_toast_group_more, PageToastUtil.PageMode.DEFAULT);
    }
}
