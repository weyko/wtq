package chat.image.photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.imlibrary.R;

import java.io.File;
import java.util.ArrayList;

import chat.image.photo.model.PhotoAlbumLVItem;
import chat.image.photo.utils.SDCardImageLoader;
import chat.image.photo.utils.ScreenUtils;

public class PhotoAlbumLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PhotoAlbumLVItem> list;
    private SDCardImageLoader loader;

    public PhotoAlbumLVAdapter(Context context, ArrayList<PhotoAlbumLVItem> list) {
        this.context = context;
        this.list = list;
        loader = new SDCardImageLoader(context, ScreenUtils.screenW, ScreenUtils.screenH);
    }

    public void clearMemory(){
    	if (loader!=null){
    		loader.clearMemory();
    	}
    }
    
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.selector_photo_album_lv_item, null);
            holder = new ViewHolder();

            holder.firstImageIV = (ImageView) convertView.findViewById(R.id.select_img_gridView_img);
            holder.pathNameTV = (TextView) convertView.findViewById(R.id.select_img_gridView_path);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //图片（缩略图）
        String filePath = list.get(position).getFirstImagePath();
        holder.firstImageIV.setTag(filePath);
        if(filePath!=null&&filePath.length()>0){
        	loader.loadImage(4, filePath, holder.firstImageIV);
        }
        //文字
        holder.pathNameTV.setText(getPathNameToShow(list.get(position)));

        return convertView;
    }

    private class ViewHolder {
        ImageView firstImageIV;
        TextView pathNameTV;
    }

    /**根据完整路径，获取最后一级路径，并拼上文件数用以显示。*/
    private String getPathNameToShow(PhotoAlbumLVItem item) {
        String absolutePath = item.getPathName();
        int lastSeparator = absolutePath.lastIndexOf(File.separator);
        if(absolutePath.length() > 0){
            return absolutePath.substring(lastSeparator + 1) + "(" + item.getFileCount() + ")";
        }else{
            return "";
        }
    }

}
