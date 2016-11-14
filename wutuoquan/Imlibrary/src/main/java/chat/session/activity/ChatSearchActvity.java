package chat.session.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imlibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import chat.base.BaseActivity;
import chat.common.util.output.ShowUtil;
import chat.contact.bean.ContactBean;
import chat.manager.ChatContactManager;
import chat.manager.ChatGroupManager;
import chat.session.adapter.common.CommonAdapter;
import chat.session.adapter.common.ViewHolder;
import chat.session.bean.ImUserBean;
import chat.session.group.bean.ChatGroupBean;
import chat.session.group.bean.RemoteMemberBean;
import chat.session.util.ChatUtil;
import chat.session.util.PageToastUtil;

/**
 * @author weyko zhong.xiwei@moxiangroup.com
 * @ClassName: ChatSearchActvity
 * @Description: 聊天搜索页面
 * @Company moxian
 * @date 2015年8月7日 上午10:29:41
 */
public class ChatSearchActvity extends BaseActivity implements
        OnClickListener, TextWatcher {
    /* 搜索key */
    public static final String SEARCH_MODE = "searchMode";
    /* 是否编辑key */
    public static final String SEARCH_ISEDIT = "searchIsEdit";
    /* 返回item的key */
    public static final String SEARCH_ITEM_CONTACT = "contact";
    /* 返回传递的key */
    public static final String SEARCH_REMOTE = "remote";
    /* 搜索联系人 */
    public static final int SEARCH_CONTACT = 0;
    /* 搜索群组 */
    public static final int SEARCH_GROUP = 1;
    /**
     * 搜索输入框
     */
    private EditText search_chat_search;
    /**
     * 清楚按钮
     */
    private ImageView clear_chat_search;
    /**
     * 取消按鈕
     */
    private TextView cancal_chat_search;
    /**
     * 搜索列表容器
     */
    private ListView list_chat_search;
    /**
     * 提示页
     */
    private View view_toast_chat_search;
    /**
     * 联系人搜索结果
     */
    private List<ImUserBean> contacts;
    /**
     * 群组搜索结果
     */
    private List<ChatGroupBean> groups;
    /* 用于传递列表数据集 */
    private RemoteMemberBean remoteMembeBean;
    /* 群组适配器 */
    private CommonAdapter<ImUserBean> adapterContact;
    /* 群组适配器 */
    private CommonAdapter<ChatGroupBean> adapterGroup;
    private PageToastUtil toastUtil;
    /**
     * 搜索模式
     */
    private int searchMode;
    /**
     * 是否编辑
     */
    private boolean isEdit = false;
    /**
     * 线程池
     */
    private ExecutorService loadPoor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_search);
        getIntentParams();
        initData();
        initEvents();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ShowUtil.showSoftWindow(ChatSearchActvity.this,
                        search_chat_search);
            }
        }, 200);
    }

    private void getIntentParams() {
        searchMode = getIntent().getIntExtra(SEARCH_MODE, SEARCH_CONTACT);
        isEdit = getIntent().getBooleanExtra(SEARCH_ISEDIT, false);
        remoteMembeBean = (RemoteMemberBean) getIntent().getSerializableExtra(
                SEARCH_REMOTE);
    }
    @Override
    protected void initData() {
        search_chat_search
                .setHint(searchMode == SEARCH_GROUP ? R.string.chat_group_create_search
                        : R.string.chat_group_search_hint);
        toastUtil = new PageToastUtil(this);
        contacts = new ArrayList<ImUserBean>();
        groups = new ArrayList<ChatGroupBean>();
    }

    @Override
    protected void initView() {
        search_chat_search = (EditText) this
                .findViewById(R.id.search_chat_search);
        clear_chat_search = (ImageView) this
                .findViewById(R.id.clear_chat_search);
        cancal_chat_search = (TextView) this
                .findViewById(R.id.cancal_chat_search);
        view_toast_chat_search = this.findViewById(R.id.view_toast_chat_search);
        list_chat_search = (ListView) this.findViewById(R.id.list_chat_search);
    }

    @Override
    protected void initEvents() {
        clear_chat_search.setOnClickListener(this);
        cancal_chat_search.setOnClickListener(this);
        search_chat_search.addTextChangedListener(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        int i = arg0.getId();
        if (i == R.id.clear_chat_search) {
            clear_chat_search.setVisibility(View.GONE);
            search_chat_search.setText("");
            searchData("");

        } else if (i == R.id.cancal_chat_search) {
            ShowUtil.hideSoftWindow(this, search_chat_search);
            doBack();

        } else {
        }
    }

    /**
     * @return void
     * @Title: doBack
     * @param:
     * @Description: 回滚操作
     */
    private void doBack() {
        this.finish();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        clear_chat_search.setVisibility(arg0.length() > 0 ? View.VISIBLE
                : View.GONE);
        searchData(arg0.toString());
    }

    /**
     * @return void
     * @Title: searchData
     * @param:
     * @Description: 查询数据
     */
    private void searchData(String searchTxt) {
        if (loadPoor == null)
            loadPoor = Executors.newSingleThreadExecutor();
        boolean hasData = false;
        switch (searchMode) {
            case SEARCH_CONTACT:
                contacts.clear();
                if (searchTxt.trim().length() == 0) {
                    adapterContact.notifyDataSetChanged();
                    list_chat_search.setVisibility(View.GONE);
                    toastUtil.hidePage();
                    return;
                }
                Future<List<ImUserBean>> future_contacts = loadPoor
                        .submit(new SearchCallable<List<ImUserBean>>(searchMode,
                                searchTxt));
                try {
                    contacts.addAll(future_contacts.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (contacts.size() > 0) {
                    hasData = true;
                }
                setContactData(contacts);
                break;
            case SEARCH_GROUP:
                groups.clear();
                if (searchTxt.trim().length() == 0) {
                    adapterGroup.notifyDataSetChanged();
                    list_chat_search.setVisibility(View.GONE);
                    toastUtil.hidePage();
                    return;
                }
                Future<List<ChatGroupBean>> future_groups = loadPoor
                        .submit(new SearchCallable<List<ChatGroupBean>>(searchMode,
                                searchTxt));
                try {
                    groups.addAll(future_groups.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (groups.size() > 0) {
                    hasData = true;
                }
                setGroupData();
                break;
            default:
                break;
        }
        setPageShow(hasData);
    }

    /**
     * @return void
     * @Title: setPageShow
     * @param:
     * @Description: 设置页面显示
     */
    private void setPageShow(boolean hasData) {
        if (hasData) {
            list_chat_search.setVisibility(View.VISIBLE);
            toastUtil.hidePage();
        } else {
            list_chat_search.setVisibility(View.GONE);
            toastUtil.showToast(view_toast_chat_search,
                    PageToastUtil.PageMode.CONTACT_NOTFUND);
        }
    }

    /**
     * @return void
     * @Title: setContactData
     * @param:
     * @Description: 填充联系人结果集
     */
    private void setContactData(final List<ImUserBean> contacts) {
        if (contacts == null)
            return;
        if (adapterContact == null) {
            adapterContact = new CommonAdapter<ImUserBean>(this, contacts,
                    R.layout.item_friend) {
                @Override
                public void convert(ViewHolder helper, ImUserBean item) {
                    setConvertData(helper, item);
                }
            };
            list_chat_search.setAdapter(adapterContact);
        } else {
            adapterContact.notifyDataSetChanged();
        }
    }

    /**
     * @return void
     * @Title: setGroupData
     * @param:
     * @Description: 填充群组结果集
     */
    private void setGroupData() {
        if (groups == null)
            return;
        if (adapterGroup == null) {
            adapterGroup = new CommonAdapter<ChatGroupBean>(this, groups,
                    R.layout.item_friend) {
                @Override
                public void convert(ViewHolder helper, ChatGroupBean item) {
                    setConvertData(helper, item);
                }
            };
            list_chat_search.setAdapter(adapterGroup);
        } else {
            adapterGroup.notifyDataSetChanged();
        }
    }

    /**
     * @return void
     * @Title: setConvertData
     * @param:
     * @Description: 设置子项内容
     */
    protected void setConvertData(ViewHolder helper, final Object obj) {
        String name = "";
        String avatar = "";
        if (obj instanceof ChatGroupBean) {
            ChatGroupBean bean = (ChatGroupBean) obj;
            name = bean.getData().getGroupName() + "("
                    + bean.getData().getNowCnt() + ")";
            avatar = bean.getData().getPhotoUrl();
        } else if (obj instanceof ImUserBean) {
            ImUserBean bean = (ImUserBean) obj;
            name = bean.getName();
            avatar = bean.getAvatar();
        }
        helper.getView(R.id.letterIndex).setVisibility(View.GONE);
        helper.getView(R.id.line2_item_friend).setVisibility(View.GONE);
        helper.getView(R.id.chat).setVisibility(View.GONE);
        helper.setText(R.id.name, name);
        helper.setAvatarImageByUrl(R.id.avatar, avatar);
        helper.getView(R.id.avatar).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                } else
                    lookInfo();
            }
        });
        helper.getConvertView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenSoftInput(search_chat_search);
                if (isEdit) {
                    if (obj instanceof ImUserBean) {
                        Intent intent = new Intent();
                        intent.putExtra(SEARCH_ITEM_CONTACT, (ImUserBean) obj);
                        setResult(RESULT_OK, intent);
                        ChatSearchActvity.this.finish();
                    }
                } else {
                    if (obj instanceof ChatGroupBean) {
                        ChatGroupBean bean = (ChatGroupBean) obj;
                        ChatUtil.gotoChatRoom(ChatSearchActvity.this, bean);
                    } else if (obj instanceof ImUserBean) {
                        ImUserBean bean = (ImUserBean) obj;
                        ChatUtil.gotoChatRoom(ChatSearchActvity.this, bean);
                    }

                }

            }
        });
    }

    /**
     * @return void
     * @Title: lookInfo
     * @param:
     * @Description: 查看详情
     */
    protected void lookInfo() {

    }

    public class SearchCallable<T> implements Callable<T> {
        private int searchMode = 0;
        private String searchTxt;

        public SearchCallable(int searchMode, String searchTxt) {
            this.searchMode = searchMode;
            this.searchTxt = searchTxt;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T call() throws Exception {
            switch (searchMode) {
                case SEARCH_CONTACT:
                    if (remoteMembeBean == null)
                        return (T) ChatContactManager.getInstance().getContactsByName(searchTxt);
                    else {
                        SparseArray<ContactBean> membsers = remoteMembeBean
                                .getMembsers();
                        ArrayList<ImUserBean> imUserBeans = new ArrayList<ImUserBean>();
                        int size = membsers.size();
                        for (int i=0;i<size;i++) {
                            if (searchTxt.length() > 0) {
                                ContactBean bean = membsers.get(membsers.keyAt(i));
                                String searchCase =bean.getShowName();
                                if (searchCase != null
                                        && searchCase.toLowerCase().contains(searchTxt
                                        .toLowerCase())) {
                                    ImUserBean imUserBean = new ImUserBean();
                                    imUserBean.setAvatar(bean.getUserImg());
                                    imUserBean.setMxId(String.valueOf(bean
                                            .getFriendID()));
                                    imUserBean.setName(bean.getShowName());
                                    imUserBean.setRoleType(bean.getRole());
                                    imUserBean.setRemark(bean.getRemarkName());
                                    imUserBean.setGender(i);
                                    imUserBeans.add(imUserBean);
                                }
                            }
                        }
                        return (T) imUserBeans;
                    }
                case SEARCH_GROUP:
                    return (T) ChatGroupManager.getInstance().getGroupListByName(searchTxt);
                default:
                    break;
            }
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        loadPoor.shutdown();
        loadPoor = null;
        ShowUtil.hideSoftWindow(this, search_chat_search);
        super.onDestroy();
    }
}
