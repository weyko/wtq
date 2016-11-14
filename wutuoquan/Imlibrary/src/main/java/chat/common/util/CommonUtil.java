package chat.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.imlibrary.R;

import java.io.UnsupportedEncodingException;

import chat.common.util.output.ShowUtil;
import chat.session.util.ChatFaceUtils;
import chat.view.PasteEditText;

/**
 * Description: 评论工具类
 * Created  by: weyko on 2016/5/18.
 */
public class CommonUtil implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static CommonUtil instance;
    private PopupWindow replyPopWindow = null;
    private PasteEditText reply_et;
    private CheckBox reply_expression_cb;
    private Button reply_bt;
    private View expression_container_view;
    private ChatFaceUtils commentFaceUtils;
    private InputMethodManager inputManager;
    private Context context;
    private OnCommentClickListener onCommentClickListener;

    public CommonUtil(Context context) {
        this.context = context;
        initReplayPopupWindow(context);
    }

    public static CommonUtil getInstance(Context context) {
        if (instance == null)
            instance = new CommonUtil(context);
        return instance;
    }

    /**
     * 弹出评论窗口
     *
     * @param view
     */
    public void openCommentDialog(View view, OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
        if (replyPopWindow == null) {
            initReplayPopupWindow(context);
        }
        if (!replyPopWindow.isShowing()) {
            replyPopWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
        reply_et.setHint(R.string.say_something);
        openKeyboard(new Handler(), 0);
//         DynamicData dynamicData = (DynamicData) view.getTag();
        reply_expression_cb.setChecked(false);
//         reply_bt.setTag(dynamicData);
    }

    /**
     * 初始化评论弹窗
     */
    private void initReplayPopupWindow(final Context context) {
        if (replyPopWindow == null) {
            inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            View replyView = LayoutInflater.from(context).inflate(R.layout.item_dynamic_reply, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            replyView.setLayoutParams(params);

            reply_et = (PasteEditText) replyView.findViewById(R.id.reply_et);
            reply_expression_cb = (CheckBox) replyView.findViewById(R.id.reply_expression_cb);
            reply_bt = (Button) replyView.findViewById(R.id.reply_send_bt);
            expression_container_view = replyView.findViewById(R.id.expression_container);
            ViewPager expression_vp = (ViewPager) replyView.findViewById(R.id.view_pager);
            commentFaceUtils = new ChatFaceUtils(context, replyView, reply_et, expression_vp);
            commentFaceUtils.showEmojiPage();
            replyPopWindow = new PopupWindow(replyView, params.width, params.height);
            replyPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            replyPopWindow.setFocusable(true);
            replyPopWindow.setClippingEnabled(true);
            replyPopWindow.setTouchable(true);
            replyPopWindow.setOutsideTouchable(true);
            replyPopWindow.setBackgroundDrawable(new ColorDrawable(0));

            reply_bt.setOnClickListener(this);
            reply_expression_cb.setOnCheckedChangeListener(this);
            reply_et.setOnClickListener(this);
            reply_et.setTextIsSelectable(true);
            reply_et.setFocusable(true);
            reply_et.setFocusableInTouchMode(true);
            reply_et.setSelectAllOnFocus(true);
            reply_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int length = 0;
                    try {
                        length = s.toString().getBytes("GBK").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (length > 200) {
                        Toast.makeText(context, context.getString(R.string.strings_length_too_long), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }
    }

    /**
     * 打开软键盘
     *
     * @param mHandler
     * @param s
     */
    private void openKeyboard(Handler mHandler, int s) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, s);
    }

    @Override
    public void onClick(View v) {
        if (v == reply_bt) {
            if (onCommentClickListener != null) {
                onCommentClickListener.onCommentClick(v);
            }
        } else if (v == reply_et) {
            reply_expression_cb.setChecked(false);
            ShowUtil.showSoftWindow(context, reply_et);
        }
    }

    /**
     * 重置
     */
    public void reset() {
        if (reply_expression_cb == null)
            return;
        reply_expression_cb.setChecked(false);
        expression_container_view.setVisibility(View.GONE);
        reply_et.setText("");
        ShowUtil.hideSoftWindow(context,reply_et);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.reply_expression_cb) { // 回复框中表情按钮点击事件
            if (isChecked) {
                expression_container_view.setVisibility(View.VISIBLE);
                inputManager.hideSoftInputFromWindow(buttonView.getWindowToken(), 0);
            } else {
                expression_container_view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (replyPopWindow != null) {
            replyPopWindow.dismiss();
            replyPopWindow = null;
        }
        if (commentFaceUtils != null) {
            commentFaceUtils.clearCache();
            commentFaceUtils = null;
        }
        instance = null;
    }

    /**
     * 评论发送按钮点击监听器
     */
    public interface OnCommentClickListener {
        public void onCommentClick(View view);
    }
    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    public static boolean checkAppIsInstall(Context context, String packageName) {
        PackageManager pManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (packageInfo != null) ? true : false;
    }
}
