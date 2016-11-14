package chat.image.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.util.file.FileOpreation;
import chat.common.util.output.ShowUtil;
import chat.image.IMImageLoader;
import chat.image.photo.utils.PickPhotoUtils;
import chat.view.HackyViewPager;

/**
 * 
 * @ClassName: ShowDynamicPhoto
 * @Description: 查看动态图片/发动态查看图片
 */
public class ShowDynamicPhoto extends BaseActivity implements
		OnClickListener {
	public final String TAG = getClass().getSimpleName();
	private HackyViewPager mViewPager;
	private List<MxPhotoItem> mDataList = new ArrayList<MxPhotoItem>();
	private MyViewPagerAdapter mAdapter;
	private int mCurrent = 0;
	private TextView cur_sum;
	private ImageView selected;
	private Button btn_save;
	private boolean isLocPhoto = false;
	private EdgeEffectCompat leftEdge;
	private EdgeEffectCompat rightEdge;
	private IMImageLoader imageLoader=null;
	private SaveFileTask mSaveFileTask ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentParams();
		setContentView(R.layout.activity_show_dynamic_image);
		initEvents();
		if (imageLoader==null){
			imageLoader = new IMImageLoader();
			imageLoader.initConfig(this,0);
		}
		initData();
	}

	private void getIntentParams() {
		Intent intent = this.getIntent();
		if (intent != null) {
			isLocPhoto = intent.getBooleanExtra("loc_photo", false);
			mCurrent = intent.getIntExtra("pos", 0);
			ArrayList<String> list = intent.getStringArrayListExtra("data");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					MxPhotoItem item = new MxPhotoItem();
					if (isLocPhoto) {
						item.locPath = list.get(i);
					} else {
						item.url = list.get(i);
					}
					mDataList.add(item);
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDataList!=null){
			mDataList.clear();
			mDataList = null;
		}
		if (imageLoader!=null){
			imageLoader.clearMemory();
			imageLoader = null;
		}

		if(mSaveFileTask != null){
			mSaveFileTask.cancel(true);
			mSaveFileTask = null ;
		}

		System.gc();
	}
	@Override
	protected void initData() {
		mAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),
				mDataList);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(mCurrent);
		mAdapter.notifyDataSetChanged();
		if (mDataList.size() > 0) {
			cur_sum.setText("" + (mCurrent + 1) + "/" + mDataList.size());
		} else {
			cur_sum.setText("");
		}

		if (isLocPhoto) {
			selected.setVisibility(View.VISIBLE);
			cur_sum.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.GONE);
		} else {
			selected.setVisibility(View.GONE);
			cur_sum.setVisibility(View.GONE);
			btn_save.setVisibility(View.VISIBLE);
		}
	}

	protected void initView() {
		mViewPager = (HackyViewPager) findViewById(R.id.showphoto_viewpager);
		selected = (ImageView) findViewById(R.id.selected);
		btn_save = (Button) findViewById(R.id.btn_save);
		cur_sum = (TextView) this.findViewById(R.id.cur_sum);
		initViewPager();
	}

	@Override
	protected void initEvents() {
		selected.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				mCurrent = arg0;
				if (mDataList.size() > 0) {
					cur_sum.setText("" + (mCurrent + 1) + "/"
							+ mDataList.size());
				} else {
					cur_sum.setText("");
				}
				changeCurPhotoSelectState();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (leftEdge != null && rightEdge != null) {
					leftEdge.finish();
					rightEdge.finish();
					leftEdge.setSize(0, 0);
					rightEdge.setSize(0, 0);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			returnDatas();
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}

	public void returnDatas() {
		if (isLocPhoto) {
			Intent intent = new Intent();
			ArrayList<String> data = new ArrayList<String>();
			for (int i = 0; i < mDataList.size(); i++) {
				if (mDataList.get(i).selected) {
					data.add(mDataList.get(i).locPath);
				}
			}
			intent.putStringArrayListExtra("data", data);
			setResult(RESULT_OK, intent);
		}
		finish();
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.btn_save) {
			mSaveFileTask = new SaveFileTask();
			mSaveFileTask.execute();

		} else if (i == R.id.selected) {
			if (mCurrent < mDataList.size()) {
				mDataList.get(mCurrent).selected = !mDataList.get(mCurrent).selected;
				changeCurPhotoSelectState();
			}

		}
	}

	private void changeCurPhotoSelectState() {
		if (mCurrent < mDataList.size()) {
			if (mDataList.get(mCurrent).selected) {
				selected.setImageResource(R.drawable.selector_checkbox_checked);
			} else {
				selected.setImageResource(R.drawable.selector_checkbox_normal);
			}
		}
	}

	private class SaveFileTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowDynamicPhoto.this.showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			boolean suc = saveCurPhoto();
			return suc ? "success" : "failed";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ShowDynamicPhoto.this.dissmisLoading();
			ShowUtil.showToast(
					ShowDynamicPhoto.this,
					(result.equals("success")) ? getString(R.string.chat_photo_save_succ)
							: getString(R.string.chat_photo_save_failed));
		}
	}

	private boolean saveCurPhoto() {
		boolean suc = false;
		if (mCurrent <= mDataList.size()) {
			/**
			 * 获取sd卡的根目录
			 */
			String path = Environment.getExternalStorageDirectory().toString()
					+ "/Moxian";
			/**
			 * 判断sd卡中是否存在Moxian这个文件夹
			 */
			File path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			File file = new File(path1, System.currentTimeMillis() + ".jpg");

			int current = mDataList.size() > 1 ? mCurrent : 0;
			File temp = new File(mDataList.get(current).url);
			if (/*temp == null || */!temp.exists()) {
				temp = IMClient.getInstance().findInImageLoaderCache(
						IMImageLoader.convateUrl(mDataList.get(current).url));
			}
			if (temp != null) {
				if (temp.exists()) {
					suc = FileOpreation.copyFile(temp.getAbsolutePath(),
							file.getAbsolutePath());
					PickPhotoUtils.getInstance().takeResult(this, null, file);
				}
			}

			/**
			 * 刷新文件夹
			 */
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.fromFile(file);
			intent.setData(uri);
			this.sendBroadcast(intent);
		}
		return suc;
	}

	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		private List<MxPhotoItem> paths;

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyViewPagerAdapter(FragmentManager fm, List<MxPhotoItem> paths) {
			super(fm);
			this.paths = paths;
		}

		@Override
		public Fragment getItem(int position) {
			FmAvatar f = new FmAvatar();
			f.setImageLoader(imageLoader);
			return f;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			FmAvatar mfragment1 = (FmAvatar) super.instantiateItem(container,
					position);
			mfragment1.setType(0);
			if (paths.get(position).locPath != null
					&& paths.get(position).locPath.length() > 0) {
				mfragment1.setImageUrl(paths.get(position).locPath);
			} else {
				mfragment1.setImageUrl(paths.get(position).url);
			}
			return mfragment1;
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public int getCount() {
			return paths.size();
		}
	}

	public class MxPhotoItem implements Serializable {
		private static final long serialVersionUID = -3631155261755673488L;
		public boolean selected = true;
		public String locPath;
		public String url;
	}

	private void initViewPager() {
		try {
			Field leftEdgeField = mViewPager.getClass().getDeclaredField(
					"mLeftEdge");
			Field rightEdgeField = mViewPager.getClass().getDeclaredField(
					"mRightEdge");
			if (leftEdgeField != null && rightEdgeField != null) {
				leftEdgeField.setAccessible(true);
				rightEdgeField.setAccessible(true);
				leftEdge = (EdgeEffectCompat) leftEdgeField.get(mViewPager);
				rightEdge = (EdgeEffectCompat) rightEdgeField.get(mViewPager);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}