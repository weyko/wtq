package net.skjr.wtq.core.utils.sys;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

/**
 * 权限功能类
 */
public class PermissionsUtils {

    //需要申请的权限，同时必须要在Androidmanifest.xml中声明
    private static String[] PERMISSIONS_GROUP = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 是否需要检查权限
     * 6.0以上才需要
     *
     * @return
     */
    public static boolean isNeedCheckPermission() {
        //低于6.0不检查
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        return true;
    }

    /**
     * 获取未获得授权的权限列表
     *
     * @param activity
     * @return
     */
    public static String[] getDenidPermissions(Activity activity) {
        if (!isNeedCheckPermission())
            return new String[]{};

        // 一个list，用来存放没有被授权的权限
        ArrayList<String> denidArray = new ArrayList<>();

        // 遍历PERMISSIONS_GROUP，将没有被授权的权限存放进denidArray
        for (String permission : PERMISSIONS_GROUP) {
            int grantCode = ActivityCompat.checkSelfPermission(activity, permission);
            if (grantCode == PackageManager.PERMISSION_DENIED) {
                denidArray.add(permission);
            }
        }

        // 将denidArray转化为字符串数组，方便其他地方调用requestPermissions来请求授权
        String[] denidPermissions = denidArray.toArray(new String[denidArray.size()]);

        return denidPermissions;
    }

    /**
     * 是否拥有某个权限
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean hasPermission(Activity activity, String permission) {
        if (!isNeedCheckPermission())
            return true;

        if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        }

        return false;
    }

    //===========================请求权限==============================================

    /**
     * Activity:请求所有需要的权限
     *
     * @param activity
     */
    public static void requestAllPermissions(Activity activity, int requestCode) {
        if (!isNeedCheckPermission())
            return;

        String[] denidPermissions = getDenidPermissions(activity);

        if (denidPermissions.length == 0)
            return;

        requestPermissions(activity, denidPermissions, requestCode);
    }

    /**
     * Activity:请求多个权限
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (!isNeedCheckPermission())
            return;

        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * Activity:请求单个权限
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        String[] permissions = new String[]{permission};
        requestPermissions(activity, permissions, requestCode);
    }

    /**
     * Fragment:请求所有需要的权限
     *
     * @param fragment
     */
    public static void requestAllPermissions(Fragment fragment, int requestCode) {
        if (!isNeedCheckPermission())
            return;

        String[] denidPermissions = getDenidPermissions(fragment.getActivity());

        if (denidPermissions.length == 0)
            return;

        requestPermissions(fragment, denidPermissions, requestCode);
    }

    /**
     * Fragment:请求多个权限
     *
     * @param fragment
     * @param permissions
     * @param requestCode
     */
    public static void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        if (!isNeedCheckPermission())
            return;

        fragment.requestPermissions(permissions, requestCode);
    }

    /**
     * Fragment:请求单个权限
     *
     * @param fragment
     * @param permission
     * @param requestCode
     */
    public static void requestPermission(Fragment fragment, String permission, int requestCode) {
        String[] permissions = new String[]{permission};
        requestPermissions(fragment, permissions, requestCode);
    }

    //==============================================================================================

    /**
     * 弹出框有”不在询问”选项框的时候,返回true（即之前用户已经拒绝过权限）
     * <p/>
     * ***1).应用安装后第一次访问，则直接返回false；
     * ***2).第一次请求权限时，用户Deny了，再次调用shouldShowRequestPermissionRationale()，则返回true；
     * ***3).第二次请求权限时，用户Deny了，并选择了“never ask again”的选项时，再次调用shouldShowRequestPermissionRationale()时，返回false；
     * ***4).设备的系统设置中，禁止了应用获取这个权限的授权，则调用shouldShowRequestPermissionRationale()，返回false。
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        //低于6.0不检查
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        return activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * 在权限申请的Activity|Fragment.onRequestPermissionsResult里调用
     * 判断是否有失败的申请
     *
     * @param grantResults
     * @return
     */
    public static boolean hasFailurePermissionRequest(int[] grantResults) {
        boolean hasFailureRequest = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                hasFailureRequest = true;
                break;
            }
        }

        return hasFailureRequest;
    }

    /**
     * 如果有权限授权失败，那么进入APP权限设置界面
     * 在权限申请的Activity.onRequestPermissionsResult里调用，去App权限设置界面
     * 让用户手工设置
     *
     * @param activity
     */
    public static void showAppSettings(Activity activity) {
        //低于6.0不检查
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        // 进入App设置页面
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    /**
     * 显示对话框，提示用户进入设置界面
     * @param activity
     */
    public static void showPermissionSettingDialog(final Activity activity) {
        //低于6.0不检查
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        String msg = "APP需要您的授权才能正常运行，请点击确定，进入设置界面，然后打开权限项，勾选所需的权限。";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       showAppSettings(activity);
                   }
               })
               .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                   }
               })
               .show();
    }
}
