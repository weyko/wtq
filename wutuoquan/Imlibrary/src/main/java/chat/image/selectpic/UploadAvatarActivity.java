package chat.image.selectpic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.fileupload.UploadBean;
import chat.common.fileupload.UploadFileTasks;
import chat.common.util.file.FileOpreation;
import chat.image.activity.PhotoWallActivity;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * 上传头像
 *
 */
public class UploadAvatarActivity extends BaseActivity {
	private String cropPath;
	private int avatarId = -1;
	/** 是否群头像 */
	private boolean isGroup = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		avatarId = getIntent().getIntExtra("avatarId", -1);
		isGroup = getIntent().getBooleanExtra("isGroup", false);
		Intent intent = new Intent(this, PhotoWallActivity.class);
		intent.putExtra(PhotoWallActivity.KEY_IS_SHOW_RECENTLY, true);// 是否显示最近图片
		intent.putExtra(PhotoWallActivity.KEY_LIMIT_RECENTLY_SHOW_NUM, 100);// 最新图片显示最大数目
		intent.putExtra(PhotoWallActivity.KEY_LIMIT_SELECTED_NUM, 1);// 可以的最大图片数目
		this.startActivityForResult(intent,
				PhotoWallActivity.REQUEST_CODE_SELECT_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (PhotoWallActivity.REQUEST_CODE_SELECT_PHOTO == requestCode) {
				if (data != null) {
					// 存放所选照片路径的list
					ArrayList<String> pic_filse = data
							.getStringArrayListExtra(PhotoWallActivity.KEY_SELECTED_PHOTOS);
					if (pic_filse != null) {
						System.out.println(pic_filse);
						if (!pic_filse.isEmpty()) {
							String pic_path = pic_filse.get(0);
							File f = new File(pic_path);
							crop(Uri.fromFile(f));
						}
					} else {
						finish();
					}
				} else {
					finish();
				}
			} else if (108 == requestCode) {
				uploadFile();
			} else {
				finish();
			}
		} else {
			finish();
		}
	}

	private File createTempFile() {
		String dir = Constant.CAMERA_PATH + "CROP" + File.separator;
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File file = new File(dir + System.currentTimeMillis() + ".jpg");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		cropPath = file.getAbsolutePath();
		return file;
	}

	private void crop(Uri uri) {
		File file = createTempFile();
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 800);
		intent.putExtra("outputY", 800);

		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		intent.putExtra("return-data", false);//设置为不返回数据
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		startActivityForResult(intent, 108);
	}

	public void uploadFile() {
		if (cropPath == null) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", IMClient.getInstance().getSSOUserId());
		params.put("fileType", 0);
		params.put("fileClassfycation", isGroup ? 14 : 2);
		MyUploadFileTasks myUploadFileTasks = new MyUploadFileTasks(params,
				"uploadFile", cropPath, URLConfig.UP_LOAD);
		myUploadFileTasks.execute();
		showLoading();
	}

	private void addAvatarImg(final String imageUrl) {
		WBaseModel<WBaseBean> model = new WBaseModel<WBaseBean>(this,
				WBaseBean.class);
		HashMap<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("avatarPath", imageUrl);
		if (avatarId > 0) {
			parameter.put("avatarId", avatarId);
		}
		String url = URLConfig.UPLOADAVATAR;
		int method = Method.POST;
		model.httpJsonRequest(method, url, parameter, new WRequestCallBack() {
			@Override
			public void receive(int httpStatusCode, Object data) {
				dissmisLoading();
				if (data != null && data instanceof WBaseBean) {
					WBaseBean WBaseBean = (WBaseBean) data;
					if (WBaseBean.isResult()) {
						Intent intent = new Intent();
						intent.putExtra("avatarUrl", imageUrl);
						setResult(RESULT_OK, intent);
						finish();
					} else {
						showResutToast(WBaseBean.getCode());
						finish();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.warning_service_error),
							Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
	}

	class MyUploadFileTasks extends UploadFileTasks {

		public MyUploadFileTasks(Map<String, Object> params, String fileKey,
				String filePath, String url) {
			super(null, params, fileKey, filePath, url);
		}

		@Override
		protected void onPostExecute(UploadBean bean) {
			super.onPostExecute(bean);
			FileOpreation.delFile(cropPath);// 删除临时文件
			if (bean.isResult()) {
				String imageUrl = bean.getData().getSavePath();
				System.out.println("----------------avatarUrl=" + imageUrl);
				if (isGroup) {
					finish();
				} else {
					addAvatarImg(imageUrl);
				}
			} else {
				dissmisLoading();
				Toast.makeText(getApplicationContext(),
						getString(R.string.warning_service_error),
						Toast.LENGTH_SHORT).show();
				finish();
			}

		}
	}

	@Override
	protected void initView() {
	}

	@Override
	protected void initEvents() {
	}

	@Override
	protected void initData() {

	}
}