package chat.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.imlibrary.R;
import chat.base.BaseActivity;
import chat.view.HandyTextView;

public class CustomBaseDialog extends Dialog implements
		View.OnClickListener {
	private Context mContext;// 上下文
	private LinearLayout mLayoutRoot;// 总体根布局
	private LinearLayout mLayoutContent;// 内容根布局
	private HandyTextView mHtvMessage;// 内容
	private LinearLayout mLayoutBottom;// 底部根布局
	private Button mBtnButton1;// 底部按钮1
	private Button mBtnButton2;// 底部按钮2

	private static CustomBaseDialog mBaseDialog;// 当前的对话框
	private OnClickListener mOnClickListener1;// 按钮1的单击监听事件
	private OnClickListener mOnClickListener2;// 按钮2的单击监听事件
	private HandyTextView mHtvTitle;

	public CustomBaseDialog(Context context) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		mContext = context;
		setContentView(R.layout.common_dialog);
		initViews();
		initEvents();
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	private void initViews() {
		mLayoutRoot = (LinearLayout) findViewById(R.id.dialog_generic_layout_root);
		mLayoutContent = (LinearLayout) findViewById(R.id.dialog_generic_layout_content);
		mHtvMessage = (HandyTextView) findViewById(R.id.dialog_generic_htv_message);
		mHtvTitle = (HandyTextView) findViewById(R.id.dialog_generic_htv_title);
		mLayoutBottom = (LinearLayout) findViewById(R.id.dialog_generic_layout_bottom);
		mBtnButton1 = (Button) findViewById(R.id.dialog_generic_btn_button1);
		mBtnButton2 = (Button) findViewById(R.id.dialog_generic_btn_button2);
		mLayoutRoot.setVisibility(View.VISIBLE);
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mLayoutRoot.setMinimumWidth(dm.widthPixels - 50);

	}

	private void initEvents() {
		mBtnButton1.setOnClickListener(this);
		mBtnButton2.setOnClickListener(this);
	}

	/**
	 * 填充新布局到内容布局
	 *
	 * @param resource
	 */
	public void setDialogContentView(int resource) {
		View v = LayoutInflater.from(mContext).inflate(resource, null);
		if (mLayoutContent.getChildCount() > 0) {
			mLayoutContent.removeAllViews();
		}
		mLayoutContent.addView(v);
	}

	/**
	 * 填充新布局到内容布局
	 */
	public void setDialogContentView(View view) {
		if (view == null)
			return;
		if (mLayoutContent.getChildCount() > 0) {
			mLayoutContent.removeAllViews();
		}
		mLayoutContent.addView(view);
	}

	public void setLeftButton(String msg,
			View.OnClickListener clickListener) {
		mBtnButton1.setText(msg);
		mBtnButton1.setOnClickListener(clickListener);
	}

	public void setRightButton(String msg,
			View.OnClickListener clickListener) {

		mBtnButton2.setText(msg);
		mBtnButton2.setOnClickListener(clickListener);
	}

	public void setRightButtonColor(int color) {
		if (mBtnButton2 != null) {
			mBtnButton2.setTextColor(color);
		}
	}

	/**
	 * 填充新布局到内容布局
	 *
	 * @param resource
	 * @param params
	 */
	public void setDialogContentView(int resource,
			LinearLayout.LayoutParams params) {
		View v = LayoutInflater.from(mContext).inflate(resource, null);
		if (mLayoutContent.getChildCount() > 0) {
			mLayoutContent.removeAllViews();
		}
		mLayoutContent.addView(v, params);
	}

	public static CustomBaseDialog getDialog(Context context, CharSequence message,
											 CharSequence button1, OnClickListener listener1) {
		mBaseDialog = new CustomBaseDialog(context);
		if (mBaseDialog.buttonIsExist(button1, listener1, null, null)) {
			mBaseDialog.setButton1(button1, listener1);
			mBaseDialog.mBtnButton2.setVisibility(View.GONE);
			mBaseDialog.setButton1Background(R.drawable.bg_button_dialog_3);
		}

		mBaseDialog.setMessage(message);
		mBaseDialog.setCancelable(true);
		mBaseDialog.setCanceledOnTouchOutside(true);
		return mBaseDialog;
	}

	public static CustomBaseDialog getDialog(Context context, CharSequence message,
											 CharSequence button1, OnClickListener listener1,
											 boolean isCancelable) {
		mBaseDialog = new CustomBaseDialog(context);
		if (mBaseDialog.buttonIsExist(button1, listener1, null, null)) {
			mBaseDialog.setButton1(button1, listener1);
			mBaseDialog.mBtnButton2.setVisibility(View.GONE);
			mBaseDialog.setButton1Background(R.drawable.bg_button_dialog_3);
		}

		mBaseDialog.setMessage(message);
		mBaseDialog.setCancelable(isCancelable);
		mBaseDialog.setCanceledOnTouchOutside(isCancelable);
		return mBaseDialog;
	}

	public static CustomBaseDialog getDialog(Context context, CharSequence title,
											 CharSequence message, CharSequence button1,
											 OnClickListener listener1, CharSequence button2,
											 OnClickListener listener2) {
		mBaseDialog = new CustomBaseDialog(context);
		if (mBaseDialog.buttonIsExist(button1, listener1, button2, listener2)) {
			mBaseDialog.setButton1(button1, listener1);
			mBaseDialog.setButton2(button2, listener2);
		}
		mBaseDialog.setTitle(title);
		mBaseDialog.setMessage(message);
		mBaseDialog.setCancelable(true);
		mBaseDialog.setCanceledOnTouchOutside(true);
		return mBaseDialog;
	}

	public static CustomBaseDialog getDialog(Context context, CharSequence title,
											 CharSequence message, CharSequence button1,
											 OnClickListener listener1, CharSequence button2,
											 OnClickListener listener2, boolean isCancelable) {
		mBaseDialog = new CustomBaseDialog(context);
		if (mBaseDialog.buttonIsExist(button1, listener1, button2, listener2)) {
			mBaseDialog.setButton1(button1, listener1);
			mBaseDialog.setButton2(button2, listener2);
		}
		mBaseDialog.setTitle(title);
		mBaseDialog.setMessage(message);
		mBaseDialog.setCancelable(isCancelable);
		mBaseDialog.setCanceledOnTouchOutside(isCancelable);
		return mBaseDialog;
	}

	public static CustomBaseDialog getDialog(Context context, CharSequence message,
											 CharSequence button1, OnClickListener listener1,
											 CharSequence button2, OnClickListener listener2) {
		mBaseDialog = new CustomBaseDialog(context);
		if (mBaseDialog.buttonIsExist(button1, listener1, button2, listener2)) {
			mBaseDialog.setButton1(button1, listener1);
			mBaseDialog.setButton2(button2, listener2);
		}
		// ljl 2015-7-2 18:01 如果没有设置title，则隐藏title
		mBaseDialog.setTitle(null);
		mBaseDialog.setMessage(message);
		mBaseDialog.setCancelable(true);
		mBaseDialog.setCanceledOnTouchOutside(true);
		return mBaseDialog;
	}

	public void setEnable(boolean isEnable) {
		mBaseDialog.setCancelable(isEnable);
		mBaseDialog.setCanceledOnTouchOutside(isEnable);
	}

	public void setTitle(CharSequence text) {
		if (text != null) {
			mHtvTitle.setVisibility(View.VISIBLE);
			mHtvTitle.setText(text);
		} else {
			mHtvTitle.setVisibility(View.GONE);
		}
	}

	public void setMessage(CharSequence text) {
		if (text != null) {
			mLayoutContent.setVisibility(View.VISIBLE);
			mHtvMessage.setText(text);
		} else {
			if (TextUtils.isEmpty(mHtvTitle.getText().toString().trim())) {
				mLayoutContent.setVisibility(View.GONE);
			}
		}
	}

	public void setMessageTextSize(float size) {
		if (size > 0) {
			mHtvMessage.setTextSize(size);
		}
	}

	public boolean buttonIsExist(CharSequence button1,
			OnClickListener listener1, CharSequence button2,
			OnClickListener listener2) {
		if ((button1 != null && listener1 != null)
				|| (button2 != null && listener2 != null)) {
			mLayoutBottom.setVisibility(View.VISIBLE);
			return true;
		} else {
			mLayoutBottom.setVisibility(View.GONE);
			return false;
		}
	}

	public void setButton1(CharSequence text,
			OnClickListener listener) {
		if (text != null && listener != null) {
			mLayoutBottom.setVisibility(View.VISIBLE);
			mBtnButton1.setVisibility(View.VISIBLE);
			mBtnButton1.setText(text);
			mOnClickListener1 = listener;
		} else {
			mBtnButton1.setVisibility(View.GONE);
		}
	}

	public void setButton2(CharSequence text,
			OnClickListener listener) {
		if (text != null && listener != null) {
			mLayoutBottom.setVisibility(View.VISIBLE);
			mBtnButton2.setVisibility(View.VISIBLE);
			mBtnButton2.setText(text);
			mOnClickListener2 = listener;
		} else {
			mBtnButton2.setVisibility(View.GONE);
		}
	}

	public void setButton1Background(int id) {
		mBtnButton1.setBackgroundResource(id);
	}

	public void setButton2Background(int id) {
		mBtnButton2.setBackgroundResource(id);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.dialog_generic_btn_button1) {
			if (mOnClickListener1 != null) {
				mOnClickListener1.onClick(this, 0);
			}

		} else if (i == R.id.dialog_generic_btn_button2) {
			if (mOnClickListener2 != null) {
				mOnClickListener2.onClick(this, 1);
			}

		}
	}

	/**
	 * 获取返回编辑提示对话框
	 * @param context
	 * @param intercface
	 * @return
	 */
	public static CustomBaseDialog getEditbackDialog(
			final BaseActivity context,
			final OnEditContinueListener intercface) {
		CustomBaseDialog mBackDialog = CustomBaseDialog.getDialog(context,
				context.getString(R.string.public_edit_dialog_title),
				context.getString(R.string.public_edit_dialog_cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						intercface.onEditContinue();
					}
				}, context.getString(R.string.public_edit_dialog_continue),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBackDialog.setButton1Background(R.drawable.bg_button_dialog_1);
		mBackDialog.setButton2Background(R.drawable.bg_button_dialog_2);
		return mBackDialog;
	}

	public interface OnEditContinueListener {
		public void onEditContinue();
	}
	
	public static void gc(){ 
		mBaseDialog = null;
	}
}
