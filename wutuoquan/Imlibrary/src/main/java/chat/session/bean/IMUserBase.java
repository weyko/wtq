package chat.session.bean;

import java.io.Serializable;

/**
 * IM 用户基类 抽出基类以便共用,减少依赖
 */
public class IMUserBase implements Serializable {
	private static final long serialVersionUID = 1224242L;
	private String id;
	private String name;
	private String avatarUrl;
	private String catalog = "";
	public boolean selected=false;
	private int boxType;
	private long time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public int getBoxType() {
		return boxType;
	}
	public void setBoxType(int boxType) {
		this.boxType = boxType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}