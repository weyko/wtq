package chat.contact.bean;

import java.io.Serializable;

public class ImageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 用MsgId做imageId */
	public String imageId;
	//public String thumbnailPath;// 缓存路径
	public String imagePath;// 绝对路径
	public boolean isSelected = false;
	public String sessionId="";
	public String shopId="";

	public boolean isSelected() {
		return false;
	}

	public void setSelected(boolean b) {
		this.isSelected = b;
	}
	@Override
	public String toString() {
		return "[" + "imageId==" + imageId + "]" + "  [imagePath==" + imagePath + "]";
	}
}
