package chat.session.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.IMClient;
import chat.common.util.ToolsUtils;
import chat.session.activity.ChatActivity;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;

/**
 * Description: 聊天长按菜单工具类
 * Created  by: weyko
 */
public class MenuUtil {
    private static MenuUtil instance;
    private PopupWindow pop;
    private LayoutInflater inflater;
    private ChatActivity activity;
    public MenuUtil() {
    }
    public static MenuUtil getInstance(){
        if(instance==null){
            instance=new MenuUtil();
        }
        return instance;
    }
    public void clear(){
        activity=null;
        instance=null;
    }
    public void showLongMenu(ChatActivity activity, MessageBean bean, View clickView) {
        this.activity=activity;
        inflater= LayoutInflater.from(activity);
        View contentView = null;
        int popupWindowWidth = 0; // 估算要显示的PopupWindow宽度
        int popupWindowHeight = 45;// 估算要显示的PopupWindow高度
        int textPadding = 40;
        PopMenuListener popMenuListener = new PopMenuListener(bean);
        SessionTypeEnum sessionType = bean.getSessionType();
        MsgTypeEnum msgType = bean.getMsgType();
        Context context= IMClient.getInstance().getContext();
        switch (sessionType) {
            case NORMAL:
            case GROUPCHAT:
                switch (msgType) {
                    // 文本消息 =复制+转发+删除
                    case text:

                        contentView = bean.getDirect() == MsgDirectionEnum.In ? inflater
                                .inflate(R.layout.chat_popleft_c_t_d, null)
                                : inflater.inflate(
                                R.layout.chat_popright_c_t_d, null);
                        contentView.findViewById(R.id.chat_copy)
                                .setOnClickListener(popMenuListener);
                        contentView.findViewById(R.id.chat_transpond)
                                .setOnClickListener(popMenuListener);
                        popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                                context,
                                context.getString(R.string.chat_textview_menu_copy)
                                        + context.getString(R.string.chat_textview_menu_transpond)
                                        + context.getString(R.string.chat_textview_menu_delete))
                                + 2 * textPadding;
                        break;
                    // 图片消息= 复制图片+保存到相册+转发+删除
                    case image:
                        contentView = bean.getDirect() == MsgDirectionEnum.In ? inflater
                                .inflate(R.layout.chat_popleft_c_s_t_d, null)
                                : inflater.inflate(
                                R.layout.chat_popright_c_s_t_d, null);
                        contentView.findViewById(R.id.chat_copyphoto)
                                .setOnClickListener(popMenuListener);
                        contentView.findViewById(R.id.chat_saveforalbum)
                                .setOnClickListener(popMenuListener);
                        contentView.findViewById(R.id.chat_transpond)
                                .setOnClickListener(popMenuListener);
                        popupWindowWidth = (int)chat.common.util.TextUtils.getTextViewLength(
                                context,
                                context.getString(R.string.chat_textview_menu_copyphoto)
                                        + context.getString(R.string.chat_textview_menu_saveforalbum)
                                        +  context.getString(R.string.chat_textview_menu_transpond)
                                        + context.getString(R.string.chat_textview_menu_delete))
                                + 3 * textPadding;
                        break;
                    case location:// 地理位置 = 转发+删除
                    case gif: // 动态表情
                    case video://视频
                        contentView = inflater.inflate(R.layout.chat_pop_t_d,
                                null);
                        contentView.findViewById(R.id.chat_transpond)
                                .setOnClickListener(popMenuListener);
                        popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                                context,
                                context.getString(R.string.chat_textview_menu_transpond)
                                        + context
                                        .getString(R.string.chat_textview_menu_delete))
                                + textPadding;
                        break;
                    case audio: // 语音消息 = 听筒模式+删除
                        IMChatAudioBody audioBody = bean.getAttachment();
                        if (audioBody.getLocalUrl() == null) {
                            contentView = inflater.inflate(R.layout.chat_pop_d,
                                    null);
                            contentView.findViewById(R.id.chat_delete)
                                    .setOnClickListener(popMenuListener);
                            popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                                    context,
                                    context.getString(R.string.chat_textview_menu_delete))
                                    + textPadding;
                        } else {
                            contentView = inflater.inflate(
                                    R.layout.chat_pop_e_d, null);
                            contentView.findViewById(R.id.chat_modeofearphone)
                                    .setOnClickListener(popMenuListener);
                            TextView voice_mode = (TextView) contentView
                                    .findViewById(R.id.voice_mode);
                            String text = "";
                            //根据本地存储语音模式信息初始化语音播放模式
                            boolean voiceIsEarMode = ChatUtil.getInstance().getVoiceIsEarMode();
                            activity.setEarPhoneMode(voiceIsEarMode);
                            if (!voiceIsEarMode) {

                                text = context
                                        .getString(R.string.chat_textview_menu_modeofearphone);
                                voice_mode.setText(text);
                            } else {
                                text = context
                                        .getString(R.string.chat_textview_menu_mode_normal);
                                voice_mode.setText(text);
                            }
                            popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                                    context,
                                    text
                                            + context
                                            .getString(R.string.chat_textview_menu_delete))
                                    + textPadding;
                        }
                        break;
                    // default= 转发+删除
                    default:
                        contentView = inflater.inflate(R.layout.chat_pop_t_d,
                                null);
                        contentView.findViewById(R.id.chat_transpond)
                                .setOnClickListener(popMenuListener);
                        popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                                context,
                                context.getString(R.string.chat_textview_menu_transpond)
                                        + context
                                        .getString(R.string.chat_textview_menu_delete))
                                + textPadding;
                        break;
                }
                break;
            case RICH:
                contentView = inflater.inflate(R.layout.chat_pop_d, null);
                contentView.findViewById(R.id.chat_delete)
                        .setOnClickListener(popMenuListener);
                popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                        context,
                        context.getString(R.string.chat_textview_menu_delete))
                        + textPadding;
                break;
            case SS:
                contentView = bean.getDirect() == MsgDirectionEnum.In ? inflater
                        .inflate(R.layout.chat_popleft_c_t_d, null)
                        : inflater.inflate(R.layout.chat_popright_c_t_d,
                        null);
                contentView.findViewById(R.id.chat_copy)
                        .setOnClickListener(popMenuListener);
                contentView.findViewById(R.id.chat_transpond)
                        .setOnClickListener(popMenuListener);
                popupWindowWidth = (int) chat.common.util.TextUtils.getTextViewLength(
                        context,
                        context.getString(R.string.chat_textview_menu_copy)
                                + context
                                .getString(R.string.chat_textview_menu_transpond)
                                + context
                                .getString(R.string.chat_textview_menu_delete))
                        + 2 * textPadding;
                break;
            default:
                break;
        }

        if (contentView != null) {
            contentView.findViewById(R.id.chat_delete).setOnClickListener(
                    popMenuListener);
        }
        pop = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, false);
        int marginPos = popupWindowWidth / 2
                - ToolsUtils.dip2px(context, 30);
        System.out.println("popupWindowWidth=" + popupWindowWidth);
        pop.setContentView(contentView);
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.setOutsideTouchable(true);
        int width = popupWindowWidth - clickView.getMeasuredWidth();
        int height = clickView.getMeasuredHeight()
                + ToolsUtils.dip2px(context, popupWindowHeight);
        if (bean.getDirect() == MsgDirectionEnum.In) {
            pop.showAsDropDown(clickView, -width / 2, -height);
        } else {
            pop.showAsDropDown(clickView, -width / 2 - marginPos,
                    -height);
        }
    }
    class PopMenuListener implements View.OnClickListener {
        private MessageBean bean;
        private int position;

        public PopMenuListener(MessageBean bean) {
            this.bean = bean;
        }

        @Override
        public void onClick(View v) {
            if(activity==null)
                return;
            int i = v.getId();
            if (i == R.id.chat_copy) {
                activity.copy(bean);

            } else if (i == R.id.chat_delete) {
                activity.delete(bean);

            } else if (i == R.id.chat_transpond) {
                activity.transpond(bean);

            } else if (i == R.id.chat_copyphoto) {
                activity.copyPhoto(bean);

            } else if (i == R.id.chat_modeofearphone) {
                boolean voiceIsEarMode = ChatUtil.getInstance().getVoiceIsEarMode();
                ChatUtil.getInstance().setVoiceToEarMode(!voiceIsEarMode);
                activity.setEarPhoneMode(!voiceIsEarMode);

            } else if (i == R.id.chat_saveforalbum) {
                ChatUtil.savePhoto(IMClient.getInstance().getContext(), bean);

            } else {
            }
            if (null != pop) {
                pop.dismiss();
                pop.setFocusable(false);
            }
        }
    }
}
