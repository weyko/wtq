package chat.common.fileupload;

import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.io.File;
import java.util.Map;

import chat.image.ProgressImageView;

/**
 * 单文件上传
 */
public class UploadFileTasks extends AsyncTask<String, String, UploadBean> {
	Map<String, Object> mMap;
	Map<String, Object> headParams;
	String fileKey;
	String mFilePath;
	String mURL;
	Message msg;
	private ProgressImageView.ImageUploadListener uploadListener;

	public UploadFileTasks(Map<String, Object> headParams,
						   Map<String, Object> map, String fileKey, String filePath, String url) {
		this.headParams = headParams;
		this.mMap = map;
		this.mFilePath = filePath;
		this.mURL = url;
		this.fileKey = fileKey;
	}

	public UploadFileTasks(Map<String, Object> headParams,
						   Map<String, Object> map, String fileKey, String filePath,
						   String url, ProgressImageView.ImageUploadListener uploadListener) {
		this.headParams = headParams;
		this.mMap = map;
		this.mFilePath = filePath;
		this.mURL = url;
		this.fileKey = fileKey;
		this.uploadListener = uploadListener;
	}

	@Override
	protected UploadBean doInBackground(String... params) {
		String result = null;
		UploadBean uploadBean = new UploadBean();
		try {
			// 对文件初始大小进行过滤
			if (!UploadFileUtils.checkFileIsUpLoadOk(mFilePath)) {
				uploadBean.setStatus(-2);
				return uploadBean;
			}
			File file = new File(mFilePath);
			// 对图片进行压缩处理
			if (UploadFileUtils.isImage(mFilePath)) {
				file = CompressImgUtil.compressImg(mFilePath);
				if(file != null&& file.length() > CompressImgUtil.compressedFilelimitSS/2){
					file = CompressImgUtil.compressImgMaxLimit(mFilePath);
				}
				// 压缩后达不到上传标准的图片不予上传
				if (file != null
						&& file.length() > CompressImgUtil.compressedFilelimitSS) {
					uploadBean.setStatus(-1);
					return uploadBean;
				}
			}
			if (file != null) {
				if (uploadListener!=null){
					result = UploadFileUtils.uploadFile(headParams, mMap, fileKey,
									file, mURL, uploadListener);
				}else{
					result = UploadFileUtils.uploadFile(headParams, mMap, fileKey, file, mURL);
				}
				try {
					if (result!=null&&result.length()>0){
						int check_pos_st = result.indexOf("{");
						boolean check_end = result.endsWith("}");
						int check_pos_end = result.lastIndexOf("}");
						if (check_pos_st > 0) {
							if (check_end) {
								result = result.substring(check_pos_st);
							} else {
								if (check_pos_end > check_pos_st) {
									result = result.substring(check_pos_st,
											check_pos_end + 1);
								}
							}
						} else {
							result = result.substring(0, check_pos_end + 1);
						}
					}
				} catch (Exception e) {
				}
				System.out.println("uploadFile---------result="+result);
				if (!TextUtils.isEmpty(result)) {// 如果返回为空
					try {
						uploadBean = JSON
								.parseObject(result, UploadBean.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return uploadBean;
				}
				return uploadBean;
			} else {
				uploadBean.setStatus(-1);
				return uploadBean;
			}
		} catch (Exception e) {
			uploadBean.setStatus(-1);
			return uploadBean;
		}
	}

	@Override
	protected void onPreExecute() {
	}
}