package chat.common.fileupload;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import chat.image.ImageUtils;
import chat.image.ProgressImageView;

public class UploadFileUtils {

	public static final int imgFileSizelimit = 10*1024*1024;// 图片限制在10M。
	public static final int audioFileSizelimit = 150*1024;// 声音文件限制在150K.
	public static final int vedioFileSizelimit = 3*1024*1024;// 视频文件限制在3M。
	public static final int S_TIMEOUT=1*60*1000;//等待数据超时时间
	public static final int CONNECTION_TIMEOUT=30*1000;//连接超时时间
	
	private static BasicHttpParams getHttpParams(File file){
		BasicHttpParams httpParams = new BasicHttpParams();
		if(file.length()>100*1024){
			HttpConnectionParams.setSoTimeout(httpParams, 3*60*1000);
		}else{
			HttpConnectionParams.setSoTimeout(httpParams, S_TIMEOUT);
		}
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        return httpParams;
	}
	/**
	 * 单文件上传
	 * 
	 * @param entityParams
	 *            参数
	 * @param fileKey
	 * @param file
	 * @param url
	 * @return
	 */
	public static String uploadFile(Map<String, Object> headParams,
			Map<String, Object> entityParams, String fileKey, File file,
			String url) {
		HttpClient client = null;
		HttpPost post = null;
		try {
			
			client = new DefaultHttpClient(getHttpParams(file));
			post = new HttpPost(url);

			if (headParams != null) {
				for (String name : headParams.keySet()) {
					post.addHeader(name, String.valueOf(headParams.get(name)));
				}
			}

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			String params = url + "、 {";
			if (entityParams != null) {
				for (String name : entityParams.keySet()) {
					entity.addPart(
							name,
							new StringBody(String.valueOf(entityParams
									.get(name)), Charset.forName(HTTP.UTF_8)));
					try {
						params = params + "\'" + name + "\':"
								+ String.valueOf(entityParams.get(name)) + ", ";
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			System.out.println("params==" + params);

			if (file != null && file.exists()) {
				entity.addPart(fileKey, new FileBody(file));
			}
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			int res = response.getStatusLine().getStatusCode();

			System.out.println("res==" + res);
			String resultStr=EntityUtils.toString(response.getEntity());
			System.out.println("resultStr==" + resultStr);
			if (res == 200) {
				return resultStr;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {// 释放资源
			if(client != null){
				client.getConnectionManager().shutdown();
			}
		}
		return null;
	}

	/**
	 * 单文件上传
	 * 
	 * @param entityParams
	 *            参数
	 * @param fileKey
	 * @param file
	 * @param url
	 * @param uploadListener
	 *            文件上传进度监听器
	 * @return
	 */
	public static String uploadFile(Map<String, Object> headParams,
			Map<String, Object> entityParams, String fileKey, File file,
			String url, ProgressImageView.ImageUploadListener uploadListener) {
		HttpClient client = null;
		HttpPost post = null;
		try {
			client = new DefaultHttpClient(getHttpParams(file));
			post = new HttpPost(url);

			if (headParams != null) {
				for (String name : headParams.keySet()) {
					post.addHeader(name, String.valueOf(headParams.get(name)));
				}
			}
			long size = 0;
			if (file != null && file.exists()) {
				size = file.length();
			}
			ListenerProgressMultipartEntity entity = new ListenerProgressMultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE, uploadListener,size);
			String params = url + "、 {";
			if (entityParams != null) {
				for (String name : entityParams.keySet()) {
					entity.addPart(
							name,
							new StringBody(String.valueOf(entityParams
									.get(name)), Charset.forName(HTTP.UTF_8)));
					try {
						params = params + "\'" + name + "\':"
								+ String.valueOf(entityParams.get(name)) + ", ";
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			System.out.println("params==" + params);

			if (file != null && file.exists()) {
				entity.addPart(fileKey, new FileBody(file,"multipart/form-data"));
			}
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			int res = response.getStatusLine().getStatusCode();

			System.out.println("res==" + res);
			if (res == 200) {
				return EntityUtils.toString(response.getEntity());
			} else {
				System.out.println("resultStr=="
						+ response.getEntity().toString());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {// 释放资源
			if(client != null){
				client.getConnectionManager().shutdown();
			}
		}
		return null;
	}

	/**
	 * 多文件上传
	 * 
	 * @param entityParams
	 *            参数
	 * @param fileKeys
	 * @param files
	 * @param url
	 * @return
	 */
	public static String uploadMultiFiles(Map<String, Object> entityParams,
			String[] fileKeys, File[] files, String url) {
		HttpClient client = null;
		HttpPost post = null;
		try {
			client = new DefaultHttpClient();
			post = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			if (entityParams != null) {
				for (String name : entityParams.keySet()) {
					entity.addPart(
							name,
							new StringBody(String.valueOf(entityParams
									.get(name)), Charset.forName(HTTP.UTF_8)));
				}
			}
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].exists()) {
						entity.addPart(fileKeys[i], new FileBody(files[i]));// 图片
					}
				}
			}
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			int res = response.getStatusLine().getStatusCode();
			if (res == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {// 释放资源
			if(client != null){
				client.getConnectionManager().shutdown();
			}
		}
		return null;
	}

	/** 判断该文件是否是一个图片。 */
	public static boolean isImage(String fileName) {
		String tmp=fileName.toLowerCase();
		return tmp.endsWith(".jpg") || tmp.endsWith(".jpeg")
				|| tmp.endsWith(".png");
	}

	/** 判断该文件是否是一个声音文件。 */
	public static boolean isAudio(String fileName) {
		return fileName.endsWith(".amr");
	}

	/** 判断该文件是否是一个视频文件。 */
	public static boolean isVedio(String fileName) {
		return fileName.endsWith(".mp4") || fileName.endsWith(".avi");
	}

	/**
	 * 检查上传文件大小是否在限制内?
	 * @return
	 */
	public static boolean checkFileIsUpLoadOk(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			if (isImage(filePath)) {
				if (file.length() < imgFileSizelimit) {
					//增加对图片旋转后的修正。
					ImageUtils.fixPicture(filePath);
					return true;
				}
			} else if (isAudio(filePath)) {
				if (file.length() < audioFileSizelimit) {
					return true;
				}
			} else if (isVedio(filePath)) {
				if (file.length() < vedioFileSizelimit) {
					return true;
				}
			}
		}
		return false;
	}

	public static Map<String, Object> getCommonHeadParams(String userId,
			String token) {
		Map<String, Object> headParams = new HashMap<String, Object>();
		headParams.put("userId", userId);
		headParams.put("token", token);
		return headParams;
	}
}