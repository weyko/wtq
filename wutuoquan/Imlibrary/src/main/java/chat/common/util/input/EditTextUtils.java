package chat.common.util.input;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class EditTextUtils {
	/** 隐藏输入键盘 */
	public static void hideSoftKeyboard(Activity activity,InputMethodManager inputMethodManager) {
		if(inputMethodManager==null){
			inputMethodManager=(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		if (activity!=null&&inputMethodManager!=null){
			if (activity.getCurrentFocus()!=null&&activity.getCurrentFocus().getWindowToken()!=null){
				inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
			}
		}
	}
}
