package chat.common.util.string;

import android.annotation.SuppressLint;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import chat.common.util.output.LogUtil;

public class PingYinUtil {
	/**
	 * 字符串转换拼音失败，抛出异常时的默认返回值
	 */
	public static final String defaultPinyin = "#";
	private static HanyuPinyinOutputFormat mFormat = null;
	
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getPingYin(String inputString) {
		//add hjhuang 根据目前的需求，只排第一个字符，特殊处理只处理第一个字符
		if(inputString!=null&&inputString.length()>1){
			inputString = inputString.substring(0,1);
		}
		if (mFormat==null){
			mFormat = new HanyuPinyinOutputFormat();
			mFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			mFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			mFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		}
		char[] input = new char[0];
		if(inputString != null){
			input = inputString.trim().toLowerCase().toCharArray();
		}
		String output = "";
		try {
			for (int i = 0; i < input.length; i++) {
				if (Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], mFormat);
					if (temp == null) {
						continue;
					}
					output += temp[0];
					temp=null;
				} else
					output += Character.toString(input[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			output = defaultPinyin;
			LogUtil.e(e.toString());

		} catch (NullPointerException e) {
			output = defaultPinyin;
			LogUtil.e(e.toString());
		}
		input = null;
		return output;
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		if (mFormat==null){
			mFormat = new HanyuPinyinOutputFormat();
			mFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			mFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			mFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		}
		try {
			char[] nameChar = chines.toCharArray();
			//defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
			//defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			for (int i = 0; i < nameChar.length; i++) {//add by h.j.huang 只处理首字母
				if (nameChar[i] > 128) {// 是中文
					try {
						if (PinyinHelper.toHanyuPinyinStringArray(nameChar[i],
								mFormat) != null) {// 解析出错,直接放到"#"里面
							pinyinName += PinyinHelper
									.toHanyuPinyinStringArray(nameChar[i],
											mFormat)[0].charAt(0);
						} else {
							pinyinName = defaultPinyin;
						}
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						pinyinName = defaultPinyin;
						LogUtil.e(e.toString());
					}
				} else if( (nameChar[i] >= 65&&nameChar[i] <= 90) || 
						(nameChar[i] >= 97&&nameChar[i] <= 122) ){  //add by h.j.huang 判断是字母
					pinyinName += nameChar[i];
				} else{
					pinyinName = defaultPinyin;
				}
			}
			nameChar = null;
		} catch (Exception e) {
			pinyinName = defaultPinyin;
			LogUtil.e(e.toString());
		}
		//defaultFormat = null;
		return pinyinName;
	}
}
