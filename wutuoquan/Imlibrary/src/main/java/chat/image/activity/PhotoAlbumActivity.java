package chat.image.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.imlibrary.R;

import java.util.ArrayList;

import chat.image.photo.adapter.PhotoAlbumLVAdapter;
import chat.image.photo.model.PhotoAlbumLVItem;
import chat.image.photo.utils.SearchImageUtils;
import chat.image.photo.utils.Utility;

public class PhotoAlbumActivity extends Activity {
	private PhotoAlbumLVAdapter adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selector_photo_album);
        if (!Utility.isSDcardOK()) {
            //Utility.showToast(this, "SDCard Invisible");
            this.finish();
            return;
        } 
        Intent t = getIntent();
        TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
        titleTV.setText(R.string.ok);

        Button backBtn = (Button) findViewById(R.id.topbar_left_btn);
        Button cancelBtn = (Button) findViewById(R.id.topbar_right_btn);
        cancelBtn.setText(R.string.cancel);
        cancelBtn.setVisibility(View.VISIBLE);

        ListView listView = (ListView) findViewById(R.id.select_img_listView);

        final ArrayList<PhotoAlbumLVItem> list = new ArrayList<PhotoAlbumLVItem>();

        if(PhotoWallActivity.isShowRecentlyPhotos){//add recently photos
            list.add(new PhotoAlbumLVItem(getResources().getString(R.string.selector_latest_image),
                    t.getIntExtra("recentlyPhotoNum", 0), t.getStringExtra("recentlyImagePath")));
        }
        ArrayList<PhotoAlbumLVItem> photoDatas= SearchImageUtils.getImagePathsByContentProvider(this);
        if(photoDatas!=null){
        	list.addAll(photoDatas);
        }
        adapter = new PhotoAlbumLVAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent intent = new Intent();
                //第一行为“最近照片”
                if (position == 0) {
                    intent.putExtra("code", PhotoWallActivity.RECENTLY_TAG);
                } else {
                    intent.putExtra("code", PhotoWallActivity.FLODER_TAG);
                    intent.putExtra("folderPath", list.get(position).getPathName());
                }
				setResult(RESULT_OK, intent);
                PhotoAlbumActivity.this.finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	backAction();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	backAction();
            }
        });
    }

   
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (adapter!=null){
			adapter.clearMemory();
		}
		adapter = null;
	}

	private void backAction() {
        this.setResult(RESULT_OK);
        this.finish();
    }

    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //动画
    }
}