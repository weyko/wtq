package chat.manager;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.imlibrary.R;

import java.util.HashMap;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.user.UserInfoHelp;
import chat.dialog.CustomBaseDialog;
import chat.login.LoginEntity;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * Description:聊天基础管理类
 */
public class ChatBaseManager {
    private static ChatBaseManager instance;
    private Handler tembHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long tembId = (long) msg.obj;
            ChatLoadManager.getInstance().loadTembMsg(tembId, tembHandler);
        }
    };
    /**
     * 异地登陆，强制下线
     */
    private CustomBaseDialog dialogConflict;
    private WBaseModel<LoginEntity> loginModel;

    public static ChatBaseManager getInstance() {
        if (instance == null)
            instance = new ChatBaseManager();
        return instance;
    }

    /**
     * 加载临时消息，并重发(处理异常消息)
     */
    public void loadTembMsgs() {
        ChatLoadManager.getInstance().loadTembMsg(-1L, tembHandler);
    }

    /**
     * 下线处理
     */
    public void loginConflict() {
        if (dialogConflict == null) {
            final BaseActivity lastActivity = IMClient.getInstance().getLastActivity();
            if (lastActivity == null)
                return;
            dialogConflict = CustomBaseDialog.getDialog(lastActivity, lastActivity.getString(R.string.conflict_login_error), lastActivity.getString(R.string.conflict_login_in),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialogConflict != null)
                                dialogConflict.dismiss();
                            logoutClientid(lastActivity, false);
                        }
                    }, lastActivity.getString(R.string.setting_sureexit),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialogConflict != null)
                                dialogConflict.dismiss();
                            logoutClientid(lastActivity, true);
                        }
                    });
            dialogConflict.setButton1Background(R.drawable.bg_button_dialog_1);
            dialogConflict.setButton2Background(R.drawable.bg_button_dialog_2);
            dialogConflict.setEnable(false);
        }
        dialogConflict.show();
    }

    /**
     * 登出逻辑处理
     */
    private void logoutClientid(BaseActivity lastActivity, final boolean isNeedLogin) {
        dialogConflict = null;
        if (isNeedLogin) {
//            Intent intent=new Intent(this,LoginActivity.class);
//            intent.putExtra("isLoginOut", true);
//            startActivity(intent);
        } else {
            Login(lastActivity);
        }
    }

    /**
     * 后台登录，不需要登录界面
     *
     * @param lastActivity
     */
    private void Login(final BaseActivity lastActivity) {
        if (lastActivity == null)
            return;
        lastActivity.showLoading();
        if (loginModel == null) {
            loginModel = new WBaseModel<LoginEntity>(lastActivity, LoginEntity.class);
        }
        HashMap<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("useraccount", UserInfoHelp.getInstance().getAccount());
        parameter.put("userpass", UserInfoHelp.getInstance().getPassWord());
        parameter.put("loginAppType", Constant.LOGINAPPTYPE);
        loginModel.httpJsonRequest(Request.Method.POST, URLConfig.LOGIN_URL, parameter, new WRequestCallBack() {
            @Override
            public void receive(int httpStatusCode, Object data) {
                lastActivity.dissmisLoading();
                if (data != null && data instanceof LoginEntity) {
                    LoginEntity info = (LoginEntity) data;
                    if (info.isResult()) {
                        IMClient.getInstance().saveSSOLoginInfo(info.getData().getUserId(), info.getData().getToken());
                        // 连接聊天服务
                        XmppServerManager.getInstance(XmppSessionManager.getInstance())
                                .startConnectService();
                    } else {
                        IMClient.getInstance().finishActivity();
                    }
                } else {
                    IMClient.getInstance().finishActivity();
                }
            }
        });
    }

    /**
     * 释放资源
     */
    public void release() {
        if (dialogConflict != null) {
            dialogConflict.dismiss();
            dialogConflict = null;
        }
        if(loginModel!=null){
            loginModel.cancelRequest();
            loginModel=null;
        }
        if(instance!=null) instance=null;
    }
}
