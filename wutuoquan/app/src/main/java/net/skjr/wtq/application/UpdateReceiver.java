package net.skjr.wtq.application;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import net.skjr.wtq.common.Event;
import net.skjr.wtq.core.utils.L;

import org.greenrobot.eventbus.EventBus;

/**
 * 接收更新下载完成通知
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        SharedPreferences sPreferences = context.getSharedPreferences("downloadcomplete", 0);
        long refernece = sPreferences.getLong("refernece", 0);
        if (refernece == myDwonloadID) {
            String serviceString = Context.DOWNLOAD_SERVICE;
            DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
            Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);

            //6.0以上不一定能跑
//            Intent install = new Intent(Intent.ACTION_VIEW);
//            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//            install.setData(downloadFileUri);
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(install);
            } catch (Exception ex) {
                L.e(ex, ex.getMessage());

                //cm 6.0上失败，转成文件再安装一次
                EventBus.getDefault().post(new Event.DownloadApkCompleteEvent(downloadFileUri));
            }

        }
    }


}
