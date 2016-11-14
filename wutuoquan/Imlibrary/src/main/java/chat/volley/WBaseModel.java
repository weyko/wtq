package chat.volley;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.common.user.UserInfoHelp;
import chat.common.util.output.LogUtil;
public class WBaseModel<T> implements Serializable {
	private final Class<T> mClazz;
	protected Context mContext;
	protected static RequestQueue requestQueue = null;
	public final static int NO_NETWORK_CONNECTED = -1;
	public final static int UNKNOW_SERVER_ERROR = -2;
	public final static int TOKEN_INVALID = 401;//token失效
	private String userId = "";
	private String token = "";
	private String appType = "wt";
	private WRequestCallBack callBack=null;
	private WRequestCallBackWithRQT callBackWithRQT;

	public WBaseModel(Context context, Class<T> clazz) {
		this.mClazz = clazz;
		this.mContext = context.getApplicationContext();
		if(requestQueue==null){
			requestQueue = Volley.newRequestQueue(mContext);
		}
	}
	@SuppressWarnings("unchecked")
	public void httpJsonRequest(final int method, String url,
			Map<String, Object> mParams, final WRequestCallBack callBack) {
		if (!isNetworkConnected(mContext)) {
			if (callBack != null) {
				callBack.receive(NO_NETWORK_CONNECTED, null);
			}
			if (mParams!=null){
				mParams.clear();
				mParams = null;
				System.gc();
			}
			return;
		}
		this.callBack = callBack;
		final long requestStartTime = System.currentTimeMillis();
		httpJsonRequestBody(method, url, mParams, requestStartTime);
	}
	@SuppressWarnings("unchecked")
	public void httpRequest(final int method, String url,
			JSONObject jsonObject, final WRequestCallBack callBack) {
		if (!isNetworkConnected(mContext)) {
			if (callBack != null) {
				callBack.receive(NO_NETWORK_CONNECTED, null);
			}
			return;
		}
		this.callBack = callBack;
		final long requestStartTime = System.currentTimeMillis();
		httpJsonRequestBody(method, url, jsonObject, requestStartTime);
	}
	@SuppressWarnings("unchecked")
	public void httpJsonRequest(final int method, String url, Map<String, Object> mParams, long requestStartTime, final WRequestCallBackWithRQT callBack) {
		if (!isNetworkConnected(mContext)) {
			if (callBack != null) {
				callBack.receive(NO_NETWORK_CONNECTED, null,0);
			}
			if (mParams!=null){
				mParams.clear();
				mParams = null;
				System.gc();
			}
			return;
		}
		this.callBackWithRQT = callBack;
		httpJsonRequestBody(method, url, mParams, requestStartTime);
	}

	private void httpJsonRequestBody(int method, String url, Map<String, Object> mParams, long requestStartTime) {
		JSONObject jsonObject = null;
		if (mParams != null && (method == Method.POST || method == Method.PUT)) {
			// 解决Map中带数组转换String多了双引号的问题
			jsonObject = new JSONObject();
			for (String key : mParams.keySet()) {
				if ( mParams.get(key) instanceof List<?>){
					JSONArray array=new JSONArray();
					List<?> listParams=(List<?>)mParams.get(key);
					if (listParams!=null){
						for(int i=0;i<listParams.size();i++){
							if (listParams.get(i) instanceof HashMap<?, ?>){
								HashMap<String, Object> tMaps=(HashMap<String, Object>)listParams.get(i);
								JSONObject child = new JSONObject(tMaps);
								array.put(child);
							}/*else if (listParams.get(i) instanceof HashMap<?, ?>){
								HashMap<String, Object> tMaps=(HashMap<String, Object>)listParams.get(i);
								JSONObject child = new JSONObject(tMaps);
								array.put(child);
							}*/else{
								array.put(listParams.get(i));
							}
						}
					}
					try {
						jsonObject.put(key, array);
					} catch (org.json.JSONException e) {
						e.printStackTrace();
					}
				}else{
					try {
						jsonObject.put(key, mParams.get(key));
					} catch (org.json.JSONException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (mParams != null
				&& (method == Method.GET || method == Method.DELETE)) {
			String temp = "";
			for (String key : mParams.keySet()) {
				String value = String.valueOf(mParams.get(key));
				if (value != null) {
					try {
						value = URLEncoder.encode(value, "utf-8");
						temp += "&" + key + "=";
						temp += value;
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (url != null && url.length() > 0 && temp.length() > 0) {
				url += "?" + temp.substring(1);
			}
		}
		final String logStr = LogUtil.timeAndNetType(mContext)
				+ LogUtil.urlLogFormat(url, mParams);
		/*WRequestErrorListener requestErrorListener = new WRequestErrorListener(
				callBack);
		requestErrorListener.setLog(logStr, requestStartTime);*/
		if (mParams!=null){
			mParams.clear();
			mParams = null;
			System.gc();
		}
		addToRequestQueue(method, url, jsonObject,logStr,
				requestStartTime);
	}
	private void httpJsonRequestBody(int method, String url, JSONObject jsonObject, long requestStartTime) {
		final String logStr = LogUtil.timeAndNetType(mContext)
				+ jsonObject.toString();
		addToRequestQueue(method, url, jsonObject,logStr,
				requestStartTime);
	}

	private boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private void addToRequestQueue(int method, final String url,
								   JSONObject jsonObject,
								   final String logStr, final long requestStartTime) {
		MxRequestErrorListener requestErrorListener = new MxRequestErrorListener();
		requestErrorListener.setLog(logStr, requestStartTime);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method,
				url, jsonObject, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(response!=null){
							LogUtil.i("response.toString=", response.toString());
							//增加接口响应数据大小监控【流量】
							LogUtil.writeFluxLogtoFile(logStr+"--->responseSize="+response.toString().length());
						}
						try {
							T result = null;
							if(response != null){
								result = JSON.parseObject(response.toString(),
										mClazz);
							}
							if (callBack != null) {
								callBack.receive(200, result);
							}
							if (callBackWithRQT!= null) {
								callBackWithRQT.receive(200, result,requestStartTime);
							}
							if (result instanceof WBaseBean) {
								WBaseBean baseBean = (WBaseBean) result;
								checkToken(baseBean.getStatus());
								if (!baseBean.isResult()) {
									LogUtil.writeRequestLogtoFile(logStr
											+ LogUtil.responseInfoFromat(
													requestStartTime,
													String.valueOf(baseBean.getStatus()),
													baseBean.getMsg()));
								} else {
									LogUtil.writeRequestLogtoFile(logStr
											+ "response=200 OK");
								}
							}
						} catch (JSONException e) {
							WBaseBean result = null;
							if(response != null) {
								result = JSON.parseObject(
										response.toString(), WBaseBean.class);
							}
							if (callBack != null) {
								callBack.receive(200, result);
							}
							if (callBackWithRQT!= null) {
								callBackWithRQT.receive(200, result,requestStartTime);
							}
							if(result != null){
								checkToken(result.getStatus());
							}
							e.printStackTrace();
							LogUtil.writeRequestLogtoFile(logStr
									+ LogUtil.responseInfoFromat(
											requestStartTime, "200",
											e.getMessage()));
						}
					}
				}, requestErrorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("Accept", "application/json");
//					headers.put("dataID", UUIDUtil.getInstance().getDataId());
//					headers.put("token", "8fd77601-98b5-4053-a6ad-a3763cd6273d");
					headers.put("token", "abcd");
					headers.put("device", "4");
					headers.put("deviceId", "android");
//					headers.put("deviceId", UUIDUtil.getInstance().getDeviceId());
					userId = UserInfoHelp.getInstance().getUserId();
					token = UserInfoHelp.getInstance().getToken();
				return headers;
			}
		};
		requestErrorListener.setJsonObjectRequest(jsonObjectRequest);
		 jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1,
		 1.0f));
		jsonObjectRequest.setTag(this);
		requestQueue.add(jsonObjectRequest);
	}

	private void checkToken(int code){
		if (mContext!=null&&code==TOKEN_INVALID){
			 Intent itent = new Intent();
			 itent.setAction("com.yunxun.moxian.receiver.sendmessage");
			 itent.putExtra("type",TOKEN_INVALID);
			 itent.putExtra("msg","token invalid");
			 mContext.sendBroadcast(itent);
		}
	}
	private class MxRequestErrorListener implements Response.ErrorListener {
		JsonObjectRequest jsonObjectRequest = null;
		String logStr;
		long requestStartTime;
		boolean retry = true;

		public MxRequestErrorListener() {
		}

		public void setJsonObjectRequest(JsonObjectRequest r) {
			jsonObjectRequest = r;
		}

		public void setLog(String logStr, long requestStartTime) {
			this.logStr = logStr;
			this.requestStartTime = requestStartTime;
		}

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			System.out.println("Errorresponse=" + arg0.getMessage());
			int code = ((arg0.networkResponse == null) ? UNKNOW_SERVER_ERROR
					: arg0.networkResponse.statusCode);
			if (retry && code == 408 && jsonObjectRequest != null) {
				retry = false;
				requestQueue.add(jsonObjectRequest);
				System.out
						.println("----------------------do 408 retry---------------");
			} else {
				if (code == TOKEN_INVALID) {// token失效,发送广播退出登录
					Intent intent = new Intent();
					intent.setAction("com.yunxun.moxian.receiver.sendmessage");
					intent.putExtra("type", TOKEN_INVALID);
					mContext.sendBroadcast(intent);
				}
				if (callBack != null && code != TOKEN_INVALID) {
					callBack.receive(code, null);
				}
				LogUtil.writeRequestLogtoFile(logStr
						+ LogUtil.responseInfoFromat(requestStartTime, ""
								+ code, arg0.getMessage()));
			}
		}
	}

	public void httpPostRequest(final int method, String url, String mParams,
			final WRequestCallBack callBack) {
		if (!isNetworkConnected(mContext)) {
			if (callBack != null) {
				callBack.receive(NO_NETWORK_CONNECTED, null);
			}
			return;
		}
		this.callBack = callBack;
		final long requestStartTime = new Date().getTime();
		final String logStr = LogUtil.timeAndNetType(mContext) + url + " "
				+ mParams;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(mParams);
		} catch (org.json.JSONException e) {
			e.printStackTrace();
		}
		MxRequestErrorListener requestErrorListener = new MxRequestErrorListener(
				/*callBack*/);
		requestErrorListener.setLog(logStr, requestStartTime);
		addToRequestQueue(method, url, jsonObject, /*callBack,*/ logStr,
				requestStartTime);
	}
	public void cancelRequest() {
		requestQueue.cancelAll(this);
		this.callBack = null;
	}
}