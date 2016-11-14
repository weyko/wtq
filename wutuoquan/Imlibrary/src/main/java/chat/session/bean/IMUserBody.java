/**
 *
 */
package chat.session.bean;

import java.io.Serializable;

/**
 *
 * @ClassName IMUserBody
 * @Description 魔聊消息用户信息基础类
 * @Company MoXian
 * @author weyko
 * @email zhong.xiwei@moxiangroup.com
 * @date 2015年6月24日下午5:28:04
 *
 */
public  class IMUserBody implements Serializable {
	private static final long serialVersionUID = 10202222L;
	/** 头像 */
	private String avatar;
	/** 名称 */
	private String name;
	/** 修改为女=2、男=1 */
	private int gender;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
    public IMUserBody getImUserBody(){
        IMUserBody userBody=new IMUserBody();
        userBody.setAvatar(avatar);
        userBody.setName(name);
        userBody.setGender(gender);
        return userBody;
    }
}
