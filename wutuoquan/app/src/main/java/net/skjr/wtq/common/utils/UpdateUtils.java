package net.skjr.wtq.common.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import net.skjr.wtq.R;
import net.skjr.wtq.application.MyPreference;
import net.skjr.wtq.common.Consts;
import net.skjr.wtq.core.utils.L;
import net.skjr.wtq.core.utils.ui.ToastUtils;
import net.skjr.wtq.model.SystemInfo;
import net.skjr.wtq.model.VersionInfo;


/**
 * 更新帮助类
 */
public class UpdateUtils {
    /**
     * 是否有新版本
     *
     * @return
     */
    public static boolean hasNewVersion(Activity activity) {
        boolean result = false;

        VersionInfo versionInfo = CommonUtils.getVersionCode(activity);

        SystemInfo systemInfo = MyPreference.getSystemInfo();
        if (systemInfo != null) {
            int newVersion = 0;
            try {
                newVersion = Integer.parseInt(systemInfo.versionCode);
            } catch (Exception ex) {
                L.e(ex, ex.getMessage());
            }

            int currentVersion = versionInfo.code;
            if (newVersion > currentVersion) {
                result = true;
            }
        }

        return result;
    }

    /**
     * 下载更新
     *
     * @param activity
     */
    public static void download(Activity activity) {
        SystemInfo systemInfo = MyPreference.getSystemInfo();
        if (systemInfo == null)
            return;

        String dowloadPath = systemInfo.url;
        if (TextUtils.isEmpty(dowloadPath)) {
            return;
        }

        ToastUtils.show(activity, "开始下载");

        //test
        //dowloadPath = "http://pkg3.fir.im/52b85d8c49e8e8ae014a9e8baf78097309985359.apk?attname=app-release.apk_1.0.0.apk";

        DownloadManager dManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dowloadPath);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Consts.APP_ENAME + ".apk");
        request.setDescription(R.string.app_name + "新版本下载");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);

        long refernece = dManager.enqueue(request);
        // 把当前下载的ID保存起来
        SharedPreferences sPreferences = activity.getSharedPreferences("downloadcomplete", 0);
        sPreferences.edit().putLong("refernece", refernece).commit();
    }
}
