package chat.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.imlibrary.R;

import java.util.Arrays;
import java.util.List;


/**
 * ActionSheet
 */
public class ActionSheet extends Dialog implements OnClickListener {
	/* 控件的id */
	public static final int CANCEL_BUTTON_ID = 100;
	private static final int BG_VIEW_ID = 10;
	private static final int TRANSLATE_DURATION = 300;
	private static final int ALPHA_DURATION = 300;
	public static final int LOGIN = 100;
	private Context mContext;
	private Attributes mAttrs;
	private MenuItemClickListener mListener;
	private View mView;
	private LinearLayout mPanel;
	private View mBg;
	private List<String> items;
	/** 已经选中项目 */
	private String currentItem = "";
	private String cancelTitle = "";
	private boolean mCancelableOnTouchOutside;
	private boolean mDismissed = true;
	private boolean isCancel = true;
	/** 动作表选中项颜色 */
	private int mMainColor = Color.BLACK;
	/** 未选中项颜色 */
	private int mSecondColor = Color.BLACK;
	private int mTag;
	/** 当前选中行从0开始 */
	private int mCurrentPostion;

	public void setmTag(int mTag) {
		this.mTag = mTag;
	}

	public ActionSheet(Context context) {
		super(context, R.style.ActionSheetStyleIOS7);// 全屏
		this.mContext = context;
		initViews();
		getWindow().setGravity(Gravity.BOTTOM);
		Drawable drawable = new ColorDrawable();
		drawable.setAlpha(0);// 去除黑色背景
		getWindow().setBackgroundDrawable(drawable);
	}
	public ActionSheet(Context context, int mainColor, int secondColor) {
		this(context);
		this.mMainColor = mainColor;
		this.mSecondColor = secondColor;
	}

	public void initViews() {
		/* 隐藏软键盘 */
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			View focusView = ((Activity) mContext).getCurrentFocus();
			if (focusView != null)
				imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
		}
		mAttrs = readAttribute();// 获取主题属性
		mView = createView();
		mBg.startAnimation(createAlphaInAnimation());
		mPanel.startAnimation(createTranslationInAnimation());
	}

	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(TRANSLATE_DURATION);
		return an;
	}

	private Animation createAlphaInAnimation() {
		AlphaAnimation an = new AlphaAnimation(0, 1);
		an.setDuration(ALPHA_DURATION);
		return an;
	}

	private Animation createTranslationOutAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				0, type, 1);
		an.setDuration(TRANSLATE_DURATION);
		an.setFillAfter(true);
		return an;
	}

	private Animation createAlphaOutAnimation() {
		AlphaAnimation an = new AlphaAnimation(1, 0);
		an.setDuration(ALPHA_DURATION);
		an.setFillAfter(true);
		return an;
	}

	/**
	 * 创建基本的背景视图
	 */
	private View createView() {
		FrameLayout parent = new FrameLayout(mContext);
		FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		parentParams.gravity = Gravity.BOTTOM;
		parent.setLayoutParams(parentParams);
		mBg = new View(mContext);
		mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mBg.setBackgroundColor(Color.argb(136, 0, 0, 0));
        int id=BG_VIEW_ID;
		mBg.setId(id);
		mBg.setOnClickListener(this);

		mPanel = new LinearLayout(mContext);
		FrameLayout.LayoutParams mPanelParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		mPanelParams.gravity = Gravity.BOTTOM;
		mPanel.setLayoutParams(mPanelParams);
		mPanel.setOrientation(LinearLayout.VERTICAL);
		parent.addView(mBg);
		parent.addView(mPanel);
		return parent;
	}

	/**
	 * 创建MenuItem
	 */
	private void createItems() {
		if (items != null && items.size() > 0)
			for (int i = 0; i < items.size(); i++) {
				Button bt = new Button(mContext);
				bt.setId(CANCEL_BUTTON_ID + i + 1);
				bt.setOnClickListener(this);
				bt.setBackgroundDrawable(getOtherButtonBg(
						items.toArray(new String[items.size()]), i));
				bt.setText(items.get(i));
				switch (mTag) {
				case LOGIN:
					if (mCurrentPostion == i) {
						bt.setTextColor(mMainColor);
					} else {
						bt.setTextColor(mSecondColor);
					}
					break;

				default:
					if (currentItem.length() > 0
							&& items.get(i).equals(currentItem))
						bt.setTextColor(mContext.getResources().getColor(
								R.color.main_color));
					else
						bt.setTextColor(mAttrs.otherButtonTextColor);

					break;
				}
				bt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mAttrs.actionSheetTextSize);
				if (i > 0) {
					LinearLayout.LayoutParams params = createButtonLayoutParams();
					params.topMargin = mAttrs.otherButtonSpacing;
					mPanel.addView(bt, params);
				} else
					mPanel.addView(bt);
			}
		Button bt = new Button(mContext);// 取消按钮
		bt.getPaint().setFakeBoldText(true);
		bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
        int id=CANCEL_BUTTON_ID;
		bt.setId(id);
		bt.setBackgroundDrawable(mAttrs.cancelButtonBackground);
		bt.setText(cancelTitle);
		bt.setTextColor(mAttrs.cancelButtonTextColor);
		if (mTag ==LOGIN)
			bt.setTextColor(mSecondColor);
		bt.setOnClickListener(this);
		LinearLayout.LayoutParams params = createButtonLayoutParams();
		params.topMargin = mAttrs.cancelButtonMarginTop;
		mPanel.addView(bt, params);

		mPanel.setBackgroundDrawable(mAttrs.background);
		mPanel.setPadding(mAttrs.padding, mAttrs.padding, mAttrs.padding,
				mAttrs.padding);
	}

	public LinearLayout.LayoutParams createButtonLayoutParams() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		return params;
	}

	/**
	 * item按钮的颜色
	 * 
	 * @param titles
	 * @param i
	 * @return
	 */
	private Drawable getOtherButtonBg(String[] titles, int i) {
		if (titles.length == 1)
			return mAttrs.otherButtonSingleBackground;
		else if (titles.length == 2)
			switch (i) {
			case 0:
				return mAttrs.otherButtonTopBackground;
			case 1:
				return mAttrs.otherButtonBottomBackground;
			}
		else if (titles.length > 2) {
			if (i == 0)
				return mAttrs.otherButtonTopBackground;
			else if (i == (titles.length - 1))
				return mAttrs.otherButtonBottomBackground;
			return mAttrs.getOtherButtonMiddleBackground();
		}
		return null;
	}

	public void showMenu() {
		if (!mDismissed)
			return;
		show();
		getWindow().setContentView(mView);
		mDismissed = false;
	}

	/**
	 * dissmiss Menu菜单
	 */
	public void dismissMenu() {
		if (mDismissed)
			return;
		dismiss();
		onDismiss();
		mDismissed = true;
	}

	/**
	 * dismiss时的处理
	 */
	private void onDismiss() {
		mPanel.startAnimation(createTranslationOutAnimation());
		mBg.startAnimation(createAlphaOutAnimation());
	}

	/**
	 * 取消按钮的标题文字
	 * 
	 * @param title
	 * @return
	 */
	public ActionSheet setCancelButtonTitle(String title) {
		this.cancelTitle = title;
		return this;
	}

	/**
	 * 取消按钮的标题文字
	 * 
	 * @param strId
	 * @return
	 */
	public ActionSheet setCancelButtonTitle(int strId) {
		return setCancelButtonTitle(mContext.getString(strId));
	}

	/**
	 * 点击外部边缘是否可取消
	 * 
	 * @param cancelable
	 * @return
	 */
	public ActionSheet setCancelableOnTouchMenuOutside(boolean cancelable) {
		mCancelableOnTouchOutside = cancelable;
		return this;
	}

	public ActionSheet addItems(String... titles) {
		if (titles == null || titles.length == 0)
			return this;
		items = Arrays.asList(titles);
		createItems();
		return this;
	}
	
	@Deprecated
	public void setCurrentItems(String currentItem) {
		if (currentItem == null || currentItem.length() == 0)
			return;
		this.currentItem = currentItem;
	}

	public ActionSheet setItemClickListener(MenuItemClickListener listener) {
		this.mListener = listener;
		return this;
	}

	private Attributes readAttribute() {
		Attributes attrs = new Attributes(mContext);
		TypedArray a = mContext.getTheme().obtainStyledAttributes(null,
				R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
		Drawable background = a
				.getDrawable(R.styleable.ActionSheet_actionSheetBackground);
		if (background != null)
			attrs.background = background;
		Drawable cancelButtonBackground = a
				.getDrawable(R.styleable.ActionSheet_cancelButtonBackground);
		if (cancelButtonBackground != null)
			attrs.cancelButtonBackground = cancelButtonBackground;
		Drawable otherButtonTopBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonTopBackground);
		if (otherButtonTopBackground != null)
			attrs.otherButtonTopBackground = otherButtonTopBackground;
		Drawable otherButtonMiddleBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
		if (otherButtonMiddleBackground != null)
			attrs.otherButtonMiddleBackground = otherButtonMiddleBackground;
		Drawable otherButtonBottomBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonBottomBackground);
		if (otherButtonBottomBackground != null)
			attrs.otherButtonBottomBackground = otherButtonBottomBackground;
		Drawable otherButtonSingleBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonSingleBackground);
		if (otherButtonSingleBackground != null)
			attrs.otherButtonSingleBackground = otherButtonSingleBackground;
		attrs.cancelButtonTextColor = a.getColor(
				R.styleable.ActionSheet_cancelButtonTextColor,
				attrs.cancelButtonTextColor);
		attrs.otherButtonTextColor = a.getColor(
				R.styleable.ActionSheet_otherButtonTextColor,
				attrs.otherButtonTextColor);
		attrs.padding = (int) a.getDimension(
				R.styleable.ActionSheet_actionSheetPadding, attrs.padding);
		attrs.otherButtonSpacing = (int) a.getDimension(
				R.styleable.ActionSheet_otherButtonSpacing,
				attrs.otherButtonSpacing);
		attrs.cancelButtonMarginTop = (int) a.getDimension(
				R.styleable.ActionSheet_cancelButtonMarginTop,
				attrs.cancelButtonMarginTop);
		attrs.actionSheetTextSize = a.getDimensionPixelSize(
				R.styleable.ActionSheet_actionSheetTextSize,
				(int) attrs.actionSheetTextSize);

		a.recycle();
		return attrs;
	}

	@Override
	public void onClick(View v) {
		int id= v.getId();
		if (id == BG_VIEW_ID && !mCancelableOnTouchOutside)
			return;
		dismissMenu();
		if (id != CANCEL_BUTTON_ID && id != BG_VIEW_ID) {
			if (mListener != null)
				mListener.onActionSheetItemClick(v.getId() - CANCEL_BUTTON_ID
						- 1);
			isCancel = false;
		}
		if (id == CANCEL_BUTTON_ID) {
			if (mListener != null)
				mListener.onActionSheetItemClick(CANCEL_BUTTON_ID);
			isCancel = true;
		}
	}

	/**
	 * 自定义属性的控件主题
	 */
	private class Attributes {
		private Context mContext;

		private Drawable background;
		private Drawable cancelButtonBackground;
		private Drawable otherButtonTopBackground;
		private Drawable otherButtonMiddleBackground;
		private Drawable otherButtonBottomBackground;
		private Drawable otherButtonSingleBackground;
		private int cancelButtonTextColor;
		private int otherButtonTextColor;
		private int padding;
		private int otherButtonSpacing;
		private int cancelButtonMarginTop;
		private float actionSheetTextSize;

		public Attributes(Context context) {
			mContext = context;
			this.background = new ColorDrawable(Color.TRANSPARENT);
			this.cancelButtonBackground = new ColorDrawable(Color.BLACK);
			ColorDrawable gray = new ColorDrawable(Color.GRAY);
			this.otherButtonTopBackground = gray;
			this.otherButtonMiddleBackground = gray;
			this.otherButtonBottomBackground = gray;
			this.otherButtonSingleBackground = gray;
			this.cancelButtonTextColor = Color.WHITE;
			this.otherButtonTextColor = Color.BLACK;
			this.padding = dp2px(20);
			this.otherButtonSpacing = dp2px(2);
			this.cancelButtonMarginTop = dp2px(10);
			this.actionSheetTextSize = dp2px(16);
		}

		private int dp2px(int dp) {
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					dp, mContext.getResources().getDisplayMetrics());
		}

		public Drawable getOtherButtonMiddleBackground() {
			if (otherButtonMiddleBackground instanceof StateListDrawable) {
				TypedArray a = mContext.getTheme().obtainStyledAttributes(null,
						R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
				otherButtonMiddleBackground = a
						.getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
				a.recycle();
			}
			return otherButtonMiddleBackground;
		}

	}

	public static interface MenuItemClickListener {
		void onActionSheetItemClick(int itemPosition);
	}

	public void setCurrentPosition(int currentPostion) {
		mCurrentPostion = currentPostion;

	}

}