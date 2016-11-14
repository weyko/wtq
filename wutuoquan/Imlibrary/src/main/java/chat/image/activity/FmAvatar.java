package chat.image.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.imlibrary.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

import chat.base.IMClient;
import chat.image.IMImageLoader;
import chat.view.photoview.PhotoView;
import chat.view.photoview.VersionedGestureDetector;

@SuppressLint({"NewApi", "ClickableViewAccessibility"})
public class FmAvatar extends Fragment implements VersionedGestureDetector.OnFinishListener {
    private View mMainView;
    private String imgageUrl;// 图片地址
    private PhotoView photoView;// 图片控件
    private int type;
    private ProgressBar bar;
    private IMImageLoader imageLoader;

    public FmAvatar() {
    }

    public void setImageLoader(IMImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mMainView = inflater.inflate(R.layout.activity_photoinfo,
                (ViewGroup) getActivity().findViewById(R.id.showphoto_viewpager), false);
        photoView = (PhotoView) mMainView.findViewById(R.id.imagesView);
        bar = (ProgressBar) mMainView.findViewById(R.id.image_progressBar);
        photoView.setOnFinishListener(this);

    }

    private void back() {
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup p = (ViewGroup) mMainView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }

        return mMainView;
    }

    public void setImageUrl(String url) {
        imgageUrl = url;
    }

    @Override
    public void onDestroy() {
        // Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        if (imgageUrl != null) {
            File file = new File(imgageUrl);
            //  处理加载本地图片显示不全的bug
            if (file.exists()) {// 本地图片
                imgageUrl = "file:///" + imgageUrl;
                displayImg(IMClient.chatLocalImgeLoader);
            } else
                displayImg(imageLoader);
        }
        super.onResume();
    }

    /**
     * @return void
     * @Title: displayImg
     * @param:
     * @Description: 显示图片内容
     */
    private void displayImg(IMImageLoader imageLoader) {
        if(imageLoader==null)
            return;
        imageLoader.displayImage(imgageUrl, photoView,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                        if (bar != null) {
                            bar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1,
                                                  Bitmap arg2) {
                        if (bar != null) {
                            bar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        if (bar != null) {
                            bar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                        if (bar != null) {
                            bar.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        // Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onStop() {
        // Auto-generated method stub
        super.onStop();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void doFinish() {
        back();

    }
}
