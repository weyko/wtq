package chat.session.util;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.BaseActivity;
import chat.base.IMClient;

/**
 * @ClassName: PageToastUtil
 * @Description: 页面提示管理类
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月5日 下午2:59:45
 *
 */
public class PageToastUtil {
	private TextView hint_toast;
	private ImageView icon_toast;

	/**
	 * @ClassName: PageMode
	 * @Description: 页面提示类型
	 * @author weyko zhong.xiwei@moxiangroup.com
	 * @Company moxian
	 * @date 2015年8月5日 下午3:29:15
	 *
	 */
	public enum PageMode {
		/** 默认 */
		DEFAULT,
		/** 群组空白 */
		GROUP_EMPTY,
		/** 群组未找到 */
		GROUP_NOTFUND,
		/** 联系人空白 */
		CONTACT_EMPTY,
		/** 联系人未找到 */
		CONTACT_NOTFUND,
		/** 邀请失效 */
		INVITE_INVALID
	}

	/* 提示页 */
	private View mainView;

	public PageToastUtil(View view) {
		icon_toast = (ImageView) view.findViewById(R.id.icon_toast);
		hint_toast = (TextView) view.findViewById(R.id.hint_toast);
	}

	public PageToastUtil(BaseActivity baseActivity) {
		icon_toast = (ImageView) baseActivity.findViewById(R.id.icon_toast);
		hint_toast = (TextView) baseActivity.findViewById(R.id.hint_toast);
	}

	/**
	 * @Title: showToast
	 * @param:
	 * @Description: 显示提示页面
	 * @return void
	 */
	public void showToast(View view, PageMode pageMode) {
		mainView = view;
		if (mainView == null)
			return;
		mainView.setVisibility(View.VISIBLE);

		int icon = R.drawable.default_head;
		String toast = "";
		Resources resources = IMClient.getInstance().getContext().getResources();
		switch (pageMode) {
		case GROUP_EMPTY:
			icon = R.drawable.ic_empty_group_toast;
			toast = resources.getString(R.string.chat_group_search_empty_hint);
			break;
		case GROUP_NOTFUND:
			icon = R.drawable.ic_avatar_toast_page;
			toast = resources.getString(R.string.chat_group_search_fail_hint);
			break;
		case CONTACT_EMPTY:
			icon = R.drawable.ic_avatar_toast_page;
			toast = resources.getString(R.string.chat_group_contact_empty);
			break;
		case CONTACT_NOTFUND:
			icon = R.drawable.ic_avatar_toast_page;
			toast = resources.getString(R.string.chat_group_contact_notfund);
			break;
		case INVITE_INVALID:
			icon = R.drawable.ic_avatar_toast_page;
			toast = resources
					.getString(R.string.chat_group_invite_invite_invalid);
			break;
		default:
			icon = R.drawable.ic_avatar_toast_page;
			hint_toast.setVisibility(View.GONE);
			break;
		}
		if(icon_toast!=null){
			icon_toast.setImageResource(icon);
		}
		if(hint_toast!=null){
			hint_toast.setText(toast);
		}
	}

	/**
	 * @Title: hidePage
	 * @param:
	 * @Description: 隐藏提示页面
	 * @return void
	 */
	public void hidePage() {
		if (mainView != null)
			mainView.setVisibility(View.GONE);
	}

	/**
	 * @Title: setHintImage
	 * @param:
	 * @Description: 设置提示图标
	 * @return void
	 */
	public void setHintImage(int resource) {
		if (icon_toast == null || resource < 0)
			return;
		icon_toast.setImageResource(resource);
	}

	/**
	 * @Title: setHint
	 * @param:
	 * @Description: 设置提示
	 * @return void
	 */
	public void setHint(String hint) {
		if (hint_toast == null || hint == null)
			return;
		hint_toast.setText(hint);
	}

	/**
	 * @Title: setHint
	 * @param:
	 * @Description: 设置提示
	 * @return void
	 */
	public void setHint(int hint) {
		if (hint_toast == null || hint < 0)
			return;
		hint_toast.setText(hint_toast.getResources().getString(hint));
	}

	/**
	 * @Title: isShowing
	 * @param:
	 * @Description: 是否显示
	 * @return boolean
	 */
	public boolean isShowing() {
		if (mainView != null)
			return mainView.getVisibility() == View.VISIBLE;
		return false;
	}
}
