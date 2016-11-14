package chat.image.selectpic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.AvatarBean;
import chat.contact.bean.UserBean;
import chat.image.IMImageLoader;
import chat.image.activity.FmAvatar;
import chat.view.HackyViewPager;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

public class ShowPhotosActivity extends BaseActivity implements
		OnPageChangeListener, OnClickListener {

	private final static int CHANGE_IMAGE = 100;
	private HackyViewPager m_vp;
	private MyViewPagerAdapter myAdapter;
	private List<AvatarBean> avatarList = new ArrayList<AvatarBean>();
	private TextView imgs_tv, changeImage_tv, setDefault_tv;
	private ImageButton btnDown;// 删除
	private int currentItem;
	private int avatarDefaultIndex = -1;
	private boolean canEditAvatarPhoto = false;
	/** add by weyko 2015.8.15 更改头像时返回通知 */
	private boolean hasChanged = false;
	private String roomId;// 群id
	private String avatarUrl;
	private boolean destory=false;
	private IMImageLoader imageLoader=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentParams();
		if (avatarList.size() <= 0) {
			back();
			return;
		}
		setContentView(R.layout.activity_show_photos);
		initEvents();
		if (imageLoader==null){
			imageLoader = new IMImageLoader();
			imageLoader.initConfig(this,0);
		}
		initData();
		destory = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (avatarList!=null){
			avatarList.clear();
			avatarList = null;
		}
		if (imageLoader!=null){
			imageLoader.clearMemory();
			imageLoader = null;
		}
		destory = true;
		System.gc();
	}
	@Override
	protected void initData() {
		refreshAvatarDefaultIndex(avatarList);
		if (avatarList.size() <= 1) {
			btnDown.setVisibility(View.INVISIBLE);
		}
		if (currentItem == avatarDefaultIndex) {
			setDefault_tv.setVisibility(View.INVISIBLE);
		} else {
			changeImage_tv.setVisibility(View.VISIBLE);
			setDefault_tv.setVisibility(View.VISIBLE);
			btnDown.setVisibility(View.VISIBLE);
		}
		imgs_tv.setText((currentItem + 1) + "/" + avatarList.size());
		// TODO
		if (avatarDefaultIndex < 0) {
			setAvatarList(avatarDefaultIndex);
		}
		setMyAdapter();
		// 查看别的的头像 隐藏所有编辑操作
		if (!canEditAvatarPhoto) {
			changeImage_tv.setVisibility(View.GONE);
			setDefault_tv.setVisibility(View.GONE);
			btnDown.setVisibility(View.GONE);
		}
		/** add by weyko 2015.8.11 处理只有一张图片时显示样式 */
		if (avatarList != null && avatarList.size() == 1) {
			setDefault_tv.setVisibility(View.GONE);
			imgs_tv.setVisibility(View.GONE);
			if (!android.text.TextUtils.isEmpty(roomId)) {
				btnDown.setVisibility(View.GONE);
			}
			LayoutParams params = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			changeImage_tv.setLayoutParams(params);
		}
	}

	private void getIntentParams() {
		Intent intent = getIntent();
		canEditAvatarPhoto = intent.getBooleanExtra("canEditAvatar", false);
		currentItem = intent.getIntExtra("currentItem", 0);
		roomId = intent.getStringExtra("roomId");
		try {
			UserBean mBean = (UserBean) intent.getSerializableExtra("mBean");
			if (mBean != null && mBean.getData().getUserAvatarList().size() > 0) {
				avatarList.addAll(mBean.getData().getUserAvatarList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshAvatarDefaultIndex(List<AvatarBean> avatarList) {
		for (int i = 0; i < avatarList.size(); i++) {
			if (avatarList.get(i).getIsAvatar() == 1) {
				avatarDefaultIndex = i;
				Log.i("got", "index:" + avatarDefaultIndex);
				break;
			}
		}
	}

	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		private List<AvatarBean> avatarList;

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyViewPagerAdapter(FragmentManager fm,
				List<AvatarBean> avatarList) {
			super(fm);
			this.avatarList = avatarList;
		}

		@Override
		public Fragment getItem(int position) {
			 FmAvatar f = new  FmAvatar();
			f.setImageLoader(imageLoader);
			return f;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			FmAvatar mfragment1 = (FmAvatar) super.instantiateItem(container,
					position);
			mfragment1.setImageUrl(avatarList.get(position).getAvatarUrl());
			Log.i("img", "img" + avatarList.get(position).getAvatarUrl());
			return mfragment1;
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public int getCount() {
			return avatarList.size();
		}
	}

	@Override
	protected void initView() {
		m_vp = (HackyViewPager) findViewById(R.id.viewpager);
		imgs_tv = (TextView) findViewById(R.id.imgs_tv);
		changeImage_tv = (TextView) findViewById(R.id.changeImage_tv);
		setDefault_tv = (TextView) findViewById(R.id.setDefault_tv);
		btnDown = (ImageButton) findViewById(R.id.btnDown);
	}

	/**
	 * 注册View 事件
	 */
	@Override
	protected void initEvents() {
		// T Auto-generated method stub
		m_vp.setOnPageChangeListener(this);
		changeImage_tv.setClickable(true);
		setDefault_tv.setClickable(true);
		changeImage_tv.setOnClickListener(this);
		setDefault_tv.setOnClickListener(this);
		btnDown.setOnClickListener(this);
		findViewById(R.id.imgs_operator).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.changeImage_tv) {
			Intent upload_photo = new Intent(this,UploadAvatarActivity.class);
			upload_photo.putExtra("isGroup", roomId != null);
			upload_photo.putExtra("avatarId", avatarList.get(currentItem)
					.getId());
			startActivityForResult(upload_photo, CHANGE_IMAGE);

		} else if (i == R.id.setDefault_tv) {
			setAvatar(currentItem);

		} else if (i == R.id.btnDown) {
			if (currentItem < avatarList.size()) {
				AvatarBean bean = avatarList.get(currentItem);
				if (bean.getIsAvatar() == 1) {// 当前项为用户头像
					int nextIndex = currentItem + 1;
					if (nextIndex >= avatarList.size()) {
						nextIndex = 0;
					}
					if (avatarList.size() > 1) {
						setAvatar(nextIndex, true, currentItem);
					}
				} else {
					if (avatarList.size() > 1) {
						deleteImage(currentItem);
					}
				}
			}

		} else {
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		// Auto-generated method stub
		currentItem = arg0;
		System.out.println("-------------onPageSelected currentItem="
				+ currentItem);
		arg0++;
		Log.i("got", "currentItem:" + currentItem);
		Log.i("got", "avatarDefaultIndex:" + avatarDefaultIndex);
		imgs_tv.setText(arg0 + "/" + avatarList.size());
		if (canEditAvatarPhoto) {
			if (currentItem == avatarDefaultIndex) {
				setDefault_tv.setVisibility(View.GONE);
				if (avatarList.size() <= 1)
					btnDown.setVisibility(View.GONE);

			} else {
				setDefault_tv.setVisibility(View.VISIBLE);
				btnDown.setVisibility(View.VISIBLE);
			}
			if (avatarList.size() == 1) {
				btnDown.setVisibility(View.INVISIBLE);
			} else {
				btnDown.setVisibility(View.VISIBLE);
			}
		}
	}

	private void deleteImage(final int index) {
		this.showLoading();
		WBaseModel<WBaseBean> model = new WBaseModel<WBaseBean>(this,
				WBaseBean.class);
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("avatarId", avatarList.get(index).getId());
		mParams.put("userId", IMClient.getInstance().getSSOUserId());
		model.httpJsonRequest(Method.DELETE,
				URLConfig.AVATAR_DALETE, mParams,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						if(destory){
							return;
						}
						ShowPhotosActivity.this.dissmisLoading();
						if (httpStatusCode != -1) {
							if (data != null && data instanceof WBaseBean) {
								WBaseBean baseBean = (WBaseBean) data;
								if (baseBean.isResult()) {
									avatarList.remove(index);
									if (currentItem >= avatarList.size()) {
										currentItem = avatarList.size() - 1;
									}
									imgs_tv.setText((currentItem + 1) + "/"
											+ avatarList.size());
									refreshAvatarDefaultIndex(avatarList);
									m_vp.getAdapter().notifyDataSetChanged();
									myAdapter.notifyDataSetChanged();
									if (currentItem == avatarDefaultIndex) {
										setDefault_tv.setVisibility(View.GONE);
										if (avatarList.size() == 1)
											btnDown.setVisibility(View.GONE);
									} else {
										setDefault_tv
												.setVisibility(View.VISIBLE);
										btnDown.setVisibility(View.VISIBLE);
									}
									 ShowUtil.showToast(ShowPhotosActivity.this,
											R.string.preson_info_delete_sucess);
								} else {
									showResutToast(baseBean.getCode());
								}
							} else {
								mToastor.showToast(R.string.server_error);
							}
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == CHANGE_IMAGE) {
				String newAvatarUrl = data.getStringExtra("avatarUrl");
				avatarList.get(currentItem).setAvatarUrl(newAvatarUrl);
				m_vp.getAdapter().notifyDataSetChanged();
				avatarUrl = newAvatarUrl;
				hasChanged = true;
			}
		}

	}

	private void setAvatar(final int index) {
		setAvatar(index, false, 0);
	}

	private void setAvatar(final int index, final boolean delete,
			final int deleteItem) {
		showLoading();
		WBaseModel<WBaseBean> model = new WBaseModel<WBaseBean>(this,
				WBaseBean.class);
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("avatarId", avatarList.get(index).getId());
		mParams.put("userId", IMClient.getInstance().getSSOUserId());
		model.httpJsonRequest(Method.POST,
				URLConfig.SET_AVATAR, mParams,
				new WRequestCallBack() {

					@Override
					public void receive(int httpStatusCode, Object data) {
						if(destory){
							return;
						}
						if (httpStatusCode == 200) {
							if (data != null && data instanceof WBaseBean) {
								WBaseBean baseBean = (WBaseBean) data;
								if (baseBean.isResult()) {
									setAvatarList(index);
									if (delete) {
										deleteImage(deleteItem);
										myAdapter.notifyDataSetChanged();
										m_vp.getAdapter().notifyDataSetChanged();
										setDefault_tv.setVisibility(View.GONE);
									} else {
										ShowPhotosActivity.this.dissmisLoading();
										ShowUtil.showToast(ShowPhotosActivity.this,R.string.avatar_change_sucess);
										Intent intent = new Intent();
										setResult(RESULT_OK, intent);
										back();
									}
								} else {
									ShowPhotosActivity.this.dissmisLoading();
									showResutToast(baseBean.getCode());
								}
							} else {
								ShowPhotosActivity.this.dissmisLoading();
								mToastor.showToast(R.string.server_error);
							}
						} else {
							ShowPhotosActivity.this.dissmisLoading();
						}
					}
				});
	}

	private void back() {
		this.finish();
	}

	/**
	 * 设置当前那张是默认图
	 * 
	 * @param index
	 */
	private void setAvatarList(final int index) {
		for (int i = 0; i < avatarList.size(); i++) {
			if (index == i) {
				avatarList.get(i).setIsAvatar(1);
			} else {
				avatarList.get(i).setIsAvatar(0);
			}
		}
		avatarDefaultIndex = index;
		Log.i("got", "setAvatarList:avatarDefaultIndex:" + avatarDefaultIndex);
	}

	private void setMyAdapter() {
		myAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),
				avatarList);
		m_vp.setAdapter(myAdapter);
		m_vp.setCurrentItem(currentItem == avatarList.size() ? currentItem--
				: currentItem);
		// 3-20
		myAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasChanged && !android.text.TextUtils.isEmpty(roomId)) {
				Intent intent = new Intent();
				intent.putExtra("avatarUrl", avatarUrl);
				setResult(RESULT_OK, intent);
				this.finish();
			} else {
//				if (avatarDefaultIndex != 0) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
//				}
				back();
			}
		}
		return false;
	}
}