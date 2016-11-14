package chat.session.group.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.io.File;
import java.util.HashMap;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.URLConfig;
import chat.common.util.output.ShowUtil;
import chat.dialog.CustomBaseDialog;
import chat.image.DisplayImageConfig;
import chat.session.group.bean.ChatGroupData;
import chat.session.group.bean.GroupQRcodeResetBean;
import chat.session.util.IMTypeUtil;
import chat.view.CircleImageView;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * @ClassName: QuickmarkActivity
 * @Description: 群二维码
 * @author weyko
 */
public class QuickmarkActivity extends BaseActivity implements
		OnClickListener {
	public static final String QRCODE = "qrcode";
	private TextView titleText;
	/** 二维码 */
	private ImageView code_qmark_chat_group;
	/* 头像 */
	private CircleImageView avatar_qmark_chat_group;
	/* 备注 */
	private TextView remark_qmark_chat_group;
	/* 分享 */
	private TextView share_qmark_chat_group;
	/* 重置 */
	private TextView reset_qmark_chat_group;
	/* 群信息 */
	private ChatGroupData groupData;
	private Bitmap qrBmp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quickmark_chat_group);
		getIntentParmas();
		initEvents();
		initData();
	}

	private void getIntentParmas() {
		groupData = (ChatGroupData) getIntent().getSerializableExtra(
				ChatGroupInfoAcitivty.ROOM);
	}
	@Override
	protected void initData() {
		titleText.setText(R.string.chat_group_qmark_title);
		if (groupData != null) {
			IMClient.sImageLoader.displayThumbnailImage(
					groupData.getPhotoUrl(), avatar_qmark_chat_group,
					DisplayImageConfig.avatarGroupOptions,
					DisplayImageConfig.headThumbnailSize,
					DisplayImageConfig.headThumbnailSize,R.drawable.ic_default_group);
			IMClient.getInstance();
			code_qmark_chat_group.setVisibility(View.VISIBLE);
			IMClient.sImageLoader.displayImage(
					// 加载群二维码原图，不使用缓存
					groupData.getQrcodeUrl(), code_qmark_chat_group,
					DisplayImageConfig.QRcodeImageOptions);
			remark_qmark_chat_group.setText(groupData.getGroupName());
			reset_qmark_chat_group
					// 只有群主才有重置二维码功能
					.setVisibility(groupData.getRoleType() == IMTypeUtil.RoleType.OWNERS ? View.VISIBLE
							: View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (qrBmp != null && !qrBmp.isRecycled()) {
			qrBmp.recycle();
		}
		qrBmp = null;
		System.gc();
	}

	@SuppressLint("NewApi")
	@Override
	protected void initView() {
		titleText = (TextView) this.findViewById(R.id.titleText);
		code_qmark_chat_group = (ImageView) this
				.findViewById(R.id.code_qmark_chat_group);
		avatar_qmark_chat_group = (CircleImageView) this
				.findViewById(R.id.avatar_qmark_chat_group);
		remark_qmark_chat_group = (TextView) this
				.findViewById(R.id.remark_qmark_chat_group);
		share_qmark_chat_group = (TextView) this
				.findViewById(R.id.share_qmark_chat_group);
		reset_qmark_chat_group = (TextView) this
				.findViewById(R.id.reset_qmark_chat_group);
		ImageView qrcode_bg = (ImageView) this.findViewById(R.id.qrcode_bg);
		BitmapFactory.Options bOptions = new BitmapFactory.Options();
		bOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		bOptions.inPurgeable = true;
		bOptions.inInputShareable = true;
		qrBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg_qrcode, bOptions);
		if (Build.VERSION.SDK_INT >= 16) {
			qrcode_bg.setBackground(new BitmapDrawable(getResources(), qrBmp));
		} else {
			qrcode_bg.setBackgroundDrawable(new BitmapDrawable(getResources(),
					qrBmp));
		}
		bOptions = null;
	}

	@Override
	protected void initEvents() {
		this.findViewById(R.id.back).setOnClickListener(this);
		share_qmark_chat_group.setOnClickListener(this);
		reset_qmark_chat_group.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.back) {
			reBack();

		} else if (i == R.id.share_qmark_chat_group) {
		} else if (i == R.id.reset_qmark_chat_group) {
			showResetDialog();

		} else {
		}
	}

	@Override
	public void onBackPressed() {
		reBack();
		super.onBackPressed();
	}

	/**
	 * @Title: showResetDialog
	 * @param:
	 * @Description: 弹出重置提示框
	 * @return void
	 */
	private void showResetDialog() {
		CustomBaseDialog dialog = CustomBaseDialog.getDialog(this,
				getString(R.string.chat_group_qmark_reset_hint),
				getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						resetQuickmark();
					}
				});
		dialog.setButton1Background(R.drawable.bg_button_dialog_1);
		dialog.setButton2Background(R.drawable.bg_button_dialog_2);
		dialog.show();
	}

	/**
	 * 
	 * @Title: resetQuickmark
	 * @param:
	 * @Description: 重置群二维码
	 * @return void
	 */
	private void resetQuickmark() {
		if (groupData == null)
			return;
		WBaseModel<GroupQRcodeResetBean> mode = new WBaseModel<GroupQRcodeResetBean>(
				this, GroupQRcodeResetBean.class);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		mode.httpJsonRequest(
				Method.POST,
				String.format(URLConfig.CHAT_GROUP_RESET_QRCODE,
						String.valueOf(groupData.getId())), parmas,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						if (data != null
								&& data instanceof GroupQRcodeResetBean) {
							GroupQRcodeResetBean bean = (GroupQRcodeResetBean) data;
							if (bean.isResult()) {
								groupData.setQrcodeUrl(bean.getData());
								// 根据消息方向获得路径
								File file = IMClient.getInstance()
										.findInImageLoaderCache(bean.getData());
								if (file != null && file.exists()) {// 二维码重置只是更换内容没有更换图片地址，所以需要将之前的缓存删掉
									file.deleteOnExit();
								}
								IMClient.sImageLoader.displayImage(
										// 加载群二维码原图，不使用缓存
										bean.getData(), code_qmark_chat_group,
										DisplayImageConfig.QRcodeImageOptions);
								showResutToast(bean.getCode());
							}
						} else {
							ShowUtil.showToast(QuickmarkActivity.this,
									getString(R.string.warning_service_error));
						}
						dissmisLoading();
					}
				});

	}

	/**
	 * @Title: reBack
	 * @param:
	 * @Description:执行回滚
	 * @return void
	 */
	private void reBack() {
		Intent intent = new Intent();
		if (groupData != null) {
			intent.putExtra(QRCODE, groupData.getQrcodeUrl());
		}
		setResult(RESULT_OK, intent);
		this.finish();

	}
}
