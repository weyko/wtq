package chat.image.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.imlibrary.R;

import java.io.File;
import java.util.ArrayList;

import chat.image.photo.MImageSelectorObserver;
import chat.image.photo.adapter.PhotoWallAdapter;
import chat.image.photo.model.PhotoSelectedItem;
import chat.image.photo.model.PhotoWallItem;
import chat.image.photo.utils.PickPhotoUtils;
import chat.image.photo.utils.ScreenUtils;
import chat.image.photo.utils.SearchImageUtils;

/**
 * selected photos
 */
public class PhotoWallActivity extends Activity implements MImageSelectorObserver {
	/*
	 * start activity for result
	 */
	public static final int REQUEST_CODE_SELECT_PHOTO=1001;//request code for select photo
	public static final int REQUEST_CODE_CAMERA=1002;//request code for start camera
	public static final int REQUEST_CODE_CANCEL_SELECT_FOLDER=1003;//request code for select photos in some folder
	protected static final int REQUEST_PHOTO_REVIEW = 1004;
	
	public static final String KEY_SELECTED_PHOTOS="photoPaths";//key for transfer selected photo paths to user
	public static final String KEY_LIMIT_RECENTLY_SHOW_NUM="maxRecentlyShowNum";//key for limited max numbers for recently photo page show
	public static final String KEY_LIMIT_SELECTED_NUM="maxSelectedNum";//key for limit max numbers for selected photo
	public static final String KEY_IS_SHOW_RECENTLY="isShowRecently";//key for is show recently
	public static final String SELECT_DYNAMIC_PIC="select_dynamic_pic";//key for is show recently
	
	//image modify cache dir
	public static final String IMAGE_ROOT_DIR=Environment
			.getExternalStorageDirectory().toString() + "/moxian_image/";
	
	public static final int RECENTLY_TAG=100;//recently photo
	public static final int FLODER_TAG=200;//some folder photo
	
	private int maxRecentlyShowNum=200;//limited max numbers for recently photo page show
	private int maxSelectedNum=9;//max numbers for selected photo limit
	
	public static boolean isShowRecentlyPhotos=true;//whether show recently photos 
	
	private TextView title;//page title
	private TextView nextStep;//next step
	private TextView preview;//预览
	private ArrayList<PhotoWallItem> list;//page data
	private ArrayList<PhotoSelectedItem> selectedPhotos=new ArrayList<PhotoSelectedItem>();//selected photos
	private GridView mPhotoWall;
	private PhotoWallAdapter adapter;
	/**
	 * path for current folder 
	 */
	private String currentFolder = null;
	
	/**
	 * is show recently photos
	 */
	private boolean isLatest = true;

	/**
	 * save image path 
	 */
	private String tempCameraPath = "";
	
	/**
	 * recently photo numbers
	 */
	private int recentlyPhotoNum=0;
	private String recentlyImagePath="";
	private boolean isSelectDynamicPic = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState!=null){
			String path=savedInstanceState.getString("cameraFile");
			if(path!=null&&path.length()>0){
				tempCameraPath = path;
			}
		}
		setContentView(R.layout.selector_photo_wall);
		getIntentParams();//get params
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		ScreenUtils.screenW = metric.widthPixels;
		ScreenUtils.screenH = metric.heightPixels;
		
		title = (TextView) this.findViewById(R.id.topbar_title_tv);

		Button backBtn = (Button) this.findViewById(R.id.topbar_left_btn);
		Button cancelBtn = (Button) this.findViewById(R.id.topbar_right_btn);
		backBtn.setText(R.string.selector_photo_album);
		backBtn.setVisibility(View.VISIBLE);
		cancelBtn.setText(R.string.cancel);
		cancelBtn.setVisibility(View.VISIBLE);

		mPhotoWall = (GridView) this.findViewById(R.id.photo_wall_grid);
		if (isShowRecentlyPhotos){
			list = SearchImageUtils.getLatestImagePaths(this, maxRecentlyShowNum, selectedPhotos);
			recentlyPhotoNum = list.size()-1;
			if(list.size()>1){
				recentlyImagePath = list.get(1).photoFilePath;
			}
			title.setText(R.string.selector_latest_image);
		}else{
			String folderPath=SearchImageUtils.getDefaultImagePath(this);
			list = SearchImageUtils.getAllImagePathsByFolder(folderPath, selectedPhotos);
			if(folderPath!=null&&folderPath.length()>0){
				int lastSeparator = folderPath.lastIndexOf(File.separator);
				String folderName = folderPath.substring(lastSeparator + 1);
				title.setText(folderName);
			}else{
				title.setText(R.string.selector_all_image);
			}
		}
		adapter = new PhotoWallAdapter(this, list);
		adapter.setSelectNumLimit(maxSelectedNum);
		adapter.setMImageSelectorObserver(this);
		mPhotoWall.setAdapter(adapter);

		nextStep = (TextView) this.findViewById(R.id.next_step);
		preview = (TextView) this.findViewById(R.id.photo_wall_preview);
		preview.setTextColor(getResources().getColor(R.color.text_color_no_click));
		preview.setEnabled(false);
		if(isSelectDynamicPic){
			preview.setVisibility(View.VISIBLE);
		} else{
			preview.setVisibility(View.GONE);
		}
		String tip=this.getString(R.string.ok);
		tip += "("+selectedPhotos.size()+"/"+maxSelectedNum+")";
		nextStep.setText(tip);
		nextStep.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//transfer selected photo paths
				ArrayList<String> paths = new ArrayList<String>();
				for(int i=0;i<selectedPhotos.size();i++){
					paths.add(selectedPhotos.get(i).filePath);
				}
				Intent dataIntent = new Intent();
				dataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				dataIntent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, paths);
				setResult(RESULT_OK, dataIntent);
				PhotoWallActivity.this.finish();
			}
		});
		nextStep.setClickable(selectedPhotos.size()==0?false:true);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backAction();
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setResult(RESULT_OK);
				PhotoWallActivity.this.finish();
			}
		});
	}

	private void getIntentParams(){
		Intent intent = getIntent();
		if(intent!=null){
			isShowRecentlyPhotos = intent.getBooleanExtra(KEY_IS_SHOW_RECENTLY, true);
			maxRecentlyShowNum = intent.getIntExtra(KEY_LIMIT_RECENTLY_SHOW_NUM, maxRecentlyShowNum);
			maxSelectedNum = intent.getIntExtra(KEY_LIMIT_SELECTED_NUM, 1);
			isSelectDynamicPic = intent.getBooleanExtra(SELECT_DYNAMIC_PIC, false);
		}
	}
	
	private void backAction() {
		Intent intent = new Intent(this, PhotoAlbumActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		if (isShowRecentlyPhotos){
			intent.putExtra("recentlyPhotoNum",recentlyPhotoNum);
			intent.putExtra("recentlyImagePath",recentlyImagePath);
			recentlyPhotoNum = list.size()-1;
		}
		this.startActivityForResult(intent, REQUEST_CODE_CANCEL_SELECT_FOLDER);
	}

	// 重写返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			this.finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 根据图片所属文件夹路径，刷新页面
	 */
	private void updateView(int code, String folderPath) {
		list.clear();
		adapter.notifyDataSetChanged();
		if (code == FLODER_TAG) { // 某个相册
			int lastSeparator = folderPath.lastIndexOf(File.separator);
			String folderName = folderPath.substring(lastSeparator + 1);
			title.setText(folderName);
			list.addAll(SearchImageUtils.getAllImagePathsByFolder(folderPath,selectedPhotos));
		} else if (code == RECENTLY_TAG) { // 最近照片
			title.setText(R.string.selector_latest_image);
			list.addAll(SearchImageUtils.getLatestImagePaths(this,maxRecentlyShowNum,selectedPhotos));
		}
		adapter.notifyDataSetChanged();
		if (list.size() > 0) {
			// 滚动至顶部
			mPhotoWall.smoothScrollToPosition(0);
		}
	}

	private void doNewIntent(Intent intent) {
		if(intent==null){
			return;
		}
		int code = intent.getIntExtra("code", -1);
		if (code == FLODER_TAG) {
			// 某个相册
			String folderPath = intent.getStringExtra("folderPath");
			if (isLatest
					|| (folderPath != null && !folderPath.equals(currentFolder))) {
				currentFolder = folderPath;
				updateView(FLODER_TAG, currentFolder);
				isLatest = false;
			}
		} else if (code == RECENTLY_TAG) {
			// “最近照片”
			if (!isLatest) {
				updateView(RECENTLY_TAG, null);
				isLatest = true;
			}
		}
	}

	@Override
	public void onSelectImage(boolean isSelected, String filePath, String folder) {
		// TODO Auto-generated method stub
		System.out.println("isSelected="+isSelected+" folder="+folder+" filePath="+filePath);
		if (maxSelectedNum==1){
			selectedPhotos.clear();
			PhotoSelectedItem item=new PhotoSelectedItem();
			item.filePath = filePath;
			item.dir = folder;
			selectedPhotos.add(item);
		}else{
			if(isSelected){
				PhotoSelectedItem item=new PhotoSelectedItem();
				item.filePath = filePath;
				item.dir = folder;
				selectedPhotos.add(item);
			}else{
				for(int i=0;i<selectedPhotos.size();i++){
					PhotoSelectedItem item=selectedPhotos.get(i);
					if(item.filePath.compareToIgnoreCase(filePath)==0&&
							item.dir.compareToIgnoreCase(folder)==0){
						selectedPhotos.remove(i);
						break;
					}
				}
			}
		}
		String tip=this.getString(R.string.ok);
		tip += "("+selectedPhotos.size()+"/"+maxSelectedNum+")";
		nextStep.setText(tip);
		nextStep.setClickable(selectedPhotos.size()==0?false:true);
		
		int selectNum = getSelectNum();
		if(selectNum > 0){
			preview.setTextColor(getResources().getColor(R.color.text_color_click));
			preview.setEnabled(true);
		} else{
			preview.setTextColor(getResources().getColor(R.color.text_color_no_click));
			preview.setEnabled(false);
		}
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(adapter!=null){
			adapter.clearMemory();
		}
	}

	@Override
	public void onStartCamera() {
		// TODO Auto-generated method stub
		File dir = new File(IMAGE_ROOT_DIR);
		if(!dir.exists()){
			dir.mkdir();
		}
		tempCameraPath = IMAGE_ROOT_DIR + System.currentTimeMillis() + ".jpg";
		System.out.println("Camera Image save path=" + tempCameraPath);
		PickPhotoUtils.getInstance().takePhoto(this, "tempUser",
				tempCameraPath);
	}
	
	@Override  
    protected void onSaveInstanceState(Bundle outState) {  
		if(outState!=null&&tempCameraPath!=null){
			outState.putString("cameraFile", tempCameraPath);
		}
        super.onSaveInstanceState(outState);
    }  
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PickPhotoUtils.PickPhotoCode.PICKPHOTO_TAKE:
				File fi = new File(tempCameraPath);
				PickPhotoUtils.getInstance().takeResult(this, data, fi);
				// 相机的图片
				ArrayList<String> camepaths = new ArrayList<String>();
				camepaths.add(tempCameraPath);
				Intent dataIntent = new Intent();
				dataIntent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, camepaths);	
				setResult(RESULT_OK, dataIntent);
				this.finish();
				break;
			case REQUEST_CODE_CANCEL_SELECT_FOLDER:
				if(data!=null){
					doNewIntent(data);
				}else{
					setResult(RESULT_OK);
					this.finish();
				}
				break;
			case REQUEST_PHOTO_REVIEW: //TODO
				if(data == null){
					return;
				}
				ArrayList<String> paths = data.getStringArrayListExtra("data");
				boolean isConfirm = data.getBooleanExtra("isConfirm", false);
				ArrayList<String> removeLists = data.getStringArrayListExtra("removeLists"); //在预览页被取消掉的图片集合
				if(isConfirm){ //如果点击的是确定按钮则关闭该界面，将数据返回到发送动态的界面；否则(返回按钮)遍历相册列表，将预览页取消掉的图片在相册列表上的状态做改变
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, paths);	
					setResult(RESULT_OK, intent);
					finish();
				} else{
			 		if(removeLists != null && removeLists.size() > 0){
						for(int i=0; i<removeLists.size(); i++){
							String path = removeLists.get(i);
							for(int j=0; j<selectedPhotos.size(); j++){
								String filePath = selectedPhotos.get(j).filePath;
								if(filePath != null && filePath.equals(path)){
									selectedPhotos.remove(j); //去掉选中的图片集合中的数据
								}
							}
							
							for(int x=0; x<list.size(); x++){
								String photoFilePath = list.get(x).photoFilePath;
								if(photoFilePath != null && photoFilePath.equals(path)){
									list.get(x).isSelected = false; //改变取消了的图片的选择状态
								}
							}
						}
					}
					String tip=this.getString(R.string.ok);
					tip += "("+selectedPhotos.size()+"/"+maxSelectedNum+")";
					nextStep.setText(tip);
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	@Override
	public int getSelectNum() {
		// TODO Auto-generated method stub
		return selectedPhotos.size();
	}
}