package chat.session.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.common.util.time.DateUtils;
import chat.image.ImageUploadEntity;
import chat.session.adapter.common.ImAdapter;
import chat.session.adapter.common.ImAdapterDelegate;
import chat.session.bean.MessageBean;
import chat.session.util.ListViewUtil;
import chat.session.viewholder.MsgViewHolderBase;

public class MsgAdapter extends ImAdapter<MessageBean> {

    private ViewHolderEventListener eventListener;
    private Map<String, Float> progresses; // 有文件传输，需要显示进度条的消息ID map
    private HashMap<String, ImageUploadEntity> uploadList;
    public MsgAdapter(Context context, List<MessageBean> items, ImAdapterDelegate delegate) {
        super(context, items, delegate);
        progresses = new HashMap<>();
        uploadList=new HashMap<String, ImageUploadEntity>();
    }

    public ViewHolderEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(ViewHolderEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void deleteItem(MessageBean message) {
        if (message == null) {
            return;
        }

        int index = 0;
        for (MessageBean item : getItems()) {
            if (item.getSessionId().equals(message.getSessionId())) {
                break;
            }
            ++index;
        }

        if (index < getCount()) {
            getItems().remove(index);
            notifyDataSetChanged();
        }
    }

    public float getProgress(MessageBean message) {
        Float progress = progresses.get(message.getSessionId());
        return progress == null ? 0 : progress;
    }

    public void putProgress(MessageBean message, float progress) {
        progresses.put(message.getSessionId(), progress);
    }
    public void addUpload(String url, ImageUploadEntity uploadEntity) {
        if (uploadList != null && !uploadList.containsKey(url)) {
            uploadList.put(url, uploadEntity);
            notifyDataSetChanged();
        }
    }
    public HashMap<String, ImageUploadEntity> getUploadList(){
        return uploadList;
    }
    public void removeUpload(String url) {
        if (uploadList != null && uploadList.containsKey(url)) {
            uploadList.remove(url);
        }
    }
    /***********************
     * 时间显示处理
     ****************************/


    public boolean needShowTime(int position, MessageBean message) {
        if (hideTimeAlways(message))
            return false;
        /** 显示时间 */
        if (position == 0) {
            return true;
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (!DateUtils.isCloseEnough(message.getMsgTime(),
                    getItem(position - 1).getMsgTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean hideTimeAlways(MessageBean message) {
        switch (message.getMsgType()) {
            case notification:
                return true;
            case tip:
                return true;
            default:
                return false;
        }
    }

    public interface ViewHolderEventListener {
        // 长按事件响应处理
        boolean onViewHolderLongClick(View clickView, View viewHolderView, MessageBean item);

        // 发送失败或者多媒体文件下载失败指示按钮点击响应处理
        void onFailedBtnClick(MessageBean resendMessage);
    }
    public void refreshCurrentItem(ListView messageListView,int index){
        Object tag = ListViewUtil.getViewHolderByIndex(messageListView, index);
        if (tag instanceof MsgViewHolderBase) {
            MsgViewHolderBase viewHolder = (MsgViewHolderBase) tag;
            viewHolder.refreshCurrentItem(index);
        }
    }
}
