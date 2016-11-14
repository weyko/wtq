package chat.homespace;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imlibrary.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.util.ArrayList;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.image.DisplayImageConfig;
import chat.image.ImageUtils;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.PullableListView;

/**
 * 梦想家园
 */
public class HomeSpaceActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {
    private View header_view;
    private ImageView back, rightImg;
    private TextView titleText;
    private PullToRefreshLayout ph_layout;
    private PullableListView pl_layout;
    private List<HomeSpaceBean> list;
    private HomeSpaceAdapter adapter;
    private int pageIndex = 0;
    /**
     * 头部控件
     */
    private View headerView;
    private ImageView bg_header_homespace, avatar_header_homespace;
    private TextView name_header_homespace;
    private Bitmap backgroundBmp;
    /**点击标题栏时间*/
    private long currentClickTime = System.currentTimeMillis();
    /**
     * 头像背景监听事件
     */
    private ImageLoadingListener headImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap bmp) {
            if (bmp != null) {
                setHeaderBackground(bmp);
            }
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_refresh);
    }

    @Override
    protected void initView() {
        header_view = this.findViewById(R.id.header_view);
        back = (ImageView) this.findViewById(R.id.back);
        rightImg = (ImageView) this.findViewById(R.id.rightImg);
        titleText = (TextView) this.findViewById(R.id.titleText);
        ph_layout = (PullToRefreshLayout) this.findViewById(R.id.ph_layout);
        pl_layout = (PullableListView) this.findViewById(R.id.pl_layout);
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_homespace, null);
        bg_header_homespace = (ImageView) headerView.findViewById(R.id.bg_header_homespace);
        avatar_header_homespace = (ImageView) headerView.findViewById(R.id.avatar_header_homespace);
        name_header_homespace = (TextView) headerView.findViewById(R.id.name_header_homespace);
        setTitleView((RelativeLayout)this.findViewById(R.id.root_title));
    }
    @Override
    protected void initEvents() {
        header_view.setOnClickListener(this);
        back.setOnClickListener(this);
        rightImg.setOnClickListener(this);
        ph_layout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        titleText.setText(R.string.home_space_title);
        setRightButton();
        list = new ArrayList<HomeSpaceBean>();
        adapter = new HomeSpaceAdapter(list);
        pl_layout.addHeaderView(headerView);
        pl_layout.setAdapter(adapter);
        setHeaderView();
        getTestList();
    }

    @Override
    public void onClick(View v) {
        if (v == header_view) {
            if (isDoubleClick())
                pl_layout.setSelection(0);
        } else if (v == back) {
            this.finish();
        } else if (v == rightImg) {

        }
    }

    /**
     * 是否双击
     */
    private boolean isDoubleClick() {
        long doubleTime = System.currentTimeMillis();
        long dotTime = doubleTime - currentClickTime;
        currentClickTime = System.currentTimeMillis();
        if (dotTime > 0 && dotTime < 200) {
            return true;
        }
        return false;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIndex = 0;
        list.clear();
        getTestList();
        ph_layout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIndex++;
        getTestList();
        ph_layout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    private void getTestList() {
        for (int i = pageIndex * 20; i < (pageIndex + 1) * 20; i++) {
            HomeSpaceBean item = new HomeSpaceBean();
            item.setName("好友" + (i + 1));
            item.setContent("内容" + (i + 1));
            List<String> picUrls = new ArrayList<String>();
            for (int j = 0; j < (i + 1) % 6; j++) {
                picUrls.add("");
            }
            item.setPicUrls(picUrls);
            item.setTime(System.currentTimeMillis());
            item.setUid(String.valueOf(i));
            List<HomeSpaceBean.CommentBean> commentBeanList = new ArrayList<HomeSpaceBean.CommentBean>();
            for (int j = 0; j < (i + 1) % 7; j++) {
                HomeSpaceBean.CommentBean commentBean = new HomeSpaceBean.CommentBean();
                commentBean.setUid(String.valueOf(j));
                commentBean.setName("用户" + j);
                if (j % 2 == 0)
                    commentBean.setReplayName("被回复用户" + j);
                String comment = "评论内容" + j + " ";
                for (int k = 0; k < j % 3; k++) {
                    comment += "梦家园是个不错的论坛";
                }
                commentBean.setComment(comment);
                commentBeanList.add(commentBean);
            }
            item.setCommentList(commentBeanList);
            list.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    private void setRightButton() {
        this.findViewById(R.id.next).setVisibility(View.GONE);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(R.drawable.ic_camera);
    }

    /**
     * 设置头部控件
     */
    private void setHeaderView() {
        name_header_homespace.setText("沙漠狼");
        String avatarUrl = "";
        IMClient.sImageLoader.displayImage(avatarUrl, avatar_header_homespace, DisplayImageConfig.userLoginItemImageOptions, headImageLoadingListener);
    }

    /**
     * 设置头部背景
     *
     * @param bitmap
     */
    private void setHeaderBackground(Bitmap bitmap) {
        if (bitmap != null) {
            if (isFinishing()) {
                return;
            }
            int width = bg_header_homespace.getWidth();
            int height = bg_header_homespace.getHeight();
            Bitmap bmp = ImageUtils.doBlurAndBlackApha(bitmap, width, height, 50);
            System.gc();
            if (bmp != null) {
                backgroundBmp = bmp;
                if (Build.VERSION.SDK_INT >= 16) {
                    bg_header_homespace.setBackground(new BitmapDrawable(getResources(), backgroundBmp));
                } else {
                    bg_header_homespace.setBackgroundDrawable(new BitmapDrawable(getResources(), backgroundBmp));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.clearCache();
            adapter = null;
        }
        if (backgroundBmp != null) {
            backgroundBmp.recycle();
            backgroundBmp = null;
        }
    }
}
