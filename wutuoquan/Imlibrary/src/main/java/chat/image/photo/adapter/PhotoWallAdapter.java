package chat.image.photo.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.imlibrary.R;

import java.util.ArrayList;

import chat.image.photo.MImageSelectorObserver;
import chat.image.photo.model.PhotoWallItem;
import chat.image.photo.utils.SDCardImageLoader;
import chat.image.photo.utils.ScreenUtils;
import chat.image.photo.utils.SearchImageUtils;

public class PhotoWallAdapter extends BaseAdapter {
	
	public final static int ACTION_TYPE_PHOTO=0;  //执行动作选图片
	public final static int ACTION_TYPE_CAMARA=1; //执行动作拍照
	
    private Context context;
    private ArrayList<PhotoWallItem> datas = null;
    private SDCardImageLoader loader;
    private MImageSelectorObserver mImageSelectorObserver;
    private boolean isRecently=false;
    private int limitNum=1;
    
    public PhotoWallAdapter(Context context, ArrayList<PhotoWallItem> datas) {
        this.context = context;
        this.datas = datas;
        this.registerDataSetObserver(dataSetObserver);
        loader = new SDCardImageLoader(context, ScreenUtils.screenW, ScreenUtils.screenH);
        checkIsRecently();
    }

    private DataSetObserver dataSetObserver= new DataSetObserver(){
    	public void onChanged() {  
    		checkIsRecently();
        }  
    };
    
    public void setSelectNumLimit(int limitNum){
    	this.limitNum = limitNum;
    }
    public void setMImageSelectorObserver(MImageSelectorObserver observer){
    	mImageSelectorObserver = observer;
    }
    
    private void checkIsRecently(){
    	if(datas.size()>0){
        	isRecently = (datas.get(0).actionType==ACTION_TYPE_CAMARA)?true:false;
        }
    }
    
    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position<datas.size()?datas.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	PhotoWallItem item = (PhotoWallItem) getItem(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.selector_photo_wall_item, null);
            holder = new ViewHolder();

            holder.imageView = (ImageView) convertView.findViewById(R.id.photo_wall_item_photo);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.photo_wall_item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(item.actionType==ACTION_TYPE_PHOTO){
        	holder.checkBox.setVisibility(View.VISIBLE);
        	holder.imageView.setTag(item.photoFilePath);
        	loader.loadImage(4, item.photoFilePath, holder.imageView);
        	holder.imageView.setScaleType(ScaleType.CENTER_CROP);
        	if(item.isSelected){
        		holder.checkBox.setImageResource(R.drawable.selector_checkbox_checked);
        	}else{
        		holder.checkBox.setImageResource(R.drawable.selector_checkbox_normal);
        	}
        }else{
        	holder.checkBox.setVisibility(View.GONE);
        	holder.imageView.setTag("carema");
        	holder.imageView.setScaleType(ScaleType.CENTER_INSIDE);
        	holder.imageView.setImageResource(R.drawable.select_pic_camera);
        }
        holder.imageView.setTag(R.id.tag_first, position);
        holder.imageView.setTag(R.id.tag_second, holder.checkBox);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Integer pos = (Integer) arg0.getTag(R.id.tag_first);
				PhotoWallItem item = (PhotoWallItem)getItem(pos);
				if(pos==0 && item.actionType==ACTION_TYPE_CAMARA){
					if(mImageSelectorObserver!=null){
						mImageSelectorObserver.onStartCamera();
					}
				}else{
					ImageView checkBox = (ImageView) arg0.getTag(R.id.tag_second);
					ImageView imageView = (ImageView)arg0;
					if (limitNum==1){//单选
						if (!item.isSelected){
							clearAllSelected();
							item.isSelected = true;
							PhotoWallAdapter.this.notifyDataSetChanged();
							if(mImageSelectorObserver!=null){
								mImageSelectorObserver.onSelectImage(item.isSelected, item.photoFilePath,getParentDir(item.photoFilePath));
							}
						}
					}else {
						if(!item.isSelected){
							if(mImageSelectorObserver.getSelectNum()>=limitNum){
								return;
							}
						}
						item.isSelected = ! item.isSelected;
						if (item.isSelected) {
							checkBox.setImageResource(R.drawable.selector_checkbox_checked);
							imageView.setColorFilter(context.getResources().getColor(R.color.selector_image_checked_bg));
		                } else {
		                	holder.checkBox.setImageResource(R.drawable.selector_checkbox_normal);
		                	imageView.setColorFilter(null);
		                }
						if(mImageSelectorObserver!=null){
							mImageSelectorObserver.onSelectImage(item.isSelected, item.photoFilePath,getParentDir(item.photoFilePath));
						}
					}
				}
			}
		});
        return convertView;
    }
    
    public void clearMemory(){
    	loader.clearMemory();
    	loader = null;
    	this.unregisterDataSetObserver(dataSetObserver);
    	dataSetObserver = null;
    }
    
    private void clearAllSelected(){
    	for(int i=0;i<datas.size();i++){
    		datas.get(i).isSelected = false;
    	}
    }
    
    private String getParentDir(String filePath){
    	String dir="";
    	if(isRecently){
    		dir = SearchImageUtils.RECENTLY;
    	}else{
    		if(filePath!=null&&filePath.length()>0){
        		int lastDirPos=filePath.lastIndexOf("/");
            	if(lastDirPos>0){
            		dir = filePath.substring(0,lastDirPos);
            	}
        	}
    	}
    	return dir;
    }
    
    private class ViewHolder {
        ImageView imageView;
        ImageView checkBox;
    }
}
