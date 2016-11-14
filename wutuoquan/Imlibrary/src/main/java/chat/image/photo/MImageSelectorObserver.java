package chat.image.photo;

/**
 * 选择照片回调
 *
 */
public interface MImageSelectorObserver {
	/**
	 * 选择/取消选择图片
	 * @param isSelected 选中/取消
	 * @param filePath  图片路径
	 * @param folder 图片所在目录
	 */
	public void onSelectImage(boolean isSelected, String filePath, String folder);
	
	public int getSelectNum();
	/**
	 * 启动相机拍照
	 */
	public void onStartCamera();
}