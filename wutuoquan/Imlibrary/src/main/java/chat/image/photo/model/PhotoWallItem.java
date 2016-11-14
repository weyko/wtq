package chat.image.photo.model;

import chat.image.photo.adapter.PhotoWallAdapter;

public class PhotoWallItem {
	public String photoFilePath;//图片文件路径
	public int actionType= PhotoWallAdapter.ACTION_TYPE_PHOTO;//执行动作
	public boolean isSelected=false;//是否选中
}