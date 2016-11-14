package chat.session.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.HashMap;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.util.output.ShowUtil;
import chat.dialog.CustomBaseDialog;
import chat.image.DisplayImageConfig;
import chat.image.activity.ChatPhotoWall;
import chat.manager.ChatContactManager;
import chat.manager.ChatLoadManager;
import chat.manager.ChatMessageManager;
import chat.manager.ChatSessionManager;
import chat.service.MessageInfoReceiver;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;

/**
 * 聊天设置页面
 */
public class ChatSettings extends BaseActivity implements OnClickListener {
    public static final int REQUESE_BACK = 1;
    private static final String TAG = "ChatSettings";
    private TextView title;// 标题
    private ImageView back;// 返回
    private String toId;// id
    private TextView tvActivityName;
    private CheckBox checkTop;// 设置置顶
    private CheckBox checkForbid;// 设置免打扰
    private TextView nick;// 昵称
    private TextView id;// id
    private ImageView avatar;// 头像
    private ImUserBean userBean;
    private String userId;
    private MessageBean iMBean;
    private String shopId;
    private int topNums;
    private String shopid;
    /**
     * 粉丝状态
     */
    private String follow_state;
    /**
     * 清除记录
     */
    private boolean isDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = IMClient.getInstance().getSSOUserId();
        try {
            MessageBean data = (MessageBean) getIntent().getSerializableExtra(
                    ChatActivity.SESSION_DATA);
            toId = data.getChatWith();
        } catch (Exception e) {
            e.printStackTrace();
            if (toId == null) {
                toId = "";
            }
        }
        setContentView(R.layout.activity_chatsettings);
        initEvents();
        initData();
    }

    @Override
    protected void initView() {

        title = (TextView) findViewById(R.id.titleText);
        back = (ImageView) findViewById(R.id.back);
        findViewById(R.id.chat_pictures).setOnClickListener(this);
        checkTop = (CheckBox) findViewById(R.id.set_chat_istop);
        checkForbid = (CheckBox) findViewById(R.id.set_chat_forbid);
        nick = (TextView) findViewById(R.id.home_nickname);
        tvActivityName = (TextView) findViewById(R.id.tvActivityName);
        id = (TextView) findViewById(R.id.home_id);
        avatar = (ImageView) findViewById(R.id.avatar);
    }

    @Override
    protected void initEvents() {
        back.setClickable(true);
        back.setOnClickListener(this);
        checkTop.setOnClickListener(this);
        checkForbid.setOnClickListener(this);
    }
    @Override
    protected void initData() {
        title.setText(R.string.chat_textview_chatset);
        HashMap<String, Object> data = ChatLoadManager.getInstance()
                .getSessions(false, true);
        try {
            if(data != null){
                topNums = (Integer) data.get("allTopCount");
            }
            shopid = getIntent().getStringExtra("shopid");
            if (shopid != null) {
                userBean = ChatContactManager.getInstance().getImUserBean(toId,
                        shopid);
            } else
                userBean = ChatContactManager.getInstance().getImUserBean(toId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String idStr = toId;
        if (idStr.contains("_")) {
            this.findViewById(R.id.chat_pictures).setVisibility(View.GONE);
            this.findViewById(R.id.chatsetting_DND).setVisibility(View.GONE);
            this.findViewById(R.id.line_p_c).setVisibility(View.GONE);
            this.findViewById(R.id.line_c_m).setVisibility(View.GONE);
            idStr = chat.common.util.TextUtils.getString(shopid);
        }
        id.setText("ID:" + idStr);
        if (userBean != null) {
            if (!TextUtils.isEmpty(userBean.getRemark())) {
                nick.setText(userBean.getRemark());
            } else if (userBean.getName() != null)
                nick.setText(userBean.getName());
            checkTop.setChecked(userBean.getIsTop() == 1);
            checkForbid.setChecked(userBean.getIsForbid() == 1);
        }
        setUserHeader();

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int i = v.getId();
        if (i == R.id.back) {
            doback();

        } else if (i == R.id.chat_pictures) {
            if (userBean == null)
                return;
            intent = new Intent(this, ChatPhotoWall.class);
            List<MessageBean> messages = ChatMessageManager.getInstance()
                    .getMessagesOfImage(
                            userBean.getMxId() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT, shopId);
            if (messages.size() > 0) {
                iMBean = messages.get(0);
            } else {
                iMBean = new MessageBean();
            }
            intent.putExtra(ChatActivity.MESSAGEBEAN, iMBean);
            intent.putExtra(ChatActivity.TOID, toId);
            startActivity(intent);

        } else if (i == R.id.delete_chat_records) {
            deleteRecords();

        } else if (i == R.id.set_chat_istop) {
            if (userBean == null) {
                checkTop.setChecked(!checkTop.isChecked());
                return;
            }
            setTop(checkTop.isChecked());

        } else if (i == R.id.set_chat_forbid) {
            if (userBean == null) {
                checkForbid.setChecked(!checkForbid.isChecked());
                return;
            }
            setIsForbid(checkForbid.isChecked());

        } else {
        }
    }

    /**
     * @return void
     * @Title: doback
     * @param:
     * @Description: 执行回滚操作
     */
    private void doback() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userinfo", userBean);
        intent.putExtra("nickname", userBean.getName());
        intent.putExtra("isDeleted", isDeleted);
        if (!TextUtils.isEmpty(follow_state))
            intent.putExtra(ChatActivity.FOLLOW_STATE, follow_state);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 好友详情页
     */
    public void stratPersonal(View v) {
        if (userBean == null)
            return;

    }

    /**
     * 删除与某人的聊天记录
     */
    public void deleteRecords() {
        if (userBean == null)
            return;
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(ChatSettings.this, null,
                getString(R.string.chat_textview_is_delete_chat_records),
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
                        isDeleted = ChatMessageManager.getInstance().destoryChatMessage(userBean.getMxId() + "@"
                                + IMTypeUtil.BoxType.SINGLE_CHAT, shopId, true);
                        String showMsg = getString(isDeleted ? R.string.chat_listdialog_delete_success
                                : R.string.chat_listdialog_delete_fail);
                        ShowUtil.showToast(ChatSettings.this, showMsg);
                        if (userBean.getMxId().contains("_")) {
                            ChatUtil.sendUpdateNotify(
                                    ChatSettings.this, MessageInfoReceiver.EVENT_UPDATE_MOBIZ,
                                    shopId, userBean.getMxId() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT);
                        }
                        ChatUtil.sendUpdateNotify(
                                ChatSettings.this, MessageInfoReceiver.EVENT_CLEAR,
                                userBean.getMxId() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT, userBean.getMxId() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT);
                        dialog.dismiss();
                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        doback();
        super.onBackPressed();
    }

    /**
     * 更新本地置顶状态
     *
     * @param isTop
     */
    public void setTop(boolean isTop) {
        if (userBean == null) {
            checkTop.setChecked(!isTop);
            return;
        }
        if (isTop && topNums >= ChatUtil.MAX_TOP) {// 置顶数超限
            ShowUtil.showToast(this, R.string.chat_listdialog_top_limit);
            checkTop.setChecked(!isTop);
            return;
        }

        boolean isSetSuccess = false;
        if (shopid != null) {
            isSetSuccess = ChatContactManager.getInstance().setIsTop(
                    userBean.getMxId(), shopid, isTop ? 1 : 0);
        } else {
            isSetSuccess = ChatContactManager.getInstance().setIsTop(
                    userBean.getMxId(), isTop ? 1 : 0);
        }
        String showMsg = "";
        if (isSetSuccess) {
            String sessionId = userBean.getMxId() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT;
            ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
                    MessageInfoReceiver.EVENT_UPDATE_SETTING, userBean.getMxId(),sessionId);
            if (toId.contains("_")) {
                ChatSessionManager.getInstance().updateTopForShop(sessionId, isTop);
            }
            ChatSessionManager.getInstance().updateTop(sessionId, isTop);
            showMsg = getString(isTop ? R.string.chat_listdialog_top_success
                    : R.string.chat_listdialog_top_cancal_success);
            userBean.setIsTop(!isTop ? 0 : 1);
            if (isTop)
                topNums++;
            else
                topNums--;
        } else {
            showMsg = getString(isTop ? R.string.chat_listdialog_top_fail
                    : R.string.chat_listdialog_top_cancal_fail);
            checkTop.setChecked(!isTop);
        }
        ShowUtil.showToast(this, showMsg);
    }

    /**
     * 更新本地免打扰状态
     *
     * @param isForbid 是否设置免打扰
     */
    public void setIsForbid(boolean isForbid) {
        if (userBean == null)
            return;
        boolean isSetSuccess = ChatContactManager.getInstance().setIsForbid(
                userBean.getMxId(), isForbid ? 1 : 0);
        String showMsg = "";
        if (isSetSuccess) {
            ChatSessionManager.getInstance().updateForbid(
                    userBean.getMxId() + "@" + IMTypeUtil.BoxType.SINGLE_CHAT,
                    isForbid ? 1 : 0);
            showMsg = getString(isForbid ? R.string.forbid_set_success
                    : R.string.forbid_cancal_success);
            userBean.setIsForbid(!isForbid ? 0 : 1);// 更新当前对象免打扰
        } else {
            showMsg = getString(isForbid ? R.string.forbid_set_fail
                    : R.string.forbid_cancal_fail);
        }
        ShowUtil.showToast(this, showMsg);
    }

    /**
     * 设置用户头像 add by weyko 2015-06-02
     *
     * @param
     */
    private void setUserHeader() {
        if (userBean == null) {
            return;
        }
        String headUrl = userBean.getAvatar();
        if (headUrl != null && !headUrl.equals("null")
                && headUrl.trim().length() > 0) {
            IMClient.sImageLoader.displayThumbnailImage(headUrl, avatar,
                    DisplayImageConfig.userLoginItemImageOptions,
                    DisplayImageConfig.headThumbnailSize,
                    DisplayImageConfig.headThumbnailSize);
        } else {
            // 设置默认
            avatar.setImageResource(R.drawable.default_head);
        }

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == RESULT_OK) {
            if (arg0 == REQUESE_BACK) {
                if (arg2 == null)
                    return;
                String nickname = arg2.getStringExtra("name");
                follow_state = arg2.getStringExtra(ChatActivity.FOLLOW_STATE);
                nick.setText(nickname);
                userBean.setName(nickname);
            }
        }
        super.onActivityResult(arg0, arg1, arg2);
    }
}
