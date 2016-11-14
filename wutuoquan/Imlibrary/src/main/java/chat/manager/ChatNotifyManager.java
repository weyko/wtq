package chat.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.imlibrary.R;
import com.nostra13.universalimageloader.utils.L;

import java.util.UUID;

import chat.base.IMClient;
import chat.session.enums.MsgTypeEnum;

/**
 * description: 通知消息管理类
 */
public class ChatNotifyManager {
    private static final int NOTIFY_NEWMSG = 100011;
    private static ChatNotifyManager instance;
    private ChatNotifyRunnable notifyRunnable;

    public ChatNotifyManager() {
    }

    public static ChatNotifyManager getInstance() {
        if (instance == null)
            instance = new ChatNotifyManager();
        return instance;
    }

    /**
     * 清空状态栏
     */
    public void clearNotify() {
        if (notifyRunnable != null) {
            notifyRunnable.clearNotify();
        }
    }

    public class ChatNotifyRunnable implements Runnable {
        private NotificationManager manager;
        private Notification notification;
        private String notifyMsg;
        private Context context;

        public ChatNotifyRunnable(Context context) {
            this.context = context;
        }

        /**
         * 设置通知栏文本
         *
         * @param notifyMsg
         */
        public void setNotifyMsg(String notifyMsg) {
            this.notifyMsg = notifyMsg;
        }

        @Override
        public void run() {
            sendNoifyMessage(notifyMsg);
        }

        /**
         * 清空状态栏
         */
        public void clearNotify() {
            if (manager != null) {
                manager.cancel(NOTIFY_NEWMSG);
            }
        }

        public void release() {
            if (manager != null) {
                manager = null;
            }
            if (notification != null) {
                notification = null;
            }
        }

        /**
         * 发送通知栏消息
         *
         * @param notifyMsg
         */
        @SuppressWarnings("deprecation")
        private void sendNoifyMessage(String notifyMsg) {
            if (manager == null) {
                manager = (NotificationManager) IMClient.getInstance().getContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String title= context.getString(R.string.notification_new_message);
            Intent intent = goMoTalkActivity(IMClient.getInstance().getContext(), MsgTypeEnum.text.getValue());
            int chatIcon=R.drawable.ic_launcher;
            send(context,intent,chatIcon,title,notifyMsg,-1,NOTIFY_NEWMSG);
        }

        /**
         * @return void
         * @Title: goMoXinActivity
         * @param:
         * @Description: TODO(这里用一句话描述这个方法的作用)
         */
        private Intent goMoTalkActivity(Context mContext, int messageType) {
            Intent mIntent = null;
//            if (HomeActivity.running) {
//                mIntent = new Intent(mContext, HomeActivity.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mIntent.putExtra("runningbackground", true);
//                mIntent.setAction(Long.toString(System.currentTimeMillis()));
//            } else {
//                mIntent = new Intent(mContext, StartPageActivity.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Bundle mBundle = new Bundle();
//                mBundle.putInt("messageType", messageType);
//                mBundle.putBoolean("chatpage", true);
//                mIntent.putExtras(mBundle);
//            }
            return mIntent;
        }
        /**
         * 优选版本通知发送
         * @param context
         * @param intent
         * @param icon
         * @param title 设置通知栏标题
         * @param text
         * @param number 设置通知集合的数量
         * @param notification_flag
         */
        private void send(Context context , Intent intent, int icon, String title, String text , int number, int notification_flag){
            int sdkInt = Build.VERSION.SDK_INT;
            L.i("notification", "sdkInt:" + sdkInt);
            if (sdkInt >= 16){
                PendingIntent pendingIntent16 = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(),
                        intent , 0);
                // 通过Notification.Builder来创建通知，注意API Level
                // API16之后才支持
                Notification notify16 = new Notification.Builder(context)
                        .setSmallIcon(icon)
                        .setTicker(text)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(Notification.PRIORITY_MAX) //设置该通知优先级
                        .setContentIntent(pendingIntent16)
                        .setNumber(number)
                        .build(); // 需要注意build()是在API
                // level16及之后增加的，API11可以使用getNotificatin()来替代
                notify16.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                manager.notify(notification_flag, notify16);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示

            }else if (sdkInt < 16 && sdkInt >= 11){
                PendingIntent pendingIntent11 = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(),
                        intent, 0);
                // 通过Notification.Builder来创建通知，注意API Level
                // API11之后才支持
                Notification notify11 = new Notification.Builder(context)
                        .setSmallIcon(icon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                        // icon)
                        .setTicker(text)// 设置在status
                        // bar上显示的提示文字
                        .setContentTitle(title)// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                        .setContentText(text)// TextView中显示的详细内容
                        .setContentIntent(pendingIntent11) // 关联PendingIntent
                        .setNumber(number) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                        .getNotification(); // 需要注意build()是在API level
                // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                notify11.flags |= Notification.FLAG_AUTO_CANCEL;
                manager.notify(notification_flag, notify11);
            }
//        else {//已废弃，不过兼容所有版本的通知
//            // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
//
//            // 创建一个PendingIntent，和Intent类似，不同的是由于不是马上调用，需要在下拉状态条出发的activity，所以采用的是PendingIntent,即点击Notification跳转启动到哪个Activity
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(),
//                    intent, 0);
//            // 下面需兼容Android 2.x版本是的处理方式
//            Notification notify1 = new Notification();
//            notify1.icon = icon;
//            notify1.tickerText = text;
//            notify1.when = System.currentTimeMillis();
//            notify1.setLatestEventInfo(context, title,
//                    text, pendingIntent);
//            notify1.number = nunber;
//            notify1.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//            // 通过通知管理器来发起通知。如果id不同，则每click，在statubar那里增加一个提示
//            manager.notify(notification_flag, notify1);
//        }
        }
    }

    /**
     * 处理通知栏消息handler
     */
    private Handler notifyHandler = new Handler(Looper.getMainLooper());

    /**
     * 发送通知
     *
     * @param notifyMsg 通知栏显示文本
     */
    public void sendNotifyForNewMessage(Context context,String notifyMsg) {
        if (notifyRunnable == null) {
            notifyRunnable = new ChatNotifyRunnable(context);
        }
        notifyRunnable.setNotifyMsg(notifyMsg);
        notifyHandler.removeCallbacks(notifyRunnable);
        notifyHandler.postDelayed(notifyRunnable, 500);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (notifyHandler != null) {
            if (notifyRunnable != null) {
                notifyHandler.removeCallbacks(notifyRunnable);
                notifyRunnable.release();
            }
        }
        notifyRunnable = null;
        notifyHandler = null;
        instance = null;
    }
}
