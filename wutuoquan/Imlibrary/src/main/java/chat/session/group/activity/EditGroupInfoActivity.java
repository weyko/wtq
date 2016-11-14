package chat.session.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.imlibrary.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import chat.base.BaseActivity;
import chat.common.config.URLConfig;
import chat.common.util.TextUtils;
import chat.common.util.output.ShowUtil;
import chat.session.group.bean.CreateGroupResultBean;
import chat.volley.WBaseBean;
import chat.volley.WBaseModel;
import chat.volley.WRequestCallBack;

/**
 * 编辑群信息
 */
public class EditGroupInfoActivity extends BaseActivity implements
		OnClickListener, TextWatcher {
	/** 群组名称 */
	public static final String EDIT_MODE = "EDIT_MODE";
	/** 群组名称 */
	public static final String EDIT_ID = "EDIT_ID";
	/** 群组名称 */
	public static final int EDIT_GROUP_NAME = 0;
	/** 我在群组的昵称 */
	public static final int EDIT_GROUP_MY_NAME = 1;
	/** 名称的最大长度 */
	private final int NAME_MAX_LEN = 40;
	/** 显示统计名称长度的分隔符 */
	private final String NAME_COUNT_SPLIT = "/";
	/** 标题 */
	private TextView title_group_info_edit;
	/** 保存 */
	private TextView save_group_info_edit;
	/** 文字个数 */
	private TextView count_group_info_edit;
	/** 提示文字 */
	private TextView hint_group_info_edit;
	/** 输入框 */
	private EditText edt_group_info_edit;
	/** 编辑模式 */
	private int editMode = EDIT_GROUP_MY_NAME;
	/** 群组id */
	private String roomId;
	/** 更新内容 */
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info_edit);
		getIntentParmas();
		initEvents();
		initData();
	}
	@Override
	protected void initData() {
		edt_group_info_edit.setText(content);
		switch (editMode) {
		case EDIT_GROUP_MY_NAME:
			title_group_info_edit.setText(R.string.chat_group_info_nickname_my);
			hint_group_info_edit
					.setText(R.string.chat_group_info_edit_myname_hint);
			break;
		case EDIT_GROUP_NAME:
			title_group_info_edit.setText(R.string.chat_group_info_nickname);
			edt_group_info_edit.setHint(R.string.chat_group_create_name_hint);
			break;
		}
		edt_group_info_edit.setText(content);
	}

	private void getIntentParmas() {
		editMode = getIntent().getIntExtra(EDIT_MODE, editMode);
		roomId = getIntent().getStringExtra(EDIT_ID);
		content = getIntent().getStringExtra("name");
	}

	@Override
	protected void initView() {
		title_group_info_edit = (TextView) this
				.findViewById(R.id.title_group_info_edit);
		save_group_info_edit = (TextView) this
				.findViewById(R.id.save_group_info_edit);
		count_group_info_edit = (TextView) this
				.findViewById(R.id.count_group_info_edit);
		hint_group_info_edit = (TextView) this
				.findViewById(R.id.hint_group_info_edit);
		edt_group_info_edit = (EditText) this
				.findViewById(R.id.edt_group_info_edit);
	}

	@Override
	protected void initEvents() {
		this.findViewById(R.id.cancal_group_info_edit).setOnClickListener(this);
		this.findViewById(R.id.save_group_info_edit).setOnClickListener(this);
		edt_group_info_edit.addTextChangedListener(this);
	}

	@Override
	public void onClick(View arg0) {
		int i = arg0.getId();
		if (i == R.id.cancal_group_info_edit) {
			doBack(true);

		} else if (i == R.id.save_group_info_edit) {
			doSave();

		} else {
		}
	}

	/**
	 * @Title: doSave
	 * @param:
	 * @Description: 保存信息
	 * @return void
	 */
	private void doSave() {
		int curCount = 0;
		try {
			curCount = edt_group_info_edit.getText().toString().trim().getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (curCount > NAME_MAX_LEN) {
			ShowUtil.showToast(this, R.string.chat_group_info_nickname_hint);
			return;
		}
		if (editMode == EDIT_GROUP_MY_NAME)
			updateMyNickname(edt_group_info_edit.getText().toString().trim());
		else
			updateGruopInfo(edt_group_info_edit.getText().toString().trim());
	}

	/**
	 * @Title: doBack
	 * @param:
	 * @Description: 回滚操作
	 * @return void
	 */
	private void doBack(boolean isCancal) {
		Intent intent = new Intent(this, ChatGroupInfoAcitivty.class);
		intent.putExtra("content", content);
		intent.putExtra("content", content);
		intent.putExtra("isCancal", isCancal);
		intent.putExtra(EDIT_MODE, editMode);
		setResult(RESULT_OK, intent);
		this.finish();
		
	}

	@Override
	public void onBackPressed() {
		doBack(true);
		super.onBackPressed();
	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
								  int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		int len = 0;
		try {
			len = arg0.toString().getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		count_group_info_edit.setVisibility(len > 0 ? View.VISIBLE : View.GONE);
		if (len > 0) {
			count_group_info_edit
					.setText(len + NAME_COUNT_SPLIT + NAME_MAX_LEN);
			if (len > NAME_MAX_LEN) {
				TextUtils.setTextColor(
						count_group_info_edit,
						0,
						count_group_info_edit.getText().toString()
								.indexOf(NAME_COUNT_SPLIT), getResources()
								.getColor(R.color.color_1));
			} else {
				count_group_info_edit.setTextColor(getResources().getColor(
						R.color.text_color_no_click));
			}
		}
		save_group_info_edit.setEnabled(len > 0);
		save_group_info_edit.setTextColor(getResources().getColor(
				len > 0 ? R.color.text_color_title
						: R.color.text_color_no_click));
	}

	/**
	 * @Title: updateGruopInfo
	 * @param:
	 * @Description: 更新群信息
	 * @return void
	 */
	public void updateGruopInfo(String content) {
		this.content = content;
		WBaseModel<CreateGroupResultBean> mode = new WBaseModel<CreateGroupResultBean>(
				this, CreateGroupResultBean.class);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		parmas.put("roomName", content);
		mode.httpJsonRequest(Method.PUT,
				String.format(URLConfig.CHAT_GRUOP_UPDATE, roomId), parmas,
				new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						if (data != null) {
							if (data instanceof WBaseBean) {
								doBack(false);
								showResutToast(((WBaseBean) data).getCode());
							} else {
								ShowUtil.showToast(
										EditGroupInfoActivity.this,
										getString(R.string.warning_service_error));
							}
						} else {
							ShowUtil.showToast(EditGroupInfoActivity.this,
									getString(R.string.warning_service_error));
						}
						dissmisLoading();
					}
				});
	}

	/**
	 * @Title: updateMyNickname
	 * @param:
	 * @Description: 更新群信息
	 * @return void
	 */
	public void updateMyNickname(String content) {
		this.content = content;
		WBaseModel<CreateGroupResultBean> mode = new WBaseModel<CreateGroupResultBean>(
				this, CreateGroupResultBean.class);
		HashMap<String, Object> parmas = new HashMap<String, Object>();
		parmas.put("nickName", content);
		mode.httpJsonRequest(Method.PUT,
				String.format(URLConfig.CHAT_GROUP_SET_MY_NICKNAME, roomId),
				parmas, new WRequestCallBack() {
					@Override
					public void receive(int httpStatusCode, Object data) {
						if (data != null) {
							if (data instanceof WBaseBean) {
								doBack(false);
								showResutToast(((WBaseBean) data).getCode());
							} else {
								ShowUtil.showToast(
										EditGroupInfoActivity.this,
										getString(R.string.warning_service_error));
							}
						} else {
							ShowUtil.showToast(EditGroupInfoActivity.this,
									getString(R.string.warning_service_error));
						}
						dissmisLoading();
					}
				});
	}
}
