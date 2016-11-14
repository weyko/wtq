package net.skjr.wtq.ui.activity.system;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.AccountBiz;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.common.Event;
import net.skjr.wtq.common.utils.ImageLoaderUtils;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.system.UploadImgObj;
import net.skjr.wtq.model.system.UserInfoObj;
import net.skjr.wtq.ui.activity.BaseToolbarActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 19:29
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class UserInfoActivity extends BaseToolbarActivity {

    private AccountBiz mBiz;
    private ImageView mInfo_img;
    private ImageView mInfo_code_card;
    private TextView mInfo_nickname;
    private TextView mInfo_address;
    private TextView mInfo_sex;
    private TextView mInfo_area;
    private TextView mInfo_mood;

    private Uri imageUri;
    private String file_name;
    private static final String IMAGE_FILE_NAME = "avatar_middle.jpg";
    private ProjectListBiz mListBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initToolbar("个人资料");
        initVariables();
        initView();
        initData();
    }

    private void initData() {
        Subscription s = mBiz.getUserInfo()
                .subscribe(new Action1<APIResult<UserInfoObj>>() {
                    @Override
                    public void call(APIResult<UserInfoObj> apiResult) {
                        ongetUserInfoComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void ongetUserInfoComplete(APIResult<UserInfoObj> apiResult) {
        if(apiResult.isSuccess) {
            UserInfoObj result = apiResult.result;
            ImageLoaderUtils.displayImage(this, result.userImg, mInfo_img);
            mInfo_nickname.setText(result.userNickname);
            mInfo_address.setText(result.cityName);
            mInfo_sex.setText(result.userSex);
            mInfo_area.setText(result.cityName);
            mInfo_mood.setText(result.userMood);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initView() {
        mInfo_img = (ImageView) findViewById(R.id.info_img);
        mInfo_code_card = (ImageView) findViewById(R.id.info_code_card);
        mInfo_nickname = (TextView) findViewById(R.id.info_nickname);
        mInfo_address = (TextView) findViewById(R.id.info_address);
        mInfo_sex = (TextView) findViewById(R.id.info_sex);
        mInfo_area = (TextView) findViewById(R.id.info_area);
        mInfo_mood = (TextView) findViewById(R.id.info_mood);
    }

    private void initVariables() {
        mBiz = new AccountBiz();
        mListBiz = new ProjectListBiz();
    }

    /**
     * 上传头像
     * @param view
     */
    public void uploadHeadImg(View view) {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {

            switch (requestCode) {
                case 1:
                    startPhotoZoom(data.getData());
                    break;
                case 2:
                    if (data != null) {
                        getImageToView();
                        uploadHeadImg();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadHeadImg(){
        //TODO:上传图片

        uploadImgToServer();
        showProgressDialog("  上传中  ");
    }

    /**
     * 上传封面图片
     */
    private void uploadImgToServer() {
        File[] files = new File[1];
        files[0] = new File(file_name);
        Subscription s = mListBiz.uploadHeadImg(files)
                .subscribe(new Action1<APIResult<UploadImgObj>>() {
                    @Override
                    public void call(APIResult<UploadImgObj> apiResult) {
                        onUploadComplete(apiResult);
                    }
                });
        addSubscription(s);
    }

    private void onUploadComplete(APIResult<UploadImgObj> apiResult) {
        if(apiResult.isSuccess) {
            dismissProgressDialog();
            EventBus.getDefault().post(new Event.RefreshTabMine());
        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 保存裁剪之后的图片数据
     */
    @SuppressWarnings("deprecation")
    private void getImageToView() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showToast("读取图片文件失败:(");
            return;
        }
        Drawable drawable = new BitmapDrawable(bitmap);
        mInfo_img.setImageDrawable(drawable);
    }
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        if (uri == null) {
            Log.i("net.skjr.kensasset", "The uri is not exist.");
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(this, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 360);
        intent.putExtra("outputY", 360);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 2);
    }
    protected Uri getTempUri() {
        imageUri = Uri.fromFile(getTempFile());
        return imageUri;
    }

    protected File getTempFile() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String imgpath = UUID.randomUUID() + "_" + IMAGE_FILE_NAME;
            File f = new File(Environment.getExternalStorageDirectory(),
                    imgpath);
            file_name = f.getAbsolutePath();
            try {
                f.createNewFile();
            } catch (IOException e) {
                showToast("SD临时文件读取错误:(");
            }
            return f;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
