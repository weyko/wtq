package net.skjr.wtq.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.sys.DeviceUuidFactory;
import net.skjr.wtq.core.utils.sys.SdCardUtils;
import net.skjr.wtq.model.VersionInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


/**
 * 一些无法归类的，常规的帮助方法
 */
public class CommonUtils {
    /**
     * 获取设备ID
     *
     * @return
     */
    public String getDeviceId(Context context) {
        DeviceUuidFactory factory = new DeviceUuidFactory(context);
        UUID uuid = factory.getDeviceUuid();
        return uuid.toString();
    }

    /**
     * 获取当前应用的根目录(注意：不带“/”)
     * /sdcard/hszt
     * /cache/hszt
     * 如果存在，那么直接返回；如果不存在，那么创建后返回
     *
     * @param context
     * @return 目录保存的目录
     */
    public static String getAppBasePath(Context context) {
        String filePath = null;
        if (SdCardUtils.isCanUseSdCard()) {
            filePath = Environment.getExternalStorageDirectory() + File.separator + Consts.APP_ENAME;
        } else {
            filePath = context.getCacheDir().getAbsolutePath() + File.separator + Consts.APP_ENAME;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
            L.d("file", "目录不存在,创建目录:" + filePath);
        }

        return filePath;
    }

    /**
     * 获取当前版本号VersionCode
     *
     * @return
     */
    public static VersionInfo getVersionCode(Context context) {
        int verCode = -1;
        String verName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(Consts.PACKAGE_NAME, 0);

            verCode = packageInfo.versionCode;
            verName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            L.e(e.getLocalizedMessage());
        }

        VersionInfo info = new VersionInfo();
        info.code = verCode;
        info.name = verName;

        return info;
    }

    /**
     * 返回当前的时间字符串
     * @return
     */
    public static String getCurrentTimeString(){
        Calendar cal=Calendar.getInstance();
        Date date=cal.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(date);

        return dateString;
    }

    /**
     * 获取保存到本地的图标
     * @param context
     * @return
     */
    public static String getAppIconPath(Context context){
        String basePath = CommonUtils.getAppBasePath(context) + "/";

        return basePath + "app.png";
    }

    /**
     * 显示对话框-信息、底部一个按钮
     * @param context
     * @param onClickListener
     */
    public static void showDialog(Context context, String message, String okmessage, final View.OnClickListener onClickListener) {
        final AppCompatDialog dialog = new AppCompatDialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_message);
        TextView title = (TextView)dialog.getWindow().findViewById(R.id.message);
        title.setText(message);

        Button ok = (Button) dialog.getWindow().findViewById(R.id.ok);
        ok.setText(okmessage);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示对话框-信息，底部取消、确定按钮
     * @param context
     * @param message
     * @param okListener
     * @param cancelListener
     */
    public static void showDialog(Context context, String message, final View.OnClickListener okListener, final View.OnClickListener cancelListener,
                                  String cancelText, String okText){
        final AppCompatDialog dialog = new AppCompatDialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_choice);
        TextView title = (TextView) dialog.getWindow().findViewById(R.id.message);
        title.setText(message);
        TextView cancel = (TextView) dialog.getWindow().findViewById(R.id.cancel);
        TextView ok = (TextView) dialog.getWindow().findViewById(R.id.ok);
        cancel.setText(cancelText);
        ok.setText(okText);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null)
                    cancelListener.onClick(v);

                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(okListener != null)
                    okListener.onClick(v);

                dialog.dismiss();
            }
        });
    }

    /**
     * 显示更新对话框-信息，底部取消、确定按钮
     * @param context
     * @param message
     * @param okListener
     * @param cancelListener
     */
    public static void showUpdateDialog(Context context, String message, final View.OnClickListener okListener, final View.OnClickListener cancelListener){
        final AppCompatDialog dialog = new AppCompatDialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_update);
        TextView title = (TextView) dialog.getWindow().findViewById(R.id.message);
        title.setText(message);

        dialog.getWindow().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null)
                    cancelListener.onClick(v);

                dialog.dismiss();
            }
        });

        dialog.getWindow().findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(okListener != null)
                    okListener.onClick(v);

                dialog.dismiss();
            }
        });
    }

}
