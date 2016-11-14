package chat.image.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.imlibrary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ImageItem;
import chat.image.ImageUtils;
import chat.image.adapter.ImageGridAdapter;
import chat.manager.ChatMessageManager;
import chat.session.activity.ChatActivity;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.MessageBean;
import chat.session.enums.SessionTypeEnum;
import chat.session.util.ChatUtil;

public class ChatPhotoWall extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener {
    public String TAG = getClass().getSimpleName();

    private Button all;
    private GridView gridView;
    private List<ImageItem> dataList;// 图像列
    private ImageGridAdapter adapter;
    private boolean flag = true;// 标记全选
    // private List<ImageItem> selected = new ArrayList<ImageItem>();
    private ImageView mSave;
    private TextView mTitle;
    private ImageView mBack;
    private CheckBox mSelect;
    private View mLoy_1;
    private ImageView transpond;// 转发
    private MessageBean mIMBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_photowall);
        initEvents();
    }

    @Override
    protected void initView() {
        all = (Button) findViewById(R.id.all);
        gridView = (GridView) findViewById(R.id.gridview);
        transpond = (ImageView) findViewById(R.id.btn_chat_transpondl);
        transpond.setEnabled(true);
        mSave = (ImageView) findViewById(R.id.btn_chat_copy);
        mSave.setEnabled(false);
        mTitle = (TextView) findViewById(R.id.titleText);
        mBack = (ImageView) findViewById(R.id.back);
        mSelect = (CheckBox) findViewById(R.id.select);
        mLoy_1 = findViewById(R.id.loy1);

    }
    @Override
    protected void initData() {
        Intent intent = getIntent();
        mIMBean = (MessageBean) intent
                .getSerializableExtra(ChatActivity.MESSAGEBEAN);
        if (mIMBean == null)
            return;
        String chat_with = mIMBean.getSessionId();
        // String chat_with=intent.getStringExtra(ChatActivity.TOID);
        if (chat_with != null && chat_with.length() > 0) {
            dataList = new ArrayList<ImageItem>();
            List<Map<String, String>> list = ChatMessageManager.getInstance()
                    .getPhotoPaths(chat_with,"");
            int size = 0;
            if (list != null) {
                size=list.size();
                setViewModeByImageSize(size);

                for (int i = 0; i < size; i++) {
                    ImageItem bean = new ImageItem();
                    Map<String, String> map = list.get(i);
                    String path = (String) map.get(ChatMessageManager.FILE);
                    if (path != null && path.length() > 0) {
                        bean.imageId = map.get(ChatMessageManager.MSGID);
                        bean.sessionId = map.get(ChatMessageManager.SESSIONID);
                        bean.imagePath = path;
                        dataList.add(bean);
                    }
                }
            }
        } else {
            this.findViewById(R.id.tv_empty_pic).setVisibility(View.VISIBLE);
            mSelect.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 根据图片个数设置视图模式
     *
     * @param size
     * @return
     */
    private int setViewModeByImageSize(int size) {
        this.findViewById(R.id.tv_empty_pic).setVisibility(
                size > 0 ? View.GONE : View.VISIBLE);
        mSelect.setVisibility(size > 0 ? View.VISIBLE : View.INVISIBLE);
        return size;
    }

    @Override
    protected void initEvents() {
        all.setOnClickListener(this);
        adapter = new ImageGridAdapter(this, dataList, mIMBean);
        gridView.setAdapter(adapter);
        mTitle.setText(R.string.chat_photowall_title);
        mBack.setOnClickListener(this);
        mSelect.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.all) {
            for (ImageItem item : dataList) {
                if (flag)
                    item.setSelected(true);
                else
                    item.setSelected(false);
            }
            adapter.notifyDataSetChanged();
            if (flag)
                flag = false;
            else
                flag = true;

            all.setText(flag == true ? R.string.chat_select_all_on
                    : R.string.chat_select_all_off);

        } else if (i1 == R.id.btn_chat_delete) {
            boolean remove = false;
            List<ImageItem> selected = getSelectedItems();
            if (selected != null && selected.size() > 0) {
                remove = ChatUtil.removeImageItem(adapter, dataList, selected);
                ShowUtil.showToast(this, R.string.chat_listdialog_delete_success);
                ChatActivity.deleteMsg = new ArrayList<String>();
                for (int i = 0; i < selected.size(); i++) {
                    ChatActivity.deleteMsg.add(selected.get(i).imageId);
                }
            } else {
                setViewModeByImageSize(0);
            }

        } else if (i1 == R.id.btn_chat_transpondl) {
            doTranspond(mIMBean);

        } else if (i1 == R.id.btn_chat_save) {
            List<ImageItem> selected = getSelectedItems();
            if (selected != null && selected.size() > 0) {
                ChatUtil.savePhoto(this, selected);
            }
            // 保存本地

        } else if (i1 == R.id.back) {
            this.finish();


        } else {
        }
    }

    /**
     * 执行转发操作
     *
     * @param bean
     */
    private void doTranspond(MessageBean bean) {
        if (bean != null) {
            bean.setSessionType(SessionTypeEnum.NORMAL);
            List<ImageItem> selected = getSelectedItems();
            if (selected == null)
                return;
            int size = selected.size();
            if (size == 0)
                return;
            if (size > 6) {
                ShowUtil.showToast(IMClient.getInstance().getContext(), "");
                return;
            }
            for (int i = 0; i < size; i++) {
                ImageItem imageItem = selected.get(i);
                IMChatImageBody imageBody = new IMChatImageBody();
                String imagePath = imageItem.imagePath;
                if (imagePath.startsWith("http:")) {
                    imageBody.setAttr1(imagePath);
                    File file = IMClient.getInstance()
                            .findInImageLoaderCache(imagePath);
                    if (file != null && file.exists()) {
                        imagePath = file.getAbsolutePath();
                        imageBody.setLocalUrl(imagePath);
                    }
                } else {
                    imageBody.setAttr1(imagePath);
                    imageBody.setLocalUrl(imagePath);
                }
                ImageUtils.ImageSize bitmapSize = ImageUtils.getBitmapSize(imagePath);
                imageBody.setWidth(bitmapSize.width);
                imageBody.setHeight(bitmapSize.height);
                imageBody.setAttr1(getString(R.string.picture));
                Intent intent = ChatUtil.transpond(IMClient.getInstance().getContext(), bean, true);
                startActivityForResult(intent, ChatActivity.REQUEST_TRANSPOND);
            }
        }
    }

    private List<ImageItem> getSelectedItems() {
        if (dataList != null && dataList.size() > 0) {
            List<ImageItem> selected = new ArrayList<ImageItem>();
            for (ImageItem item : dataList) {
                if (item.isSelected) {
                    selected.add(item);
                }
            }
            return selected;
        }
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        if (i == R.id.select) {
            if (isChecked) {
                mSelect.setText(R.string.cancel);
                mLoy_1.setVisibility(View.VISIBLE);
            } else {
                mSelect.setText(R.string.selsect);
                mLoy_1.setVisibility(View.GONE);
            }
            adapter.flag = isChecked;
            adapter.notifyDataSetChanged();

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ChatActivity.REQUEST_TRANSPOND) {
                if (ChatActivity.chatActivity != null) {
//                    ChatActivity.chatActivity.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (adapter != null) {
            adapter.clearMemory();
        }
        adapter = null;
        System.gc();
    }
}
