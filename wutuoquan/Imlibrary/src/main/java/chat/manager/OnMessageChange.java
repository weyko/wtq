package chat.manager;

/**
 * @ClassName: OnMessageChange
 * @Description: 监听消息改变接口
 * @author weyko zhong.xiwei@moxiangroup.com
 * @Company moxian
 * @date 2015年8月4日 下午3:02:44
 *
 */
public interface OnMessageChange {
	/**
	* @Title: onChanged 
	* @param: 
	* @Description:数据库改变
	* @return void
	 */
	public void onChanged(String sessionId);
}
