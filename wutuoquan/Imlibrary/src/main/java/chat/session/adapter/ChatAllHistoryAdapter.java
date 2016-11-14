package chat.session.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.List;

import chat.base.IMClient;
import chat.common.util.TextUtils;
import chat.common.util.ToolsUtils;
import chat.common.util.time.DateUtils;
import chat.image.DisplayImageConfig;
import chat.session.activity.NewFriendsActivity;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.emoji.EmojiManager;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.view.CircleImageView;

/**
 * 消息列表适配器
 */
public class ChatAllHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<MessageBean> list;
    private LayoutInflater inflater;
    private OnClickListener listener;
    private Resources resources;
    private int padding = 4;
    private String mobizTile;

    public ChatAllHistoryAdapter(Context context, List<MessageBean> list) {
        super();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        resources = context.getResources();
        padding = ToolsUtils.dip2px(context, 4);
        mobizTile = context.getString(R.string.chat_list_group_title);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public MessageBean getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHolder holder = null;
        final MessageBean sessionBean = list.get(position);
        final boolean isGroup = sessionBean.getSessionType() == SessionTypeEnum.GROUPCHAT;
        listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = sessionBean.getChatWith();
                if (isGroup) {
                    ChatUtil.gotoChatRoom(IMClient.getInstance()
                            .getLastActivity(), sessionBean);
                    return;
                }
                if (id == null)
                    return;
                if (sessionBean.getSessionId().equals(
                        String.valueOf(IMTypeUtil.SessionType.FRIEND.name()))) {// 新朋友
                    Intent intent = new Intent(context,
                            NewFriendsActivity.class);
                    context.startActivity(intent);
                    return;
                }
                if (!sessionBean.getSessionId().contains("@")) {
                    ChatUtil.gotoChatRoom(IMClient.getInstance()
                            .getLastActivity(), sessionBean.getImUserBean());
                    return;
                }
            }
        };
        if (convertView == null) {
            holder = new MyHolder();
            convertView = inflater.inflate(R.layout.item_chat_history, null);
            holder.newsNum = (TextView) convertView
                    .findViewById(R.id.msg_unread_number);
            holder.avatar = (CircleImageView) convertView
                    .findViewById(R.id.msg_avatar);
            holder.time = (TextView) convertView.findViewById(R.id.msg_time);
            holder.nickName = (TextView) convertView
                    .findViewById(R.id.msg_name);
            holder.isTop = (ImageView) convertView.findViewById(R.id.msg_top);
            holder.message = (TextView) convertView
                    .findViewById(R.id.msg_message);
            holder.disturb = (ImageView) convertView
                    .findViewById(R.id.msg_bottom);
            holder.state = (ImageView) convertView.findViewById(R.id.msg_state);
            holder.msg_empty_number = (ImageView) convertView
                    .findViewById(R.id.msg_empty_number);

            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }
        final ImageView avatar = holder.avatar;
        avatar.setOnClickListener(listener);
        holder.disturb.setVisibility(sessionBean.getIsForbid() == 1 ? View.VISIBLE
                : View.INVISIBLE);
        holder.isTop.setVisibility(list.get(position).getIsTop() == 1 ? View.VISIBLE
                : View.INVISIBLE);
        long time = sessionBean.getMsgTime();
        if (String.valueOf(time).length() <= 10) {// 兼容旧版本
            time *= 1000;
        }
        holder.time.setText(DateUtils.getTimesTampString(context, time));
        String nickname = "";
        if (isGroup) {
            nickname = sessionBean.getChatGroupBean().getData().getGroupName();
        } else {
            ImUserBean imUserBean = sessionBean.getImUserBean();
            if (imUserBean != null) {
                if (android.text.TextUtils.isEmpty(imUserBean
                        .getRemark()))
                    nickname = imUserBean.getName();
                else {
                    nickname = imUserBean.getRemark();
                }
            }
        }
        if (isGroup
                && android.text.TextUtils.isEmpty(nickname)) {// 未命名群
            nickname = resources
                    .getString(R.string.chat_group_create_name_empty);
        }
        String msg = sessionBean.getSession();
        MsgDirectionEnum dir = sessionBean.getDirect();
        SessionTypeEnum sessionType = sessionBean.getSessionType();
        if (sessionType == SessionTypeEnum.S) {
            ImUserBean imUserBean = sessionBean.getImUserBean();
            msg = imUserBean.getName() + resources.getString(R.string.chat_group_invite_item_head) + "\""
                    + sessionBean.getSession() + "\"";
            msg = TextUtils.getStringFormat(context,
                    R.string.chat_body_link, msg);
            holder.message.setText(msg);
        } else {
            EmojiManager.getInstance().getSmileText(context, holder.message, msg, ImageSpan.ALIGN_BOTTOM, 0.55f);
        }
        setUserHeader(holder, sessionBean);
        Drawable d = null;
        holder.state.setVisibility(View.GONE);
        if (dir == MsgDirectionEnum.Out) {// 判断是接受的还是发生的消息,0:发送，1：接受
            switch (sessionBean.getMsgStatus()) {
                case sending:
                    d = context.getResources().getDrawable(
                            R.drawable.ic_chat_sending);
                    holder.state.setVisibility(View.VISIBLE);
                    break;
                case success:
                    holder.state.setVisibility(View.GONE);
                    break;
                default:
                    d = context.getResources().getDrawable(
                            R.drawable.ic_public_warning);
                    holder.state.setVisibility(View.VISIBLE);
                    break;
            }
        }
        holder.nickName.setText(nickname);
        if (d != null) {
            d.setBounds(0, 0, 30, 30);
            holder.state.setImageDrawable(d);
        }
        int num = sessionBean.getUnReads();
        holder.newsNum.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
        if (num > 0) {
            if (num > 99) {
                holder.newsNum.setPadding(padding, padding / 2, padding,
                        padding / 2);
                holder.newsNum.setText("99+");
            } else {
                holder.newsNum.setPadding(0, 0, 0, 0);
                holder.newsNum.setText(String.valueOf(num));
            }
        }
        if (sessionBean.getSessionId().equals(IMTypeUtil.SessionType.MOBIZ.name())) {
            holder.newsNum.setVisibility(View.GONE);
            holder.msg_empty_number.setVisibility(View.VISIBLE);
            if (num <= 0)
                holder.msg_empty_number.setVisibility(View.GONE);
        } else {
            if (num > 0) {
                holder.newsNum.setVisibility(View.VISIBLE);
            } else {
                holder.newsNum.setVisibility(View.GONE);
            }
            holder.msg_empty_number.setVisibility(View.GONE);
        }
        return convertView;

    }

    /**
     * 设置用户头像
     *
     * @param holder
     */
    private void setUserHeader(MyHolder holder, MessageBean bean) {
        String headUrl = bean.getSessionType() == SessionTypeEnum.GROUPCHAT ? bean
                .getChatGroupBean().getData().getPhotoUrl() : bean
                .getImUserBean().getAvatar();
        holder.avatar.setTag(headUrl);
        if (IMTypeUtil.SessionType.FRIEND.name().equals(bean.getSessionId())) {
            holder.avatar.setImageResource(R.drawable.ic_new_friend);
            return;
        }
        if (headUrl != null && !headUrl.equals("null")
                && headUrl.trim().length() > 0) {
            IMClient.sImageLoader.displayThumbnailImage(headUrl,
                    holder.avatar,
                    DisplayImageConfig.userLoginItemImageOptions,
                    DisplayImageConfig.headThumbnailSize,
                    DisplayImageConfig.headThumbnailSize);
        } else {
            if (bean.getSessionType() == SessionTypeEnum.GROUPCHAT) {// 群聊
                holder.avatar.setImageResource(R.drawable.ic_default_group);
            } else {
                if (bean.getImUserBean() != null
                        && bean.getImUserBean().getFansBean() != null) {
                    holder.avatar.setImageResource(R.drawable.default_head);
                } else {
                    holder.avatar.setImageResource(R.drawable.default_head);
                }
            }
        }
    }

    static class MyHolder {
        TextView newsNum;// 内容数量
        TextView time;// 时间
        TextView nickName;// 昵称
        CircleImageView avatar;// 头像
        TextView message;// 内容
        ImageView isTop;// 置顶
        ImageView disturb;// 免打扰
        ImageView state;
        ImageView msg_empty_number;
    }
}