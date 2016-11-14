package chat.image.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imlibrary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.config.Constant;
import chat.common.config.URLConfig;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ImageItem;
import chat.image.IMImageLoader;
import chat.manager.ChatMessageManager;
import chat.service.MessageInfoReceiver;
import chat.session.activity.ChatActivity;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.MessageBean;
import chat.session.util.ChatUtil;
import chat.view.HackyViewPager;

/**
 * 显示大图
 */
public class ShowBigPhoto extends BaseActivity implements OnClickListener {
    public final String TAG = getClass().getSimpleName();
    private String chat_with;
    private HackyViewPager mViewPager;
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private MyViewPagerAdapter mAdapter;
    private int mCurrent = -1;
    private MessageBean mIMBean;
    private ImageView mCopy;
    private RelativeLayout loy, shop_banner;
    private LinearLayout circles_shop_banner;
    private TextView name_shop_banner;
    private ImageView copy_shop_banner;
    public final static String SHOP_KEY = "shop_banner";
    public final static String SHOWBIG_TYPE = "imagetype";
    public final static String SHOP_POSITION_KEY = "shop_banner_position";
    private int imageType = 1;// 图片加载类型，1：本地，0：网络
    private ProgressBar pb_load_local = null;
    private IMImageLoader imageLoader = null;
    // 是否有编辑
    private boolean hasEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showchatpicture);
        imageType = getIntent().getIntExtra(SHOWBIG_TYPE, 1);
        if (imageLoader == null) {
            imageLoader = new IMImageLoader();
            imageLoader.initConfig(this, 0);
        }
        initImageDatas();
        initView();
        initEvents();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ChatUtil.sendUpdateNotify(IMClient.getInstance().getContext(),
                MessageInfoReceiver.EVENT_UPDATE_AFTER_DELETE_PIC, ""
                        + hasEdited, "");
        if (mDataList != null) {
            mDataList.clear();
            mDataList = null;
        }
        if (imageLoader != null) {
            imageLoader.clearMemory();
            imageLoader = null;
        }
        circles = null;
        System.gc();
    }

    private void initImageDatas() {
        final Intent intent = getIntent();
        mIMBean = (MessageBean) intent
                .getSerializableExtra(ChatActivity.MESSAGEBEAN);
        chat_with = mIMBean.getSessionId();
        if (mIMBean.getAttachment() != null) {
            IMChatImageBody imageBody = mIMBean.getAttachment();
            String imagePath = intent
                    .getStringExtra(ChatActivity.TRANSPOND_PIC_PATH);
            if (imagePath == null || imagePath.length() <= 0) {
                String mLocalUrl = imageBody.getLocalUrl();
                String mUrl = imageBody.getAttr1();
                if (!TextUtils.isEmpty(mUrl) && !mUrl.startsWith("http:")) {
                    mUrl = URLConfig.getDomainUrl(Constant.DOMAIN_IMAGE_TYPE)
                            + mUrl;
                }
                if (mLocalUrl == null || mLocalUrl.length() <= 0) {
                    if (mUrl != null && mUrl.length() > 0) {
                        final File imageFile = IMClient.getInstance()
                                .findInImageLoaderCache(mUrl);
                        if (imageFile != null && imageFile.exists()) {
                            imagePath = imageFile.getAbsolutePath();
                        } else {
                            imagePath = mUrl;
                        }
                    }
                } else {
                    imagePath = mLocalUrl;
                }
            }
            int pos = intent.getIntExtra(ChatActivity.TRANSPOND_TYPE, -1);
            if (chat_with != null) {
                List<Map<String, String>> list = ChatMessageManager
                        .getInstance().getPhotoPaths(chat_with, "");
                for (int i = 0; i < list.size(); i++) {
                    Map<String, String> map = list.get(i);
                    String path = (String) map.get(ChatMessageManager.FILE);
                    if (path != null && path.length() > 0) {
                        ImageItem bean = new ImageItem();
                        bean.imageId = map.get(ChatMessageManager.MSGID);
                        if (path.equals(imagePath)) {
                            mCurrent = mDataList.size();
                        }
                        bean.imagePath = path;
                        bean.sessionId = map.get(ChatMessageManager.SESSIONID);
                        mDataList.add(bean);
                    }
                }
            }
            if (mCurrent < 0) {
                if (pos >= 0 && pos < mDataList.size()) {
                    mCurrent = pos;
                }
            }
        }
    }

    protected void initView() {
        mViewPager = (HackyViewPager) ShowBigPhoto.this
                .findViewById(R.id.showphoto_viewpager);
        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),
                mDataList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrent);
        mAdapter.notifyDataSetChanged();
        mCopy = (ImageView) findViewById(R.id.btn_chat_copy);
        shop_banner = (RelativeLayout) this.findViewById(R.id.shop_banner);
        loy = (RelativeLayout) this.findViewById(R.id.loy);
        circles_shop_banner = (LinearLayout) this
                .findViewById(R.id.circles_shop_banner);
        name_shop_banner = (TextView) this.findViewById(R.id.name_shop_banner);
        copy_shop_banner = (ImageView) this.findViewById(R.id.copy_shop_banner);
        pb_load_local = (ProgressBar) this.findViewById(R.id.pb_load_local);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrent = arg0;
                if (isShopMode) {
                    setCircleShowType();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        copy_shop_banner.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {
        mCopy.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        List<ImageItem> list = getSelectPhoto();
        int i1 = v.getId();
        if (i1 == R.id.btn_chat_copy) {
            this.finish();

        } else if (i1 == R.id.btn_chat_save || i1 == R.id.copy_shop_banner) {
            ChatUtil.savePhoto(this, list);

        } else if (i1 == R.id.btn_chat_transpondl) {
        } else if (i1 == R.id.btn_chat_delete) {
            boolean isDeleted = ChatUtil.removeImageItem(mAdapter, mDataList,
                    list);
            if (isDeleted) {
                hasEdited = true;
                if (ChatActivity.deleteMsg == null)
                    ChatActivity.deleteMsg = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    ChatActivity.deleteMsg.add(list.get(i).imageId);
                }
            }
            if (mDataList.size() <= 0) {
                pb_load_local.setVisibility(View.GONE);
            }

        } else if (i1 == R.id.btn_chat_menu) {
            intent = new Intent(this, ChatPhotoWall.class);
            intent.putExtra(ChatActivity.MESSAGEBEAN, mIMBean);
            startActivity(intent);

        }
    }

    /**
     * 获取当前选择的图片分装成list
     *
     * @return list
     */
    private List<ImageItem> getSelectPhoto() {
        List<ImageItem> list = new ArrayList<ImageItem>();
        if (mCurrent < mDataList.size() && mCurrent >= 0) {
            ImageItem imageItem = mDataList.get(mCurrent);
            list.add(imageItem);
        }
        return list;
    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        private List<ImageItem> paths;

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyViewPagerAdapter(FragmentManager fm, List<ImageItem> paths) {
            super(fm);
            this.paths = paths;
        }

        @Override
        public Fragment getItem(int position) {
            FmAvatar f = new FmAvatar();
            f.setImageLoader(imageLoader);
            return f;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FmAvatar mfragment1 = (FmAvatar) super.instantiateItem(container,
                    position);
            mfragment1.setType(imageType);
            mfragment1.setImageUrl(paths.get(position).imagePath);
            return mfragment1;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return paths.size();
        }
    }

    /**
     * 设置商品模式
     */
    private boolean isShopMode = false;
    private ImageView circles[];

    public void setShopModeWithShops(List<ImageItem> dataList) {
        mDataList = dataList;
        isShopMode = true;
        initView();
        loy.setVisibility(View.GONE);
        int size = mDataList.size();
        if (size <= 1)
            return;
        shop_banner.setVisibility(View.VISIBLE);
        circles = new ImageView[size];
        circles_shop_banner.removeAllViews();
        int margin = (int) getResources().getDimension(
                R.dimen.personal_image_padding);
        for (int i = 0; i < size; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (i > 0)
                params.leftMargin = margin;
            ImageView circle = new ImageView(this);
            circle.setLayoutParams(params);
            circle.setImageResource(i == 0 ? R.drawable.circle_white
                    : R.drawable.circle_gray);
            circles_shop_banner.addView(circle);
            circles[i] = circle;
        }
    }

    /**
     * 设置圆点的显示样式
     */
    protected void setCircleShowType() {
        if (circles == null)
            return;
        name_shop_banner.setText("");
        for (int i = 0; i < circles.length; i++) {
            circles[i].setImageResource(mCurrent == i ? R.drawable.circle_white
                    : R.drawable.circle_gray);
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        if (arg1 == RESULT_OK) {
            if (arg0 == 1000) {
                // 转发成功后的操作
                if (data != null) {
                    MessageBean bean = (MessageBean) data
                            .getSerializableExtra(ChatActivity.MESSAGEBEAN);
                    if (bean != null) {
                        ShowUtil.showToast(this, getString(R.string.chat_transpond_success));
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("isTran", true);
                        intent.putExtra(ChatActivity.SESSION_DATA, bean);
                        startActivity(intent);
                    } else {
                        ShowUtil.showToast(this, getString(R.string.chat_transpond_fail));
                    }
                }
            }
        }
        super.onActivityResult(arg0, arg1, data);
    }
}
