package chat.session.group.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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
import chat.common.util.TextUtils;
import chat.common.util.ToolsUtils;
import chat.common.util.output.ShowUtil;
import chat.common.util.time.TimeUtil;
import chat.contact.activity.ChoseContactsActivity;
import chat.contact.bean.ContactBean;
import chat.dialog.CustomBaseDialog;
import chat.image.DisplayImageConfig;
import chat.image.activity.ChatPhotoWall;
import chat.manager.ChatContactManager;
import chat.manager.ChatGroupManager;
import chat.manager.ChatLoadManager;
import chat.manager.ChatMembersManager;
import chat.manager.ChatMessageManager;
import chat.manager.ChatSessionManager;
import chat.service.MessageInfoReceiver;
import chat.session.activity.ChatActivity;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.ChatGroupInfoResultBean;
import chat.session.group.bean.ChatGroupMemberBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.group.bean.SparseArrayList;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.view.ActionSheet;
import chat.view.ScrollGridView;
import chat.view.SettingItemView;
import chat.view.TopView;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @author weyko
 * @ClassName: ChatGroupInfoAcitivty
 * @Description: 群组信息页面
 */
public class ChatGroupInfoAcitivty extends BaseActivity implements OnClickListener, ActionSheet.MenuItemClickListener,
        SettingItemView.OnSwitchListener {
    /**
     * 群组信息传递的key
     */
    public final static String ROOM = "room";
    public final static String SESSION_ID = "sessionId";
    public static final int GROUP_ADMIN_MAX = 5;
    /**
     * 删除
     */
    public static final int REQUEST_DELETE = 12;
    /* 添加 */
    public static final int REQUEST_ADD = 13;
    /* 设置管理员 */
    public static final int REQUEST_SET_ADMIN = 14;
    /* 设置超级管理员 */
    public static final int REQUEST_SET_OWNER = 15;
    private static final int REQUEST_EDIT = 10;
    private static final int REQUEST_AVATAR = 11;
    /**
     * 查看二维码
     */
    private static final int REQUEST_QRCODE = 16;
    /**
     * 查看更多群成员
     */
    private static final int REQUEST_MORE = 17;
    private final int GROUP_MEMBERS_MAX = 500;//群成员最大人数
    private final int MIN_BG_HEIGHT = ToolsUtils.dip2px(
            IMClient.getInstance().getContext(), 200);// 背景最小高度
    /**
     * 群最大显示人数
     */
    private int MAX_SHOW_MEMBERS = 40;
    /**
     * 群昵称
     */
    private SettingItemView nickname_group_info;
    /**
     * 二维码
     */
    private View code_group_info;
    /**
     * 我的群昵称
     */
    private SettingItemView nickname_my_group_info;
    /**
     * 全部群成员
     */
    private SettingItemView members_all_group_info;
    /**
     * 消息免打扰
     */
    private SettingItemView msg_on_off_group_info;
    /**
     * 消息置顶
     */
    private SettingItemView top_group_info;
    /**
     * 保存到通讯录
     */
    private SettingItemView book_group_info;
    /**
     * 聊天图片
     */
    private SettingItemView img_group_info;
    /**
     * 清除聊天记录
     */
    private SettingItemView clear_msg_group_info;
    /**
     * 举报群
     */
    private SettingItemView report_group_info;
    /**
     * 群组id
     */
    private String roomId;
    /**
     * 群成员列表容器
     */
    private ScrollGridView members_group_info;
    /**
     * 删除并退出群按钮
     */
    private TextView delete_group_info;
    /**
     * 解散群按钮
     */
    private TextView dissolve_group_info;
    /**
     * 本群创建时间
     */
    private TextView create_group_info;
    /**
     * 群成员列表
     */
    private List<ChatGroupMemberBean> participantVoList;
    private CommonAdapter<ChatGroupMemberBean> adapter;
    /**
     * 当前用户在该的角色
     */
    private int roleType = IMTypeUtil.RoleType.MEMBERS;
    /**
     * 当前编辑的成员
     */
    private ChatGroupMemberBean currentMember;
    private String myUserId;
    /**
     * 开方 类型:0私有，1：公共
     */
    private int publicType = 0;
    private ChatGroupInfoResultBean.ChatGroupInfoData chatGroupInfoData;
    private ArrayList<String> membersId;
    /**
     * 是否有变化
     */
    private boolean hasChanged = false;
    /**
     * 管理员人数
     */
    private int numsOfAdmin = 0;
    private TopView sticky_group_info;
    private int topNum;
    private String sessionId = null;
    private String cacheGroupInfo;
    private WBaseModel<ChatGroupInfoResultBean> groupInfoModel = null;
    private WBaseModel<WBaseBean> operatorModel = null;
    private Bitmap backgroudBmp = null;
    private TextView titleText;
    private ImageView back;
    // 当前索引位置
    private int currentPosition;
    /**
     * @Title: getMembers
     * @param:
     * @Description 邀请群成员
     * @return void
     */
    private boolean isSended = false;
    /**
     * @Title: setMembers
     * @param:
     * @Description: 设置群成员
     * @return void
     */
    private boolean isOverrun = false;// 是否超限
    private int admins;// 当前群管理员个数
    private int members;// 全部群成员人数
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            setMembers();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        getIntentParmas();
        initData();
        initEvents();
        getGroupInfo(true);
        initTopNums();
    }

    private void initTopNums() {
        try {
            HashMap<String, Object> data = ChatLoadManager.getInstance()
                    .getSessions(false, true);
            if (data != null) {
                topNum = (Integer) data.get("allTopCount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        myUserId = IMClient.getInstance().getSSOUserId();
        participantVoList = new ArrayList<ChatGroupMemberBean>();
        membersId = new ArrayList<String>();
        initBaseInfo(false, 0);
    }

    /**
     * @return void
     * @Title: initBaseInfo
     * @param:
     * @Description: 初始化群基本信息
     */
    private void initBaseInfo(boolean updateGroupName, int groupNumerbs) {
        if (chatGroupInfoData == null || participantVoList == null)
            return;
        String roomName = chatGroupInfoData.getGroupName();
        if (android.text.TextUtils.isEmpty(roomName)) {
            roomName = getString(R.string.chat_group_create_name_empty);
        }
        if (updateGroupName)
            updateGroupName(roomName);
    }

    /**
     * @return void
     * @Title: updateGroupName
     * @param:
     * @Description: 更新群名称
     */
    private void updateGroupName(String name) {
        String showName = name;
        if (chatGroupInfoData != null
                && chatGroupInfoData.getRoomNameUpdated() == 0)
            showName = getString(R.string.chat_group_create_name_empty);
        nickname_group_info.setRight(showName);
        ChatGroupManager.getInstance().updateGroupName(roomId, name);
    }

    /**
     * @return void
     * @Title: updateGroupName
     * @param:
     * @Description: 更新我的群名称
     */
    private void updateMyNickName(String name) {
        nickname_my_group_info.setRight(name);
        ChatMembersManager.getInstance().updateMyNickName(roomId,
                IMClient.getInstance().getSSOUserId(), name);
    }

    /**
     * @return void
     * @Title: showPageStyleByRole
     * @param:
     * @Description: 根据类型显示样式
     */
    private void showPageStyleByRole() {
        nickname_group_info.setVisibility(View.VISIBLE);
        switch (roleType) {
            case IMTypeUtil.RoleType.OWNERS:
                delete_group_info.setVisibility(View.VISIBLE);
                dissolve_group_info.setVisibility(View.VISIBLE);
                break;
            case IMTypeUtil.RoleType.ADMINS:
                delete_group_info.setVisibility(View.VISIBLE);
                break;
            case IMTypeUtil.RoleType.MEMBERS:
                nickname_group_info.setVisibility(View.GONE);
                delete_group_info.setVisibility(View.VISIBLE);
                break;
            case IMTypeUtil.RoleType.OUTCASTS:// 被踢出群了
                break;
            default:
                break;
        }
    }

    private void getIntentParmas() {
        roomId = getIntent().getStringExtra(ROOM);
        sessionId = getIntent().getStringExtra(SESSION_ID);
    }

    @Override
    protected void initView() {
        msg_on_off_group_info = (SettingItemView) this
                .findViewById(R.id.msg_on_off_group_info);
        top_group_info = (SettingItemView) this
                .findViewById(R.id.top_group_info);
        book_group_info = (SettingItemView) this
                .findViewById(R.id.book_group_info);
        img_group_info = (SettingItemView) this
                .findViewById(R.id.img_group_info);
        clear_msg_group_info = (SettingItemView) this
                .findViewById(R.id.clear_msg_group_info);
        report_group_info = (SettingItemView) this
                .findViewById(R.id.report_group_info);
        nickname_group_info = (SettingItemView) this
                .findViewById(R.id.nickname_group_info);
        nickname_my_group_info = (SettingItemView) this
                .findViewById(R.id.nickname_my_group_info);
        members_all_group_info = (SettingItemView) this
                .findViewById(R.id.members_all_group_info);
        members_group_info = (ScrollGridView) this
                .findViewById(R.id.members_group_info);
        code_group_info = this.findViewById(R.id.code_group_info);
        delete_group_info = (TextView) this
                .findViewById(R.id.delete_group_info);
        dissolve_group_info = (TextView) this
                .findViewById(R.id.dissolve_group_info);
        create_group_info = (TextView) this
                .findViewById(R.id.create_group_info);
        sticky_group_info = (TopView) this.findViewById(R.id.sticky_group_info);
        titleText = (TextView) this.findViewById(R.id.titleText);
        back = (ImageView) this.findViewById(R.id.back);
    }

    @Override
    protected void initEvents() {
        nickname_group_info.setOnClickListener(this);
        nickname_my_group_info.setOnClickListener(this);
        members_all_group_info.setOnClickListener(this);
        code_group_info.setOnClickListener(this);
        delete_group_info.setOnClickListener(this);
        dissolve_group_info.setOnClickListener(this);
        img_group_info.setOnClickListener(this);
        clear_msg_group_info.setOnClickListener(this);
        report_group_info.setOnClickListener(this);
        msg_on_off_group_info.setOnSwitchListener(this);
        top_group_info.setOnSwitchListener(this);
        book_group_info.setOnSwitchListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        int i = arg0.getId();
        if (i == R.id.back) {
            doBack(false);

        } else if (i == R.id.nickname_group_info) {
            if (chatGroupInfoData == null)
                return;
            Intent intent = new Intent(this, EditGroupInfoActivity.class);
            intent.putExtra(EditGroupInfoActivity.EDIT_MODE,
                    EditGroupInfoActivity.EDIT_GROUP_NAME);
            intent.putExtra(EditGroupInfoActivity.EDIT_ID, roomId);
            String name = nickname_group_info.getRightText();
            if (getString(R.string.chat_group_create_name_empty).equals(name)) {
                name = "";
            }
            intent.putExtra("name", name);
            startActivityForResult(intent, REQUEST_EDIT);

        } else if (i == R.id.nickname_my_group_info) {
            if (chatGroupInfoData == null)
                return;
            Intent intent_my = new Intent(this, EditGroupInfoActivity.class);
            intent_my.putExtra(EditGroupInfoActivity.EDIT_MODE,
                    EditGroupInfoActivity.EDIT_GROUP_MY_NAME);
            intent_my.putExtra(EditGroupInfoActivity.EDIT_ID, roomId);
            intent_my.putExtra("name", nickname_my_group_info.getRightText());
            startActivityForResult(intent_my, REQUEST_EDIT);

        } else if (i == R.id.code_group_info) {
            if (chatGroupInfoData == null) {
                return;
            }
            //if (chatGroupInfoData != null) {
            Intent intent_code = new Intent(this, QuickmarkActivity.class);
            ChatGroupData groupData = new ChatGroupData();
            try {
                groupData.setId(Long.parseLong(roomId));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            groupData.setRoleType(chatGroupInfoData.getRoleType());
            groupData.setGroupName(chatGroupInfoData.getGroupName());
            groupData.setPhotoUrl(chatGroupInfoData.getPhotoUrl());
            groupData.setQrcodeUrl(chatGroupInfoData.getQrcodeUrl());
            intent_code.putExtra(ROOM, groupData);
            startActivityForResult(intent_code, REQUEST_QRCODE);

            //}

//            case R.id.tv_add_group_info:
//                editMembers(REQUEST_ADD);
//                break;
//            case R.id.tv_delete_group_info:
//                intent = new Intent(this, EditMembersActivity.class);
//                intent.putExtra("roomId", roomId);
//                intent.putExtra("roleType", roleType);
//                intent.putExtra(EditMembersActivity.REQUEST_KEY, REQUEST_DELETE);
//                startActivityForResult(intent, REQUEST_DELETE);
//                break;
        } else if (i == R.id.delete_group_info) {
            if (chatGroupInfoData == null)
                return;
            removeGroup();

        } else if (i == R.id.dissolve_group_info) {
            if (chatGroupInfoData == null)
                return;
            dissolveGroup();

        } else if (i == R.id.img_group_info) {
            picturesOfChat();

        } else if (i == R.id.clear_msg_group_info) {
            deleteRecords();

        } else if (i == R.id.report_group_info) {
        } else if (i == R.id.members_all_group_info) {
            Intent moreIntent = new Intent(this, MoreGroupMembersActivity.class);
            moreIntent.putExtra("roomId", roomId);
            moreIntent.putExtra("roleType", roleType);
            moreIntent.putExtra("members", members);
            startActivityForResult(moreIntent, REQUEST_MORE);

        } else {
        }
    }

    /**
     * @return void
     * @Title: picturesOfChat
     * @param:
     * @Description: 聊天图片
     */
    private void picturesOfChat() {
        Intent intent = new Intent(this, ChatPhotoWall.class);
        List<MessageBean> messages = ChatMessageManager.getInstance()
                .getMessagesOfImage(roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT, "");
        MessageBean iMBean;
        if (messages.size() > 0) {
            iMBean = messages.get(0);
        } else {
            iMBean = new MessageBean();
        }
        intent.putExtra(ChatActivity.MESSAGEBEAN, iMBean);
        startActivity(intent);
    }

    /**
     * 清除群的聊天记录
     */
    public void deleteRecords() {
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(this, null,
                getString(R.string.chat_delete_chat_records_toast),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, this.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = ChatMessageManager
                                .getInstance()
                                .destoryChatMessage(
                                        roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT, "", true);
                        ChatUtil.sendUpdateNotify(ChatGroupInfoAcitivty.this,
                                MessageInfoReceiver.EVENT_CLEAR, roomId
                                        + "@" + IMTypeUtil.BoxType.GROUP_CHAT, roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                        String showMsg = getString(isDeleted ? R.string.chat_listdialog_delete_success
                                : R.string.chat_listdialog_delete_fail);
                        ShowUtil.showToast(ChatGroupInfoAcitivty.this, showMsg);
                        dialog.dismiss();
                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();

    }

    /**
     * @return void
     * @Title: editMembers
     * @param:
     * @Description: 编辑成员
     */
    @SuppressLint("UseSparseArrays")
    private void editMembers(int requestKey) {
        if (chatGroupInfoData == null)
            return;
        List<ChatGroupMemberBean> remotes = new ArrayList<ChatGroupMemberBean>();
        int size = participantVoList.size();
        if (requestKey == REQUEST_ADD) {
            if (members >= GROUP_MEMBERS_MAX) {
                ShowUtil.showToast(IMClient.getInstance().getContext(), TextUtils.getStringFormat(IMClient.getInstance().getContext(), R.string.group_add_max_hint, GROUP_MEMBERS_MAX));
                return;
            }
            ArrayList<ChatGroupMemberBean> contacts = ChatContactManager
                    .getInstance().getContactsForMember();
            HashMap<String, String> ids = new HashMap<String, String>();
            for (ChatGroupMemberBean bean : participantVoList) {
                ids.put(String.valueOf(bean.getUserId()),
                        String.valueOf(bean.getUserId()));
            }
            for (ChatGroupMemberBean bean : contacts) {
                if (ids.containsKey(String.valueOf(bean.getUserId())))
                    continue;
                remotes.add(bean);
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (requestKey == REQUEST_SET_ADMIN
                        && participantVoList.get(i).getRoleType() == IMTypeUtil.RoleType.ADMINS) {
                    continue;
                } else if (participantVoList.get(i).getRoleType() >= roleType) {
                    continue;
                }
                remotes.add(participantVoList.get(i));
            }
        }
        Intent intent = new Intent(this, ChoseContactsActivity.class);
        intent.putExtra(ChoseContactsActivity.ISMULTISELECT,true);
        intent.putExtra(RemoteMemberBean.MEMBES, selectMembers);
        startActivityForResult(intent, requestKey);
    }

    /**
     * @return void
     * @Title: dissolveGroup
     * @param:
     * @Description: 解散群
     */
    private void dissolveGroup() {
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(this,
                getString(R.string.chat_group_info_menu_dissolve_hint),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dissolveGroupToServer();
                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();
    }

    /**
     * @return void
     * @Title: removeGroup
     * @param:
     * @Description: 删除并退出群
     */
    private void removeGroup() {
        CustomBaseDialog dialog = CustomBaseDialog
                .getDialog(
                        this,
                        getString(roleType == IMTypeUtil.RoleType.OWNERS ? R.string.chat_group_info_menu_remove_hint
                                : R.string.chat_group_info_menu_remove_hint_member),
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        },
                        getString(roleType == IMTypeUtil.RoleType.OWNERS ? R.string.chat_group_info_menu_remove_sure
                                : R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                if (roleType == IMTypeUtil.RoleType.OWNERS) {
                                    Intent intent = new Intent(IMClient.getInstance().getContext(), EditMembersActivity.class);
                                    intent.putExtra("roomId", roomId);
                                    intent.putExtra("roleType", roleType);
                                    intent.putExtra(EditMembersActivity.REQUEST_KEY, REQUEST_SET_OWNER);
                                    startActivityForResult(intent, REQUEST_SET_OWNER);
                                } else {
                                    ChatGroupMemberBean bean = new ChatGroupMemberBean();
                                    try {
                                        bean.setId(Long.parseLong(IMClient
                                                .getInstance().getSSOUserId()));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    bean.setMtalkDomain(URLConfig
                                            .getServerName());
                                    bean.setRoleType(roleType);
                                    currentMember = bean;
                                    removeMember(true);
                                }
                            }
                        });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();
    }

    /**
     * @return void
     * @Title: deleteGroupToServer
     * @param:
     * @Description:请求服务器解散群
     */
    private void dissolveGroupToServer() {
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        operatorModel.httpJsonRequest(Method.DELETE,
                String.format(URLConfig.CHAT_GROUP_DISSOLVE, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                showResutToast(baseBean.getCode());
                                if (baseBean.isResult()) {
                                    backForOutGroup();
                                    ChatGroupManager.getInstance()
                                            .setGroupInValid(roomId);
                                }
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                    }
                });
    }

    /**
     * @return void
     * @Title: getMembers
     * @param:
     * @Description: 获取群信息
     */
    public void getGroupInfo(final boolean isLoadFromServer) {
        if (isLoadFromServer)
            showLoading();
        if (groupInfoModel == null) {
            groupInfoModel = new WBaseModel<ChatGroupInfoResultBean>(this,
                    ChatGroupInfoResultBean.class);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("pageIndex", 1);
        parmas.put("pageSize", MAX_SHOW_MEMBERS);
        groupInfoModel.httpJsonRequest(Method.GET,
                String.format(URLConfig.CHAT_GRUOP_INFO, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof ChatGroupInfoResultBean) {
                                final ChatGroupInfoResultBean bean = (ChatGroupInfoResultBean) data;
                                if (bean.isResult()) {
                                    if (bean.getData() == null
                                            || bean.getData().size() == 0) {
                                        ShowUtil.showToast(
                                                ChatGroupInfoAcitivty.this,
                                                getString(R.string.warning_service_error));
                                        return;
                                    }
                                    chatGroupInfoData = bean.getData().get(0);
                                    roleType = chatGroupInfoData.getRoleType();
                                    members = chatGroupInfoData.getNowCnt();
                                    publicType = chatGroupInfoData
                                            .getPublicType();
                                    showPageStyleByRole();
                                    String time = TimeUtil.getCurrentMillTime(
                                            chatGroupInfoData.getCreateTime(),
                                            getString(R.string.chat_group_info_create_time));
                                    create_group_info.setVisibility(View.VISIBLE);
                                    create_group_info.setText(TextUtils.getStringFormat(
                                            IMClient.getInstance().getContext(),
                                            R.string.chat_group_info_create,
                                            time));
                                    if (chatGroupInfoData != null) {
                                        nickname_my_group_info.setRight(TextUtils
                                                .getString(chatGroupInfoData
                                                        .getNickName()));
                                        if (android.text.TextUtils
                                                .isEmpty(chatGroupInfoData
                                                        .getGroupName())) {
                                            chatGroupInfoData
                                                    .setGroupName(ChatGroupManager
                                                            .getInstance()
                                                            .getNameOfUnNameGroup(
                                                                    roomId));
                                        }
                                    }
                                    if (roleType < 1) {
                                        ShowUtil.showToast(
                                                IMClient.getInstance().getContext(),
                                                R.string.chat_group_not_in);
                                    }
                                    ChatLoadManager.getInstance()
                                            .updateChatGroupInfo(handler,
                                                    roomId, bean,
                                                    chatGroupInfoData,
                                                    isLoadFromServer,
                                                    cacheGroupInfo, true);
                                }
                            } else if (data instanceof WBaseBean) {
                                if (isLoadFromServer) {
                                    WBaseBean baseBean = (WBaseBean) data;
                                    showResutToast(baseBean.getCode());
                                }
                            } else {
                                if (isLoadFromServer)
                                    ShowUtil.showHttpRequestErrorResutToast(
                                            IMClient.getInstance().getContext(),
                                            httpStatusCode, data);
                            }
                        } else {
                            if (isLoadFromServer) {
                                ShowUtil.showHttpRequestErrorResutToast(
                                        IMClient.getInstance().getContext(),
                                        httpStatusCode, data);
                            }
                        }
                        initSwitch();
                        if (isLoadFromServer) {
                            dissmisLoading();
                        }
                    }
                });
    }

    /**
     * @return void
     * @Title: initSwitch
     * @param:
     * @Description: 初始化开关
     */
    private void initSwitch() {
        msg_on_off_group_info.setChecked(ChatGroupManager.getInstance()
                .getIsNotify(roomId));
        top_group_info.setChecked(ChatGroupManager.getInstance().getIsTop(
                roomId));
        book_group_info.setChecked(ChatGroupManager.getInstance()
                .getIsBookTolist(roomId));
        if (roleType > IMTypeUtil.RoleType.ADMINS) {
            book_group_info.setSwithchEnable(false);
        }
    }

    private void addMembers(final SparseArrayList<ContactBean> adds,
                            final boolean needToInvite) {
        if (adds == null || isSended||adds.size()==0)
            return;
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        int size = adds.size();
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("role", IMTypeUtil.RoleType.MEMBERS);
            map.put("userId", adds.valueAt(i).getFriendID());
            map.put("mtalkDomain", URLConfig.getServerName());
            list.add(map);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("needToInvite", needToInvite ? 1 : 0);
        parmas.put("memberList", list);
        operatorModel.httpJsonRequest(Method.POST,
                String.format(URLConfig.CHAT_GRUOP_INFO, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                if (baseBean.isResult()) {
                                    if (!needToInvite) {
                                        members += adds.size();
                                        ChatMembersManager.getInstance()
                                                .insertGroupMembers(roomId,
                                                        adds);
                                        setMembers();
                                        ChatUtil.sendUpdateNotify(
                                                ChatGroupInfoAcitivty.this,
                                                MessageInfoReceiver.EVENT_UPDATE_CHAT,
                                                "", roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                                    }
                                    showResutToast(baseBean.getCode());
                                } else {
                                    if (baseBean.getCode().equals("mx11080023")) {
                                        addMembers(adds, true);
                                    } else {
                                        showResutToast(baseBean.getCode());
                                    }

                                }
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                        isSended = true;
                    }
                });
    }

    /**
     * @return void
     * @Title: getMembers
     * @param:
     * @Description: 删除群成员
     */
    private void deleteMember(final SparseArrayList<ContactBean> deletes,
                              final boolean isOutGroup) {
        if (deletes == null)
            return;
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        int size = deletes.size();
        List<HashMap<String, Object>> participantList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < size; i++) {
            ContactBean friendBean = deletes.valueAt(i);
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("role", friendBean.getRole());
            String userId = String.valueOf(friendBean.getFriendID());
            item.put("userId", userId);
            participantList.add(item);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("needToInvite",0);
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
                                    if (isOutGroup) {
                                        backForOutGroup();
                                    } else {
                                        ChatMembersManager.getInstance()
                                                .removeMembers(roomId, deletes);
                                        members -= deletes.size();
                                        setMembers();
                                    }
                                    if (sessionId != null
                                            && currentMember != null
                                            && IMClient
                                            .getInstance()
                                            .getSSOUserId()
                                            .equals(String.valueOf(currentMember
                                                    .getId()))) {
                                        boolean isDeleted = ChatMessageManager
                                                .getInstance()
                                                .destoryChatMessage(sessionId,
                                                        null);
                                        if (isDeleted) {
                                            ChatUtil.sendUpdateNotify(
                                                    ChatGroupInfoAcitivty.this,
                                                    MessageInfoReceiver.EVENT_UPDATE_CHAT,
                                                    "msg", roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT);
                                        }
                                    }
                                }
                                showResutToast(baseBean.getCode());
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                    }

                });
    }

    /**
     * @return void
     * @Title: backForOutGroup
     * @param:
     * @Description: 退出当前群，跳转操作
     */
    private void backForOutGroup() {
        ChatMessageManager.getInstance().destoryChatMessage(
                roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT, null);
        ChatMembersManager.getInstance().removeAll(roomId);
        ChatGroupManager.getInstance().removeGroupById(roomId);
        try {
            ArrayList<FragmentActivity> activitys = IMClient
                    .getInstance().getActivitys();
            for (FragmentActivity activity : activitys) {
                if (activity instanceof ChatActivity) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IMClient.isGroupChange = true;
        ChatGroupInfoAcitivty.this.finish();

    }
    private String selectMembers;
    private void setMembers() {
        HashMap<String, Object> data = ChatLoadManager.getInstance()
                .getGroupMembers(roomId, 1, MAX_SHOW_MEMBERS);
        if (data == null)
            return;
        if (data.size() == 0) {
            return;
        }
        ArrayList<ChatGroupMemberBean> groupMembers = (ArrayList<ChatGroupMemberBean>) data
                .get("data");
        if (groupMembers == null)
            return;
        if (participantVoList == null) {
            participantVoList = new ArrayList<ChatGroupMemberBean>();
        }
        participantVoList.clear();
        selectMembers= String.valueOf(data.get("memberIds"));
        setMembersView();
        participantVoList.addAll(groupMembers);
        if (chatGroupInfoData != null)
            nickname_my_group_info.setRight(TextUtils
                    .getString(chatGroupInfoData.getNickName()));
        try {
            admins = Integer.parseInt(String.valueOf(data.get("admins")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        delete_group_info.setEnabled(participantVoList.size() > 1);
        setGroupMemberButton();
        setAdapter(participantVoList);
    }

    /***
     * 设置群成员管理按钮
     */
    private void setGroupMemberButton() {
        ChatGroupMemberBean addBean = new ChatGroupMemberBean();
        addBean.setUserId(-1);
        participantVoList.add(addBean);
        if (roleType > IMTypeUtil.RoleType.MEMBERS) {//管理员或者群主才有删除权限
            ChatGroupMemberBean deleteBean = new ChatGroupMemberBean();
            deleteBean.setUserId(-2);
            participantVoList.add(deleteBean);
        }
    }

    /**
     * @return void
     * @Title: setMembersView
     * @param:
     * @Description: 设置群成员视图
     */
    private void setMembersView() {
        members_all_group_info
                .setTitle(getString(R.string.chat_groupinfo_members_title)
                        + "(" + members + ")");
        titleText.setText(getString(R.string.chat_group_info_title) + "("
                + members + ")");
        initBaseInfo(true, members);
    }

    /**
     * @return void
     * @Title: setAdapter
     * @param:
     * @Description: 填充适配器
     */
    private void setAdapter(List<ChatGroupMemberBean> participantVoList) {
        if (participantVoList == null)
            return;
        if (adapter == null) {
            adapter = new CommonAdapter<ChatGroupMemberBean>(this,
                    participantVoList, R.layout.item_members) {
                @Override
                public void convert(final ViewHolder helper,
                                    final ChatGroupMemberBean item) {
                    setItemData(helper, item);
                }
            };
            if (members_group_info != null) {
                members_group_info.setAdapter(adapter);
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * @return void
     * @Title: setItemData
     * @param:
     * @Description: 填充item数据
     */
    private void setItemData(final ViewHolder helper,
                             final ChatGroupMemberBean item) {
        final long id = item.getUserId();
        TextView name_members_item = helper.getView(R.id.name_members_item);
        name_members_item.setVisibility(id > 0 ? View.VISIBLE : View.INVISIBLE);
        if (id > 0) {
            helper.setAvatarImageByUrl(R.id.avatar_members_item, item.getAvatar(),
                    DisplayImageConfig.normalAvatarImageOptions);
            name_members_item.setText(item.getNickName());
            ImageView tag_members_item = helper.getView(R.id.tag_members_item);
            switch (item.getRoleType()) {
                case IMTypeUtil.RoleType.OWNERS:
                    tag_members_item.setVisibility(View.VISIBLE);
                    tag_members_item.setImageResource(R.drawable.ic_head_group_ower);
                    break;
                case IMTypeUtil.RoleType.ADMINS:
                    tag_members_item.setVisibility(View.VISIBLE);
                    tag_members_item.setImageResource(R.drawable.ic_head_group_admin);
                    break;
                case IMTypeUtil.RoleType.MEMBERS:
                    tag_members_item.setVisibility(View.GONE);
                    break;
                default:
                    tag_members_item.setVisibility(View.GONE);
                    break;
            }
            currentPosition = adapter.getPosition(item);
        } else {
            ImageView avatar_members_item = helper.getView(R.id.avatar_members_item);
            avatar_members_item.setImageResource(id == -1 ? R.drawable.ic_add_members : R.drawable.ic_delete_group_member);
        }
        helper.getConvertView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (id == -1) {
                    editMembers(REQUEST_ADD);
                    return;
                }
                if (id == -2) {
                    Intent intent = new Intent(ChatGroupInfoAcitivty.this, EditMembersActivity.class);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("roleType", roleType);
                    intent.putExtra(EditMembersActivity.REQUEST_KEY, REQUEST_DELETE);
                    startActivityForResult(intent, REQUEST_DELETE);
                    return;
                }
                isOverrun = admins >= GROUP_ADMIN_MAX
                        && currentPosition >= GROUP_ADMIN_MAX
                        && item.getRoleType() != IMTypeUtil.RoleType.ADMINS;
                currentMember = item;
                showMenuDialog(item);
            }
        });
    }

    /**
     * @return void
     * @Title: doBack
     * @param:
     * @Description: 执行回滚操作
     */
    private void doBack(boolean isDeleteGroup) {
        Intent intent = new Intent();
        String name = "";
        if (chatGroupInfoData != null)
            name = chatGroupInfoData.getGroupName();
        intent.putExtra("name", name);
        intent.putExtra("hasChanged", hasChanged && name.length() > 0);
        intent.putExtra("isGroup", true);
        intent.putExtra("isDeleteGroup", isDeleteGroup);
        setResult(RESULT_OK, intent);
        this.finish();

    }

    @Override
    public void onBackPressed() {
        doBack(false);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == RESULT_OK) {
            if (arg2 == null)
                return;
            if (arg0 == REQUEST_EDIT) {
                String content = arg2.getStringExtra("content");
                if (arg2.getBooleanExtra("isCancal", false)) {
                    return;
                }
                switch (arg2.getIntExtra(EditGroupInfoActivity.EDIT_MODE, -1)) {
                    case EditGroupInfoActivity.EDIT_GROUP_NAME:
                        nickname_group_info.setRight(content);
                        chatGroupInfoData.setGroupName(content);
                        chatGroupInfoData.setRoomNameUpdated(1);
                        updateGroupName(content);
                        break;
                    case EditGroupInfoActivity.EDIT_GROUP_MY_NAME:
                        nickname_my_group_info.setRight(content);
                        updateMyNickName(content);
                        break;
                    default:
                        break;
                }
            } else if (arg0 == REQUEST_AVATAR) {
                String avatar = arg2.getStringExtra("avatarUrl");
                if (avatar != null) {
                    if (chatGroupInfoData != null) {
                        hasChanged = !avatar.equals(chatGroupInfoData
                                .getPhotoUrl());
                        chatGroupInfoData.setPhotoUrl(avatar);
                    }
                    updateGruopInfo("", avatar);
                }
            } else if (arg0 == REQUEST_ADD || arg0 == REQUEST_DELETE
                    || arg0 == REQUEST_SET_ADMIN || arg0 == REQUEST_SET_OWNER) {
                boolean isCancal = arg2.getBooleanExtra("isCancal", false);
                if (isCancal)
                    return;
                RemoteMemberBean setBean = (RemoteMemberBean) arg2
                        .getSerializableExtra(RemoteMemberBean.MEMBES);
                doResultForEdit(arg0, setBean);
            } else if (arg0 == REQUEST_QRCODE) {
                String qrcodeUrl = arg2
                        .getStringExtra(QuickmarkActivity.QRCODE);
                if (qrcodeUrl == null || qrcodeUrl.length() == 0)
                    return;
                chatGroupInfoData.setQrcodeUrl(qrcodeUrl);
            } else if (arg0 == REQUEST_MORE) {
                boolean hasChange = arg2.getBooleanExtra("hasChange", false);
                if (hasChange) {
                    members = arg2.getIntExtra("members", members);
                    setMembersView();
                    setMembers();
                }
            }
        }
        super.onActivityResult(arg0, arg1, arg2);
    }

    /**
     * @return void
     * @Title: doResultForEdit
     * @param:
     * @Description: 执行编辑的回执操作
     */
    private void doResultForEdit(int requestCode, RemoteMemberBean setBeans) {
        if (setBeans == null)
            return;
        switch (requestCode) {
            case REQUEST_ADD:
                isSended = false;
                addMembers(setBeans.getMembsers(), false);
                break;
            case REQUEST_DELETE:
                deleteMember(setBeans.getMembsers(), false);
                break;
            case REQUEST_SET_ADMIN:
//                setManager(setBeans.getMembsers(), false);
                break;
            case REQUEST_SET_OWNER:
//                setOwner(setBeans.getMembsers());
                break;
            default:
                break;
        }
    }

    /**
     * @return void
     * @Title: updateGruopInfo
     * @param:
     * @Description: 更新群信息
     */
    public void updateGruopInfo(String content, final String photoUrl) {
        showLoading(getString(R.string.chat_group_info_uploading_avatar));
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("photoUrl", TextUtils.getString(photoUrl));
        operatorModel.httpJsonRequest(Method.PUT,
                String.format(URLConfig.CHAT_GRUOP_UPDATE, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                if (baseBean.isResult()) {
                                    ChatGroupManager
                                            .getInstance()
                                            .updateGroupAvatar(roomId, photoUrl);
                                }
                            } else {
                                ShowUtil.showToast(
                                        ChatGroupInfoAcitivty.this,
                                        getString(R.string.warning_service_error));
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                    }
                });
    }

    /**
     * 显示菜单栏对话框
     */
    private void showMenuDialog(ChatGroupMemberBean item) {
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
    private void setMenuItems(ActionSheet menuView, ChatGroupMemberBean item) {
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
                        String setManager = getString(item.getRoleType() == IMTypeUtil.RoleType.ADMINS ? R.string.chat_group_info_menu_admin_cancal
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
                else if (item.getRoleType() > IMTypeUtil.RoleType.MEMBERS)// 同级别或高级别不能移除
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

    @Override
    public void onActionSheetItemClick(int itemPosition) {
        switch (itemPosition) {
            case 0:
                lookInfo();
                break;
            case 1:
                goToChat();
                break;
            case 2:
                if (isOverrun || roleType == IMTypeUtil.RoleType.ADMINS) {
                    removeMember(false);
                } else {
                    List<ChatGroupMemberBean> list = new ArrayList<ChatGroupMemberBean>();
                    list.add(currentMember);
                    setManager(list, currentMember.getRoleType() == IMTypeUtil.RoleType.ADMINS);
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
     * @Title: removeMember
     * @param:
     * @Description: 移除当前群成员
     */
    private void removeMember(boolean isOutGroup) {
        if (currentMember == null)
            return;
        SparseArrayList<ContactBean> list = new SparseArrayList<ContactBean>();
        ContactBean bean=new ContactBean();
        bean.setFriendID((int)currentMember.getUserId());
        bean.setRole(currentMember.getRoleType());
        list.put(0,bean);
        deleteMember(list, isOutGroup);
    }

    /**
     * @return void
     * @Title: setManager
     * @param:
     * @Description: 设置管理员
     */
    private void setManager(final List<ChatGroupMemberBean> deletes,
                            final boolean isRemove) {
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        int size = deletes.size();
        List<HashMap<String, Object>> addList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < size; i++) {
            HashMap<String, Object> object = new HashMap<String, Object>();
            String userId = String.valueOf(deletes.get(i).getUserId());
            object.put("userId", userId);
            object.put("mtalkDomain", deletes.get(i).getMtalkDomain());
            addList.add(object);
            deletes.get(i).setRoleType(
                    isRemove ? IMTypeUtil.RoleType.MEMBERS : IMTypeUtil.RoleType.ADMINS);
        }
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
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                if (baseBean.isResult()) {
                                    ChatMembersManager.getInstance()
                                            .updateGroupRoleForList(roomId,
                                                    deletes);
                                    setMembers();
                                    adapter.notifyDataSetChanged();
                                }
                                showResutToast(baseBean.getCode());
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                    }

                });

    }

    /**
     * @return void
     * @Title: setOwner
     * @param:
     * @Description: 设置群主
     */
    private void setOwner(List<ChatGroupMemberBean> membsers) {
        if (membsers == null)
            return;
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("ownerId", String.valueOf(membsers.get(0).getUserId()));
        operatorModel.httpJsonRequest(Method.PUT,
                String.format(URLConfig.CHAT_GRUOP_UPDATE, roomId), parmas,
                new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                if (baseBean.isResult()) {
                                    backForOutGroup();
                                }
                                showResutToast(baseBean.getCode());
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        dissmisLoading();
                    }
                });
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
        userBean.setAvatar(currentMember.getAvatar());
        userBean.setMxId(String.valueOf(currentMember.getUserId()));
        userBean.setName(currentMember.getNickName());
        ChatUtil.gotoChatRoom(this, userBean);
    }

    /**
     * @return void
     * @Title: lookInfo
     * @param:
     * @Description: 查看用户信息
     */
    private void lookInfo() {
        if (currentMember == null)
            return;
    }

    /**
     * @return void
     * @Title: saveToContacts
     * @param:
     * @Description:保存到通讯录
     */
    public void saveToContacts(final boolean isOpen) {
        if (operatorModel == null) {
            operatorModel = new WBaseModel<WBaseBean>(this, WBaseBean.class);
        }
        HashMap<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("nickName", "");
        parmas.put("saveToContacts", isOpen ? 1 : 0);
        operatorModel.httpJsonRequest(Method.PUT,
                String.format(URLConfig.CHAT_GROUP_SET_MY_NICKNAME, roomId),
                parmas, new WRequestCallBack() {
                    @Override
                    public void receive(int httpStatusCode, Object data) {
                        boolean isOk = false;
                        if (data != null) {
                            if (data instanceof WBaseBean) {
                                WBaseBean baseBean = (WBaseBean) data;
                                showResutToast(baseBean.getCode());
                                if (baseBean.isResult()) {
                                    if (ChatGroupManager.getInstance()
                                            .setIsBookTolist(roomId,
                                                    isOpen ? 1 : 0)) {
                                        isOk = true;
                                    }
                                }

                            } else {
                                ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                        getString(R.string.warning_service_error));
                            }
                        } else {
                            ShowUtil.showToast(ChatGroupInfoAcitivty.this,
                                    getString(R.string.warning_service_error));
                        }
                        if (!isOk) {
                            book_group_info.setChecked(!isOpen);
                        }
                        dissmisLoading();
                    }
                });
    }

    /**
     * @return boolean
     * @Title: isSelf
     * @param:
     * @Description: 是否为自己
     */
    public boolean isSelf(String userId) {
        return myUserId.equals(userId);
    }

    @Override
    public void onSwitchChanger(View view, boolean isOpen) {
        if (chatGroupInfoData == null)
            return;
        int i = view.getId();
        if (i == R.id.msg_on_off_group_info) {
            setForbid(isOpen);

        } else if (i == R.id.top_group_info) {
            setTop(isOpen);

        } else if (i == R.id.book_group_info) {
            saveToContacts(isOpen);

        } else {
        }
    }

    /**
     * @return void
     * @Title: setForbid
     * @param:
     * @Description: 设置免打扰
     */
    private void setForbid(boolean isOpen) {
        if (!ChatGroupManager.getInstance().setIsNotify(roomId, isOpen ? 1 : 0)) {
            msg_on_off_group_info.setChecked(!isOpen);
        } else {
            ChatSessionManager.getInstance().updateForbid(
                    roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT, isOpen ? 1 : 0);
        }
    }

    /**
     * @return void
     * @Title: setTop
     * @param:
     * @Description: 设置置顶
     */
    private void setTop(boolean isOpen) {
        if (isOpen && topNum >= ChatUtil.MAX_TOP) {
            ShowUtil.showToast(this, R.string.chat_listdialog_top_limit);
            top_group_info.setChecked(!isOpen);
            return;
        }
        if (!ChatGroupManager.getInstance().setIsTop(roomId, isOpen ? 1 : 0)) {
            top_group_info.setChecked(!isOpen);
        } else {
            if (ChatSessionManager.getInstance().updateTop(
                    roomId + "@" + IMTypeUtil.BoxType.GROUP_CHAT, isOpen)) {
                if (isOpen)
                    topNum++;
                else
                    topNum--;
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (groupInfoModel != null) {
            groupInfoModel.cancelRequest();
            groupInfoModel = null;
        }
        if (operatorModel != null) {
            operatorModel.cancelRequest();
            operatorModel = null;
        }
        if (backgroudBmp != null && !backgroudBmp.isRecycled()) {
            backgroudBmp.recycle();
            backgroudBmp = null;
        }
        ChatLoadManager.getInstance().reSetChatGroupInfoHandler();
        // IMClient.sImageLoader.stop();
        adapter = null;
        if (members_group_info != null) {
            members_group_info.clearAnimation();
            members_group_info.destroyDrawingCache();
            members_group_info = null;
        }
        participantVoList.clear();
        participantVoList = null;
        chatGroupInfoData = null;
        currentMember = null;
        System.gc();
    }
}
