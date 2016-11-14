package chat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imlibrary.R;

/**
 * 
 * 自定义设置子项控件 SettingItemView
 * @author weyko 用于管理每个设置项
 */

public class SettingItemView extends LinearLayout {
	private RelativeLayout bg_item_setting;//背景控件
	private TextView mTitleView;// 标题控件
	private TextView mRightView;// 右边提示控件
	private CheckBox setting_onoff;// 右边开关
	private String mTitle;// 标题内容
	private String mRight;// 右边提示内容
	private int mDrawableLeft;// 左边图标
	private int mDrawableRight;// 右边图标
	private boolean mIsEmpty;// 设置是否为空白
	private boolean mIsOpen;// 设置是否打开开关
	private OnSwitchListener onSwitchListener;// 开关监听接口

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			initView(context, attrs);
		} catch (Exception e) {
		}
	}

	/**
	 * 初始化视图、控件
	 * 
	 * @param context
	 * @param attrs
	 */
	@SuppressLint("NewApi")
	private void initView(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.item_setting, this, true);// 填充内容视图
		bg_item_setting= (RelativeLayout) this.findViewById(R.id.bg_item_setting);
		mTitleView = (TextView) this.findViewById(R.id.setting_item_title);
		mRightView = (TextView) this.findViewById(R.id.setting_item_right);
		setting_onoff = (CheckBox) this.findViewById(R.id.setting_onoff);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SettingItemView);// 获取属性集合
		if (a == null || mTitleView == null)
			return;
		mTitleView.setText(a.getString(R.styleable.SettingItemView_title));// 获取控件属性初始化设置的值
		mRightView.setText(a.getString(R.styleable.SettingItemView_hint));// 获取控件属性初始化设置的值
		mDrawableRight = a.getResourceId(
				R.styleable.SettingItemView_drawable_right, -1);
		mIsEmpty = a.getBoolean(R.styleable.SettingItemView_empty, false);
		mIsOpen = a.getBoolean(R.styleable.SettingItemView_on_off, false);
		if (mIsEmpty) {
			mDrawableRight = 0;
		}
		if (mIsOpen) {
			mDrawableRight = 0;
			setting_onoff.setVisibility(View.VISIBLE);
			setting_onoff.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (onSwitchListener != null)
						onSwitchListener.onSwitchChanger(
								SettingItemView.this, setting_onoff.isChecked());
				}
			});
		}
		if (mDrawableRight > -1)
			mRightView.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft,
					0, mDrawableRight, 0);
		a.recycle();
	}

	/**
	 * 设置背景
	 */
	public void setBackground(int resid) {
		bg_item_setting.setBackgroundResource(resid);
	}
	/**
	 * 设置背景颜色
	 */
	public void setBackgroundWithColor(int color) {
		bg_item_setting.setBackgroundColor(color);
	}
	/**
	 * 设置标题
	 *
	 * @param title
	 */
	public void setTitle(String title) {
		mTitle = title;
		mTitleView.setText(mTitle);
	}

	/**
	 * 设置右边提示文字
	 * 
	 * @param right
	 */
	public void setRight(String right) {
		mRight = right;
		mRightView.setText(mRight);
	}
	/**
	* @Title: getRightText 
	* @param: 
	* @Description:  获取右边文字
	* @return String
	 */
	public String getRightText() {
		if (mRightView != null) {
			return mRightView.getText().toString();
		}
		return "";
	}

	/**
	 * 设置右边drawable
	 * 
	 * @param drawableRight
	 */
	public void setDrawableRight(int drawableRight) {
		mRightView.setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, 0,
				drawableRight, 0);
	}

	/**
	 * 设置左边drawable
	 * 
	 * @param drawableLeft
	 */
	public void setDrawableLeft(int drawableLeft) {
		mRightView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0,
				mDrawableRight, 0);
	}

	/**
	 * 设置文字对齐方向
	 */
	public void setItemTextGravity(int gravity) {
		mRightView.setGravity(Gravity.CENTER_VERTICAL | gravity);
	}

	/**
	 * 设置文本大小为内容模式大小
	 */
	public void setTextSizeWithContMode() {
		mRightView.setTextSize(getResources().getDimension(
				R.dimen.text_size_title_btn));
	}

	/**
	 * 设置文本大小为提示模式大小
	 */
	public void setTextSizeWithToastMode() {
		mRightView.setTextSize(getResources().getDimension(
				R.dimen.text_size_toast));
	}

	/**
	 * 设置右边文字字体颜色
	 * 
	 * @param color
	 */
	public void setItemTextColor(int color) {
		if (color < 0)
			color = 0;
		mRightView.setTextColor(color);
	}

	public void setChecked(boolean isChecked) {
		if (setting_onoff != null)
			setting_onoff.setChecked(isChecked);
	}

	/**
	 * @Title: setSwithchEnable
	 * @param:
	 * @Description: 设置开关是否可用
	 * @return void
	 */
	public void setSwithchEnable(boolean isEnable) {
		setting_onoff.setEnabled(isEnable);
	}

	/**
	 * @Title: setOnSwitchListener
	 * @param:
	 * @Description: 设置开关监听接口
	 * @return void
	 */
	public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
		this.onSwitchListener = onSwitchListener;
	}

	/**
	 * @ClassName: OnSwitchListener
	 * @Description: 监听开关变换接口
	 * @author weyko zhong.xiwei@moxiangroup.com
	 * @Company moxian
	 * @date 2015年8月8日 上午10:54:21
	 *
	 */
	public interface OnSwitchListener {
		public void onSwitchChanger(View view, boolean isOpen);
	}
}
