package net.skjr.wtq.ui.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
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
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import net.skjr.wtq.R;
import net.skjr.wtq.business.ProjectListBiz;
import net.skjr.wtq.databinding.ActivityPublishProjectBinding;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.account.CityListAccount;
import net.skjr.wtq.model.account.DictInfo;
import net.skjr.wtq.model.system.UploadImgObj;
import net.skjr.wtq.ui.adapter.SelectAreaAdapter;
import net.skjr.wtq.viewmodel.CheckResult;
import net.skjr.wtq.viewmodel.PublishProject1ViewModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 创建者     huangbo
 * 创建时间   2016/9/20 15:14
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class PublishProject1Activity extends BaseToolbarActivity implements View.OnClickListener {

    private Uri imageUri;
    private String file_name;
    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "avatar_middle.jpg";
    private ImageView mPublish_img;
    private ActivityPublishProjectBinding mBinding;
    private TextView mPublish_industry;
    private TextView mPublish_area;
    private boolean isAreaPop;
    private ProjectListBiz mBiz;
    private List<DictInfo.ListEntity> mDictInfoList;
    //行业列表
    private List<String> mDictList = new ArrayList<>();
    //城市列表
    private List<String> mProvinceList = new ArrayList<>();
    private List<String> mCityList = new ArrayList<>();

    private List<CityListAccount.ListEntity> mProvincelistEntry;
    private List<CityListAccount.ListEntity> mCitylistEntry;

    private String mProjectArea;
    private String mProjectAreaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initToolbar("发布项目");
        initVariables();
        initView();
        initData();
    }


    private void initData() {
        //获取行业列表
        getDiceList();
        getProvinceList();
    }

    private void initVariables() {
        mBiz = new ProjectListBiz();
    }

    /**
     * 获取行业列表
     */
    private void getDiceList() {
        Subscription s = mBiz.getDictList()
                .subscribe(new Action1<APIResult<DictInfo>>() {
                    @Override
                    public void call(APIResult<DictInfo> accountInfoAPIResult) {
                        onGetDictListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }
    private void onGetDictListComplete(APIResult<DictInfo> apiResult) {
        if(apiResult.isSuccess) {
            DictInfo result = apiResult.result;
            mDictInfoList = result.list;
            for(DictInfo.ListEntity dict : mDictInfoList) {
                mDictList.add(dict.name);
            }

        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 获取省列表
     */
    private void getProvinceList() {
        Subscription s = mBiz.getProvinceList(1)
                .subscribe(new Action1<APIResult<CityListAccount>>() {
                    @Override
                    public void call(APIResult<CityListAccount> accountInfoAPIResult) {
                        onGetProvinceListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetProvinceListComplete(APIResult<CityListAccount> apiResult) {
        if(apiResult.isSuccess) {
            CityListAccount result = apiResult.result;
            mProvincelistEntry = result.list;
            for(CityListAccount.ListEntity li : mProvincelistEntry) {
                mProvinceList.add(li.class_name);
            }

        } else {
            showToast(apiResult.message);
        }
    }

    /**
     * 获取市列表
     */
    private void getCityList(int pro) {
        Subscription s = mBiz.getProvinceList(pro)
                .subscribe(new Action1<APIResult<CityListAccount>>() {
                    @Override
                    public void call(APIResult<CityListAccount> accountInfoAPIResult) {
                        onGetCityListComplete(accountInfoAPIResult);
                    }

                });

        addSubscription(s);
    }

    private void onGetCityListComplete(APIResult<CityListAccount> apiResult) {
        if(apiResult.isSuccess) {
            CityListAccount result = apiResult.result;
            mCitylistEntry = result.list;
            mCityList.clear();
            for(CityListAccount.ListEntity li : mCitylistEntry) {
                mCityList.add(li.class_name);
            }
            initPopWindow(mCityList, mPublish_area,3);
        } else {
            showToast(apiResult.message);
        }
    }

    private void initView() {
        mPublish_img = (ImageView) findViewById(R.id.publish_img);
        mPublish_industry = (TextView) findViewById(R.id.publish_industry);
        mPublish_area = (TextView) findViewById(R.id.publish_area);

        mPublish_img.setOnClickListener(this);
        mPublish_industry.setOnClickListener(this);
        mPublish_area.setOnClickListener(this);
    }
    private void initBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_project);
        PublishProject1ViewModel model = new PublishProject1ViewModel();
        model.setIndustryType("请选择");
        model.setRegionCity("请选择");
        model.setChecked(true);
        mBinding.setModel(model);
    }

    /**
     * 显示选择对话框
     */
    private void uploadImg() {

        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery,
                IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != Activity.RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case RESULT_REQUEST_CODE:
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
        Subscription s = mBiz.uploadImg(files)
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
            UploadImgObj result = apiResult.result;
            mBinding.getModel().setStockImg(result.savePath);
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
        mPublish_img.setImageDrawable(drawable);
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
        startActivityForResult(intent, RESULT_REQUEST_CODE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.publish_img:
                uploadImg();
                break;
            case R.id.publish_industry:
                //选择行业
                selectIndustry();
                break;
            case R.id.publish_area:
                //选择所在地
                selectArea();
                break;
        }
    }

    /**
     * 选择所在地
     */
    private void selectArea() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;
        initPopWindow(mProvinceList, mPublish_area,1);
    }

    /**
     * 选择行业
     */
    private void selectIndustry() {
        if (isAreaPop) {
            isAreaPop = false;
            return;
        }
        isAreaPop = true;
        initPopWindow(mDictList, mPublish_industry,0);
    }

    public void startNewProject(View view) {
        PublishProject1ViewModel model = mBinding.getModel();
        CheckResult check = model.check();
        if(check.isSuccess) {
            Intent intent = new Intent(this, PublishProject2Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("project",model);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            showToast(check.errorMessage);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    /**
     * 初始化PopWindow
     * @param list
     */
    private void initPopWindow(final List<String> list, final TextView textview, final int type) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);

        //设置ListView类型的适配器
        SelectAreaAdapter adapter = new SelectAreaAdapter(this, list);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                textview.setText(list.get(position));
                isAreaPop = false;
                //筛选项目列表
                selectProjectList(position, type);
            }
        });

        listPopupWindow.setAnchorView(textview);

        //设置对话框的宽高
        listPopupWindow.setWidth(200);
        listPopupWindow.setHeight(400);
        listPopupWindow.setModal(false);
        listPopupWindow.show();
    }

    /**
     * 选择行业和所在地
     * @param position
     * @param type
     */
    private void selectProjectList(int position, int type) {
        PublishProject1ViewModel model = mBinding.getModel();
        switch (type) {
            case 0:
                //选择行业
                model.setIndustryType(mDictInfoList.get(position).code);
                break;
            case 1:
                //选择所在地
//                model.setRegionCity(mCitylist.get(position).class_id);
                int province = mProvincelistEntry.get(position).class_id;
                mProjectArea = mProvincelistEntry.get(position).class_name;
                mProjectAreaId = province+",";
                getCityList(province);
                break;
            default:
                mProjectArea += mCitylistEntry.get(position).class_name;
                mProjectAreaId += mCitylistEntry.get(position).class_id;
                mPublish_area.setText(mProjectArea);
                model.setRegionCity(mProjectAreaId);
                break;
        }
    }
}
