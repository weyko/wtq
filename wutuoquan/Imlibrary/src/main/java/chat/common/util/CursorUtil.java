package chat.common.util;

import android.database.Cursor;

/**
 * @ClassName: CursorUtil
 * @Description: 游标管理工具类
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月5日 下午12:30:24
 *
 */
public class CursorUtil {
	public static String getString(Cursor cursor, String key) {
		if (cursor == null)
			return "";
		return cursor.getString(cursor.getColumnIndex(key));
	}

	public static int getInt(Cursor cursor, String key) {
		if (cursor == null)
			return -1;
		return cursor.getInt(cursor.getColumnIndex(key));
	}
	public static long getLong(Cursor cursor, String key) {
		if (cursor == null)
			return -1L;
		return cursor.getLong(cursor.getColumnIndex(key));
	}
}
