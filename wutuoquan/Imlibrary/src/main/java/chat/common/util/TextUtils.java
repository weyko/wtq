package chat.common.util;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.regex.Pattern;

public class TextUtils {

	/**
	 * 改变textview的颜色
	 * 
	 * @param textView
	 *            改变颜色的TextView
	 * @param start
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @param color
	 *            颜色
	 */
	public static void setTextColor(final TextView textView, final int start,
			final int end, final int color) {
		String text = textView.getText().toString();
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new ForegroundColorSpan(color), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(sp);
	}

	/** 检查邮箱地址格式 */
	public static boolean matchEmail(String text) {
		if (Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(text)
				.matches()) {
			return true;
		}
		return false;
	}

	/** 检查手机号格式 */
	public static boolean matchPhone(String text) {
		if (Pattern.compile("(\\d{11})|(\\+\\d{3,})").matcher(text).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 拼接一个字符串与Id
	 * 
	 * @param context
	 * @param id
	 * @param str
	 * @return
	 */
	public static String getStringByIdFormat(Context context, int id, String str) {
		String ss = context.getResources().getString(id);
		String s = String.format(ss, str);
		return s;
	}
	/** 判断是否为数字 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	/** 判断是否为数字 */
	public static boolean isNumeric(char c) {
		if (c < 48 || c > 57)
			return false;
		return true;

	}

	/**
	 * 拼接一个字符串与Id
	 * 
	 * @param context
	 * @param id
	 * @param str
	 * @return
	 */
	public static String getStringFormat(Context context, int id, String str) {
		String ss = context.getResources().getString(id);
		String s = String.format(ss, str);
		return s;
	}
	/**
	 * 拼接一个字符串与Id
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static String getStringFormat(Context context, int id, Object... obj) {
		String ss = context.getResources().getString(id);
		String s = String.format(ss, obj);
		return s;
	}

	// 计算出该TextView中文字的长度(像素)
	public static float getTextViewLength(Context context, String text) {
		TextView textView = new TextView(context);
		TextPaint paint = textView.getPaint();
		// 得到使用该paint写上text的时候,像素为多少
		float textLength = paint.measureText(text);
		return textLength;
	}

	/**
	 * 处理空值情况
	 * 
	 * @param str
	 * @return
	 */
	public static String getString(String str) {
		if (str == null) {
			return "";
		} else {
			if (str.equals("null")) {
				return "";
			}
			return str.trim();
		}
	}

	/**
	 * 给文本控件中间添加一条横线
	 * 
	 * @param view
	 */
	public static void drawMiddleLine(TextView view) {
		if (view == null)
			return;
		view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
	}

	/**
	 * 给文本控件底部添加一条横线
	 * 
	 * @param view
	 */
	public static void drawBottomLine(TextView view) {
		if (view == null)
			return;
		view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}
	/**
	 * 是否为数字
	 * @param strNum
	 * @return
	 */
	public static boolean isDigit(String strNum) {
		if (strNum!=null) {
			return strNum.matches("[0-9]{1,}");
		}else {
			return false;
		}
	}
	/**
	 * 
	 * @param distance
	 * @return
	 */
	public static String formatDistance(int distance) {
		String str="";
		if (distance<999) {
			str = distance +"m";
		}else if (distance<99999) {
			str = distance/1000+"."+(distance%1000)/10+"km";
		}else if (distance<999999) {
			str = distance/1000+"."+(distance%1000)/100+"km";
		}else if (distance<9999999) {
			str = distance/1000+"km";
		}else{
			str = distance/1000000+","+(distance%1000000)/1000+"km";
		}
		return str;
	}
}