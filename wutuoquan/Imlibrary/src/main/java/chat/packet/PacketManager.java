package chat.packet;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.imlibrary.R;

/**
 * Description: 红包管理类
 * Created  by: weyko on 2016/5/24.
 */
public class PacketManager {
    private static PacketManager instance;
    private Dialog packetDialog;
    private FragmentActivity activity;
    private View rootView;
    private AnimatorSet setPacket;
    private View.OnClickListener onCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (packetDialog != null) {
                packetDialog.dismiss();
                stop();
            }
        }
    };

    public PacketManager() {

    }

    public static PacketManager getInstance() {
        if (instance == null)
            instance = new PacketManager();
        return instance;
    }

    /**
     * 弹出红包对话框
     *
     * @param activity
     */
    public void showPacketDialog(FragmentActivity activity) {
        this.activity = activity;
        if (packetDialog == null) {
            packetDialog = new Dialog(activity, R.style.Theme_Light_FullScreenDialogAct);
            rootView = LayoutInflater.from(activity).inflate(R.layout.layout_packet, null);
            rootView.findViewById(R.id.close_packet).setOnClickListener(onCloseListener);
            packetDialog.setContentView(rootView);
            packetDialog.setCanceledOnTouchOutside(false);
        }
        animation(rootView);
        packetDialog.show();
    }

    /**
     * 开启动画
     *
     * @param view
     */
    private void animation(View view) {
        if (setPacket == null) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.08f, 1.0f, 1.08f, 1.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.08f, 1.0f, 1.08f, 1.0f);
            setPacket = new AnimatorSet();
            setPacket.playTogether(animator1, animator2);
            setPacket.setInterpolator(new LinearInterpolator());
            setPacket.setDuration(800);
        }
        setPacket.start();
    }

    /**
     * 停止动画
     */
    public void stop() {
        if (setPacket != null) {
            setPacket.end();
        }
    }
    /**
     * 销毁资源
     */
    public void onDestory() {
        if (packetDialog != null) {
            packetDialog.dismiss();
            packetDialog = null;
        }
        if (setPacket != null) {
            setPacket.cancel();
            setPacket = null;
        }
    }
}
