/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chat.session.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.imlibrary.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chat.base.BaseActivity;
import chat.base.IMClient;
import chat.card.bean.GiveCardBean;
import chat.common.config.Constant;
import chat.common.util.CommonUtil;
import chat.common.util.file.FileOpreation;
import chat.common.util.input.EditTextUtils;
import chat.common.util.output.ShowUtil;
import chat.common.util.sys.FullScreenUtil;
import chat.dialog.CustomBaseDialog;
import chat.image.ChatImageLoader;
import chat.image.ImageUploadEntity;
import chat.image.ImageUtils;
import chat.image.activity.PhotoWallActivity;
import chat.listener.MXStanzaListener;
import chat.manager.ChatGroupManager;
import chat.manager.ChatLoadManager;
import chat.manager.ChatMessageManager;
import chat.manager.ChatNotifyManager;
import chat.manager.ChatSessionManager;
import chat.manager.ChatUploadManager;
import chat.manager.RecordManager;
import chat.manager.XmppServerManager;
import chat.manager.XmppSessionManager;
import chat.media.CameraUtil;
import chat.packet.PacketManager;
import chat.service.MessageInfoReceiver;
import chat.service.MessageReciveObserver;
import chat.session.MessageBuilder;
import chat.session.adapter.MsgAdapter;
import chat.session.adapter.common.ImAdapterDelegate;
import chat.session.adapter.common.ImViewHolder;
import chat.session.audio.MessageAudioControl;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.IMChatBaseBody;
import chat.session.bean.IMChatImageBody;
import chat.session.bean.ImUserBean;
import chat.session.bean.MessageBean;
import chat.session.enums.MsgDirectionEnum;
import chat.session.enums.MsgStatusEnum;
import chat.session.enums.MsgTypeEnum;
import chat.session.enums.SessionTypeEnum;
import chat.session.group.activity.ChatGroupInfoAcitivty;
import chat.session.group.bean.IMSGroupBaseBean;
import chat.session.observe.MessageClient;
import chat.session.observe.Subcriber;
import chat.session.util.ChatFaceUtils;
import chat.session.util.ChatUtil;
import chat.session.util.IMTypeUtil;
import chat.session.util.ListViewUtil;
import chat.session.util.MenuUtil;
import chat.session.viewholder.MsgViewHolderFactory;
import chat.view.MicrophoneImageView;
import chat.view.PasteEditText;
import chat.view.pullview.PullToRefreshLayout;
import chat.view.pullview.Pullable;
import chat.view.pullview.PullableListView;

/**
 * 聊天页面
 */
@SuppressLint("ShowToast")
public class ChatActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener, OnTouchListener, OnLongClickListener,
        RecordManager.RecordStateListener, MessageReciveObserver, SensorEventListener,
        OnFocusChangeListener, PullToRefreshLayout.OnRefreshListener, XmppServerManager.OnMessageSendListener,
        OnScrollListener, CameraUtil.OnCameraLitener, ChatUploadManager.OnUploadListener, ImAdapterDelegate {
    public static final String SESSION_DATA = "SESSION_DATA";
    public static final String CHAT_CARD = "CHAT_CARD";
    public static final String FOLLOW_STATE = "FOLLOW_STATE";// 粉丝状态
    public static final int UPDATE_VOICE = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_VIEW_PHOTO = 20;
    public static final int REQUEST_TRANSPOND = 22;// 转发
    public static final int REQUEST_SETTING = 23;// 设置
    public static final int REQUEST_USRINFO = 24;// 查看详情
    public static final int REQUEST_CARD = 25;// 卡券
    public static final String COPY_IMAGE = "MOPALCOPE";
    //1==MessageBean 2==Pictures
    public static final String TRANSPOND_TYPE = "transpond_type";
    public static final String MESSAGEBEAN = "IMMessageBean";
    public static final String TRANSPOND_PIC_PATH = "path";
    public static final String TOID = "toID";
    public final static String ACTION_CARD_CHAT = "ACTION_CARD_CHAT"; //监听卡包转赠
    protected static final int RESEND_MSG = 103;
    protected static final int FOLLOW_UPDATE = 104;
    private static final int CHAT_ADAPTER_NOTIFY = 100;
    private static final int CHAT_LIST_TO_LAST = CHAT_ADAPTER_NOTIFY + 1;
    private static final int CHAT_LIST_TO_DEFAULT = CHAT_LIST_TO_LAST + 1;
    private final static int MAX_INPUT_NUM = 2000;
    private static final int MAX_SELECT_PIC = 6;// 最大选择图片数
    public static List<String> deleteMsg = null;//删除的消息队列
    public static ChatActivity chatActivity;
    private static int position;
    private final int UP_MOVE_CHECK_NUM = 80;
    public String playMsgId;
    public String userid;
    private String toID = "";
    private PullableListView msgListView; //消息列表
    private PasteEditText editText; //文本输入框
    private View sendBtn;//发送
    private View emojiIconContainer;
    private LinearLayout btnContainer;
    private View more;
    private ClipboardManager clipboard;
    private InputMethodManager manager;
    private String toChatUsername;// 给谁发送消息
    //    private MessageAdapter adapter;
    private MsgAdapter adapter;
    private File cameraFile;
    private List<MessageBean> msgBeans;
    private String fromID;
    private ChatFaceUtils chatFaceUtils;
    private ViewPager mViewPager;
    private CheckBox cb_inputType, cb_emoticons;//切换语音文本,选择表情
    private ImageView btnMore;//发送图片 位置
    private LinearLayout loy_voice;
    private FrameLayout loy_text;
    private View voiceWindowView;//选择录音
    private PopupWindow voiceRecordWindow;
    private RecordManager recordManager = null;
    private MicrophoneImageView micImage;
    private TextView recordingHint;//录音动画的文本
    private int timeLen = 0;
    private TextView speakBtn;
    private ImageView rubishVoice;
    private TextView recording_time;//录音时间状态
    private int lastY = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private MediaPlayer mediaPlayer;
    private ImUserBean imUser;
    private int boxType = IMTypeUtil.BoxType.SINGLE_CHAT;
    private MessageBean sessionBean;
    private TextView tv_tip;
    private boolean hasChange = false;//名称是否改变
    private ImageView back, rightImg;
    private PullToRefreshLayout ph_chat;
    private View head_view;
    private long currentTime = -1;
    private RunnableSelection runnableSelection;
    private boolean responseNewMsg = false;
    private List<MessageBean> tMsg;
    private ImageView clear_chat;
    private boolean chatWithShop = false;
    private int audioMode = 0;//记录用户之前的声音播放模式，以便退出页面时恢复。
    private boolean speakerphoneOn = false;
    private TextView tv_follow_first;
    private boolean isLoading = false; //是否正在加载 ,避免重复加载
    private View view_chat_bottom, right_title;
    private boolean isHasMore = true;
    //    private GiveCardDialog giveCardDialog = null;
    private TextView title;//标题
    private MessageBean cardBean;
    private CameraUtil cameraUtil;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_VOICE:
                    adapter.notifyDataSetChanged();
                    break;
                case RESEND_MSG:
                    try {
                        final MessageBean bean = (MessageBean) msg.obj;
                        MXStanzaListener mxStanzaListener = XmppSessionManager
                                .getInstance().getMXStanzaListener();
                        if (mxStanzaListener != null) {
                            if (mxStanzaListener.isQueueOn(bean.getSessionId(), "", bean.getMsgCode())) {
                                ChatLoadManager.getInstance().reSendMsg(bean, false, ChatActivity.this);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FOLLOW_UPDATE:
                    adapter.notifyDataSetChanged();
                    if (msgListView != null && msgListView.getCount() > 0) {
                        int lastIndex = msgListView.getCount() - 1;
                        // 只有发送的消息,或则可视位置为最后一条， 才需要定位到最后一条
                        msgListView.smoothScrollToPosition(lastIndex);
                        if (msgBeans != null) {
                            setToastPage(msgBeans.size() > 0);
                        }
                    }
                    break;
                case CHAT_ADAPTER_NOTIFY:
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case CHAT_LIST_TO_LAST:
                    msgListView.setSelection(adapter.getCount() - 1);
                    break;
                case CHAT_LIST_TO_DEFAULT:
                    msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 监听打招呼的消息
     */
    private BroadcastReceiver receiverFollow = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent data) {
            if (data != null) {
                String action = data.getAction();
                if (ChatUtil.ACTION_FOLLOW.equals(action)) {
                    MessageBean messageBean = (MessageBean) data
                            .getSerializableExtra("messageBean");
                    if (msgBeans != null) {
                        if (!messageBean.getSessionId().equals(
                                sessionBean.getSessionId()))
                            return;
                        msgBeans.add(messageBean);
                        handler.sendEmptyMessage(FOLLOW_UPDATE);
                    }
                } else {
                    if (ACTION_CARD_CHAT.equals(data.getAction())) {
                        MessageBean cardBean = (MessageBean) data.getSerializableExtra("cardBean");
                        String str = data.getStringExtra("says");
                        boolean isFromChat = data.getBooleanExtra("isFromChat", false);
                        if (isFromChat) {
                            if (!TextUtils.isEmpty(str)) {
                                sendText(toID, str);
                                setToastPage(msgBeans.size() > 0);
                            }
                        } else {
                            if (cardBean != null && !msgBeans.contains(cardBean)) {
                                msgBeans.add(cardBean);
                                adapter.notifyDataSetChanged();
                            }
                            setToastPage(msgBeans.size() > 0);
                        }
                    }
                }
            }
        }
    };
    private boolean isScrolling = false;//是否正在滑动
    private int firstVisibleItem, visibleItemCount;//第一个可见未知，所有可见个数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatActivity = this;
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("cameraFile");
            if (path != null) {
                cameraFile = new File(path);
            }
        }
        recordManager = new RecordManager(this.getApplicationContext());
        setContentView(R.layout.activity_chat);
        responseNewMsg = false;
        MessageInfoReceiver.registerReceiverHandler(this);
        doRegisterForFollow();
        initView();
        setUpView();
        initEvents();
        MessageClient.getInstance().register(this);
    }

    /**
     * 得到Intent的数据
     */
    private void getIntentData() {
        sessionBean = (MessageBean) getIntent().getSerializableExtra(
                SESSION_DATA);
        if (sessionBean != null) {
            toID = sessionBean.getTo();
            boolean isTran = getIntent().getBooleanExtra("isTran", false);
            if (isTran) {
                resetSendMsg(sessionBean);
            }
            GiveCardBean giveCardBean = (GiveCardBean) getIntent()
                    .getSerializableExtra(CHAT_CARD);
            if (giveCardBean != null) {
                showCardDialog(giveCardBean, sessionBean, false);
            }
            boxType = sessionBean.getSessionType().getValue();
            rightImg.setVisibility(View.VISIBLE);
            if (boxType == SessionTypeEnum.GROUPCHAT.getValue()) {
                this.findViewById(R.id.btn_card).setVisibility(View.GONE);
                rightImg.setImageResource(R.drawable.ic_set_group);
                checkGroupIsValid(sessionBean.getChatWith());
                if (sessionBean.getChatGroupBean() != null) {
                    imUser = sessionBean.getChatGroupBean().getImUserBean();
                } else {
                    imUser = new ImUserBean();
                    imUser.setAvatar(sessionBean.getChatGroupBean().getData()
                            .getPhotoUrl());
                    imUser.setName(sessionBean.getChatGroupBean().getData()
                            .getGroupName());
                }
            } else {
                rightImg.setImageResource(R.drawable.ic_mochat_setting);
                imUser = sessionBean.getImUserBean();
            }
            toChatUsername = imUser.getName();
        }
        userid = IMClient.getInstance().getSSOUserId();
        fromID = userid;
    }

    @Override
    protected void initData() {
        getIntentData();
        XmppSessionManager.getInstance().addOnMessageSendListener(this);
        chatFaceUtils = new ChatFaceUtils(this, editText, mViewPager);
        chatFaceUtils.showEmotPager(0);
        msgBeans = new LinkedList<MessageBean>();
        tMsg = new LinkedList<MessageBean>();
        runnableSelection = new RunnableSelection();
        if (sessionBean != null)
            msgBeans.addAll(loadData());
        // 初始化聊天内容
//        adapter = new MessageAdapter(this, msgBeans, audioManager, mediaPlayer,
//                handler);
        adapter = new MsgAdapter(this, msgBeans, this);
        msgListView.setAdapter(adapter);
        setToastPage(msgBeans.size() > 0);
        if (msgBeans.size() > 0) {
            position = msgBeans.size() - 1;
            setCurrentPosition();
        }
        adapter.setEventListener(viewHolderEventListener);
    }

    /**
     * @return boolean
     * @Title: checkGroupIsValid
     * @param:
     * @Description: 检测群是否有效
     */
    private void checkGroupIsValid(String roomId) {
        boolean isValid = ChatGroupManager.getInstance()
                .getGroupIsValid(roomId);
        view_chat_bottom.setVisibility(isValid ? View.VISIBLE : View.GONE);
        right_title.setVisibility(isValid ? View.VISIBLE : View.INVISIBLE);
        if (isValid) {
            rightImg.setImageResource(R.drawable.ic_set_group);
        }
    }

    /**
     * @Description: 加载数据
     */
    private List<MessageBean> loadData() {
        List<MessageBean> chatMessages = ChatLoadManager.getInstance()
                .getChatMessages(sessionBean.getSessionId(), "", currentTime);
        List<MessageBean> temp = new ArrayList<MessageBean>();
        if (chatMessages != null && chatMessages.size() > 0) {
            temp.addAll(chatMessages);
        }
        isLoading = false;
        return temp;
    }

    protected void initView() {
        mediaPlayer = new MediaPlayer();
        back = (ImageView) this.findViewById(R.id.back);
        title = (TextView) this.findViewById(R.id.titleText);
        setTitleText();
        loy_voice = (LinearLayout) findViewById(R.id.loy_voice);
        loy_text = (FrameLayout) findViewById(R.id.loy_text);
        speakBtn = (TextView) findViewById(R.id.id_chat_speak);

        voiceWindowView = LayoutInflater.from(this).inflate(
                R.layout.item_chat_voice_animation, null);
        micImage = (MicrophoneImageView) voiceWindowView
                .findViewById(R.id.mic_image);
        micImage.setVoiceBitmapResource(R.drawable.record_animate_04);
        recordingHint = (TextView) voiceWindowView
                .findViewById(R.id.recording_hint);
        rubishVoice = (ImageView) voiceWindowView
                .findViewById(R.id.rubish_voice);
        recording_time = (TextView) voiceWindowView
                .findViewById(R.id.recording_time);

        // 推出声音窗口
        voiceRecordWindow = new PopupWindow(voiceWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        voiceRecordWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        voiceRecordWindow.setTouchable(true);

        msgListView = (PullableListView) this.findViewById(R.id.list);
        msgListView.setPullToRefreshMode(Pullable.TOP);
        editText = (PasteEditText) findViewById(R.id.et_sendmessage);
        sendBtn = findViewById(R.id.btn_send);
        sendBtn.setClickable(true);
        emojiIconContainer = findViewById(R.id.view_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        btnMore = (ImageView) findViewById(R.id.btn_more);
        more = findViewById(R.id.more);
        cb_emoticons = (CheckBox) findViewById(R.id.cb_emoticons);
        cb_inputType = (CheckBox) findViewById(R.id.cb_inputselect);

        mViewPager = (ViewPager) this.findViewById(R.id.view_pager);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        editText.setOnFocusChangeListener(this);
        editText.setTag(true);
        tv_tip = (TextView) this.findViewById(R.id.tv_tip);

        rightImg = (ImageView) this.findViewById(R.id.rightImg);
        ph_chat = (PullToRefreshLayout) this.findViewById(R.id.ph_chat);
        ph_chat.setState(1);
        head_view = this.findViewById(R.id.head_view);
        head_view.setBackgroundColor(getResources().getColor(R.color.bg_color));
        clear_chat = (ImageView) this.findViewById(R.id.clear_chat);
        tv_follow_first = (TextView) this.findViewById(R.id.tv_follow_first);
        view_chat_bottom = this.findViewById(R.id.view_chat_bottom);
        right_title = this.findViewById(R.id.right_title);
    }

    /**
     * @Description: 设置提示页样式
     */
    public void setToastPage(boolean hasData) {
        tv_tip.setVisibility(hasData ? View.GONE : View.VISIBLE);
        ph_chat.setVisibility(View.VISIBLE);
        if (msgBeans != null && msgBeans.size() > 0) {
            msgListView.setPullToRefreshMode(Pullable.TOP);
        } else {
            msgListView.setPullToRefreshMode(Pullable.NONE);
        }
    }

    private void checkInputNum() {
        if (editText != null) {
            String textLen = editText.getText().toString();
            if (textLen.length() >= MAX_INPUT_NUM) {
                Toast.makeText(
                        ChatActivity.this,
                        ChatActivity.this
                                .getString(R.string.chat_send_text_maxnum_tip),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatActivity = null;// 静态对象在页面销毁时置空防止内存泄露。
        ChatLoadManager.getInstance().removeChatActivity();
        EditTextUtils.hideSoftKeyboard(ChatActivity.this, manager);
        MessageInfoReceiver.unregisterReceiverHandler(this);
        unregisterReceiver(receiverFollow);
        chatFaceUtils.clearCache();
        recordManager = null;
        if (msgListView != null) {
            currentTime = -1;
            msgBeans.clear();
            msgBeans = null;
            msgListView.destroyDrawingCache();
            msgListView = null;
        }
        if (micImage != null) {
            micImage.clearMemory();
        }
//        if (adapter != null) {
//            adapter.clearCache();
//        }

        ChatImageLoader.clearMemory();
        XmppSessionManager instance2 = XmppSessionManager.getInstance();
        instance2.removeOnMessageSendListener(this);
        handler.removeCallbacksAndMessages(null);
        deleteMsg = null;
        chatActivity = null;
        FullScreenUtil.init(this).release();
        PacketManager.getInstance().onDestory();
        if (cameraUtil != null) {
            cameraUtil.onDestory();
            cameraUtil = null;
        }
        MenuUtil.getInstance().clear();
        MessageClient.getInstance().unregister(this);
        System.gc();
    }

    /**
     * 插入新数据，并更新
     */
    @Subcriber
    private boolean insertNewMessage(MessageBean message) {
        boolean isInsert = true;
        if (editText != null) {
            editText.setText("");
        }
        if (msgBeans != null && sessionBean.getSessionId().equals(message.getSessionId())) {
            msgBeans.add(message);
            adapter.notifyDataSetChanged();
            position = msgBeans.size() - 1;
            if (message.getDirect() == MsgDirectionEnum.Out)
                setCurrentPosition();
            setToastPage(msgBeans.size() > 0);
        }
        return isInsert;
    }

    private void setUpView() {
        clipboard = (ClipboardManager) this.getApplicationContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 消息图标点击事件
     */
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_send) {
            String s = editText.getText().toString();
            sendText(toID, s);
            editText.setText("");

        } else if (i == R.id.btn_picture) {
            selectPicFromLocal();

        } else if (i == R.id.btn_video) {
            if (cameraUtil != null) {//释放之前生成的资源
                cameraUtil.onDestory();
            }
            //需要重新创建一个工具类，不然视频录制不能连续(还不太清楚具体原因，后续有时间再定位下，初步怀疑是录制节点有缓存)
            cameraUtil = new CameraUtil(this, sessionBean.getSessionId());
            cameraUtil.showCameraWindow(this);

        } else if (i == R.id.btn_packet) {
            PacketManager.getInstance().showPacketDialog(this);

//            case R.id.btn_location:// 打开地图
//                BaiduMapActivity.goToMap(this);
//                break;
        } else if (i == R.id.back) {
            EditTextUtils.hideSoftKeyboard(ChatActivity.this, manager);
            back();

        } else if (i == R.id.loy_text || i == R.id.et_sendmessage) {
            ShowUtil.showSoftWindow(this, editText);
            more.setVisibility(View.GONE);
            FullScreenUtil.init(this);

        } else if (i == R.id.btn_card) {
            choiceCard();

        } else if (i == R.id.clear_chat) {
            editText.setText("");

        } else if (i == R.id.right_title) {
            goToChatSet();

        } else {
        }
    }

    private void choiceCard() {
//        Intent intent = new Intent(this, CardActivity.class);
//        intent.putExtra("activityType", 2);
//        intent.putExtra("imUser", imUser);
//        startActivityForResult(intent, REQUEST_CARD);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null && cameraFile != null) {
            outState.putString("cameraFile", cameraFile.getAbsolutePath());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent = new Intent(this, PhotoWallActivity.class);
        intent.putExtra(PhotoWallActivity.KEY_IS_SHOW_RECENTLY, true);// 是否显示最近图片
        intent.putExtra(PhotoWallActivity.KEY_LIMIT_RECENTLY_SHOW_NUM, 100);// 最新图片显示最大数目
        intent.putExtra(PhotoWallActivity.KEY_LIMIT_SELECTED_NUM,
                MAX_SELECT_PIC);// 可以的最大图片数目
        startActivityForResult(intent,
                PhotoWallActivity.REQUEST_CODE_SELECT_PHOTO);
    }

    private void sendText(String toID, String content) {
        MessageBean textMessage = MessageBuilder.createTextMessage(toID, sessionBean.getSessionType(), content);
        if (insertNewMessage(textMessage))
            sendChatMessage(textMessage, textMessage.getSendBody());
    }

    /**
     * 发送GIF
     */
    public void sendGif(String name) {
        MessageBean gifMessage = MessageBuilder.createGifMessage(toID, sessionBean.getSessionType(), name);
        if (insertNewMessage(gifMessage))
            sendChatMessage(gifMessage, gifMessage.getSendBody());
    }

    /**
     * 发送GIF
     */
    public void sendGif(String toID, String gifName) {
        MessageBean gifMessage = MessageBuilder.createGifMessage(toID, sessionBean.getSessionType(), gifName);
        if (insertNewMessage(gifMessage))
            sendChatMessage(gifMessage, gifMessage.getSendBody());
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    public void sendPicture(String toID, final String filePath, String url) {
        MessageBean picMessage = MessageBuilder.createImageMessage(toID, sessionBean.getSessionType(), new File(filePath), url);
        if (insertNewMessage(picMessage)) {
            if (TextUtils.isEmpty(url)) {
                ChatUploadManager.getInstance().uploadFile(picMessage, this);
            } else
                sendChatMessage(picMessage, picMessage.getSendBody());
        }
    }

    /**
     * @return void
     * @Title: sendVoice
     * @Description: 发送语音
     */
    private void sendVoice(String toID, String filePath) {
        MessageBean voiceMessage = MessageBuilder.createAudioMessage(toID, sessionBean.getSessionType(), timeLen, filePath);
        if (insertNewMessage(voiceMessage)) {
            ChatUploadManager.getInstance().uploadFile(voiceMessage, this);
        }
    }

    /**
     * @Description: 发送视频
     */
    private void sendVideo(String toID, String filePath) {
        MessageBean videoMessage = MessageBuilder.createVideoMessage(toID, sessionBean.getSessionType(), filePath, "");
        if (insertNewMessage(videoMessage))
            ChatUploadManager.getInstance().uploadFile(videoMessage, this);
    }

    public synchronized void sendChatMessage(MessageBean message, String content) {
        if (message == null || content == null)
            return;
        XmppSessionManager.getInstance().sendMessage(
                message.getTo(), content, message);
    }

    /**
     * 聊天设置
     */
    public void goToChatSet() {
        if (sessionBean == null)
            return;
        Intent intent = new Intent();
        if (boxType == IMTypeUtil.BoxType.GROUP_CHAT) {
            intent.setClass(this, ChatGroupInfoAcitivty.class);
            intent.putExtra(ChatGroupInfoAcitivty.ROOM,
                    sessionBean.getChatWith());
            intent.putExtra(ChatGroupInfoAcitivty.SESSION_ID,
                    sessionBean.getSessionId());
        } else {
            intent.setClass(this, ChatSettings.class);
            intent.putExtra(TOID, toID);
            intent.putExtra(SESSION_DATA, sessionBean);
        }
        startActivityForResult(intent, REQUEST_SETTING);
    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void more(View view) {
        if (more.getVisibility() == View.GONE) {
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
            loy_voice.setVisibility(View.GONE);
            loy_text.setVisibility(View.VISIBLE);
            editText.requestFocus();
            EditTextUtils.hideSoftKeyboard(ChatActivity.this, manager);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        IMClient.isChatPage = true;
        if (tMsg.size() > 0) {
            if (msgBeans != null)
                msgBeans.addAll(tMsg);
            msgListView.smoothScrollToPosition(msgListView.getCount() - 1);
            adapter.notifyDataSetChanged();
            if (msgBeans != null) {
                setToastPage(msgBeans.size() > 0);
            }
        }
        responseNewMsg = true;
        tMsg.clear();
        if (deleteMsg != null && msgBeans != null) {
            for (int i = 0; i < deleteMsg.size(); i++) {
                for (int j = 0; j < msgBeans.size(); j++) {
                    if (msgBeans.get(j).getMsgCode().equals(deleteMsg.get(i))) {
                        msgBeans.remove(j);
                        break;
                    }
                }
            }
            deleteMsg.clear();
            deleteMsg = null;
            adapter.notifyDataSetChanged();
            setToastPage(msgBeans.size() > 0);
        }
        ChatNotifyManager.getInstance().clearNotify();
        updateUnreadNum();
    }

    /**
     * @Description:更新未读数
     */
    private void updateUnreadNum() {
        if (sessionBean != null) {
            String sessionId = sessionBean.getSessionId();
            ChatMessageManager.getInstance().updateMessageAllReaded(sessionId, "");
            ChatSessionManager.getInstance().updateSessionUnReadAll(sessionId);
            ChatUtil.sendUpdateNotify(this,
                    MessageInfoReceiver.EVENT_UPDATE_READ_NUMBERS,
                    sessionId, sessionId);
            if (sessionBean.getDirect() == MsgDirectionEnum.In) {
                ChatSessionManager.getInstance().updateSessionReadStatus(
                        sessionBean.getMsgCode(), 1);
            }
        }
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
        try {
            if (recordManager.isRunning()) { // 停止录音
                recordManager.stopRecord();
                hideVoiceWindow();
            }
//            adapter.stopVoice(); // 停止声音播放
        } catch (Exception e) {
        }
        if (editText != null)// 按home键隐藏软键盘
            hiddenSoftInput(editText);
        IMClient.isChatPage = false;
        if (cameraUtil != null) {
            cameraUtil.onPause();
        }
    }

    public boolean back() {
        if (more != null && more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            return false;
        }
        if(hasChange){
            //todo
        }
        this.finish();
        return hasChange;
    }

    @Override
    public void onBackPressed() {
        if (!back())
            return;
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username =  chat.common.util.TextUtils.getString(intent
                .getStringExtra("nickname"));
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    protected void initEvents() {
        cb_emoticons.setOnCheckedChangeListener(this);
        cb_inputType.setOnCheckedChangeListener(this);
        recordManager.setVoiceVolumeListener(this);
        loy_voice.setOnLongClickListener(this);
        loy_voice.setOnTouchListener(this);
        msgListView.setOnTouchListener(this);
        editText.addTextChangedListener(new TextListener());
        ph_chat.setOnRefreshListener(this);
        clear_chat.setOnClickListener(this);
        tv_follow_first.setOnClickListener(this);
        right_title.setOnClickListener(this);
        back.setOnClickListener(this);
        msgListView.setOnScrollListener(this);
    }

    /**
     * 表情选择 输入法切换
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        EditTextUtils.hideSoftKeyboard(this, manager);
        int i = buttonView.getId();
        if (i == R.id.cb_emoticons) {
            if (isChecked) {
                cb_inputType.setChecked(true);
                more.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                emojiIconContainer.setVisibility(View.VISIBLE);
                loy_voice.setVisibility(View.GONE);
                loy_text.setVisibility(View.VISIBLE);
                editText.requestFocus();
                EditTextUtils.hideSoftKeyboard(ChatActivity.this, manager);
            } else {
                btnContainer.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
            }

        } else if (i == R.id.cb_inputselect) {
            if (isChecked) {
                loy_voice.setVisibility(View.GONE);
                loy_text.setVisibility(View.VISIBLE);
                editText.requestFocus();
                manager.showSoftInput(editText,
                        InputMethodManager.SHOW_IMPLICIT);
                more.setVisibility(View.GONE);
            } else {
                loy_voice.setVisibility(View.VISIBLE);
                loy_text.setVisibility(View.GONE);
                editText.clearFocus();
                more.setVisibility(View.GONE);
                EditTextUtils.hideSoftKeyboard(ChatActivity.this, manager);
            }

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int i = v.getId();
        if (i == R.id.list) {
            more.setVisibility(View.GONE);
            emojiIconContainer.setVisibility(View.GONE);
            btnContainer.setVisibility(View.GONE);
            EditTextUtils.hideSoftKeyboard(ChatActivity.this, manager);
            return false;
        } else if (i == R.id.loy_voice) {
            return speaking(v, event);
        }
        return false;
    }

    private boolean speaking(View v, MotionEvent event) {
        if (CommonUtil.isExitsSdcard()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (recordManager.isRunning()) {
                    boolean upMove = upMove((int) event.getY());
                    if (upMove) {
                        recordingHint
                                .setText(getString(R.string.chat_send_cancel));
                        rubishVoice.setVisibility(View.VISIBLE);
                        recording_time.setVisibility(View.INVISIBLE);
                        recordingHint.setTextColor(Color.RED);
                        micImage.setVisibility(View.INVISIBLE);
                    } else {
                        recordingHint
                                .setText(getString(R.string.chat_record_up_to_cancal));
                        recordingHint.setTextColor(Color.WHITE);
                        rubishVoice.setVisibility(View.INVISIBLE);
                        recording_time.setVisibility(View.VISIBLE);
                        micImage.setVisibility(View.VISIBLE);
                    }
                    return true;
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                lastY = 0;
                if (voiceRecordWindow != null && voiceRecordWindow.isShowing()) {
                    voiceRecordWindow.dismiss();
                }
                recordingHint
                        .setText(getString(R.string.chat_record_up_to_cancal));

                if (recordManager.isRunning()) {
                    if (rubishVoice.getVisibility() == View.VISIBLE) {
                        recordManager.cancel();
                        hideVoiceWindow();
                    } else {
                        recordManager.stopRecord();
                        hideVoiceWindow();
                    }
                }
                rubishVoice.setVisibility(View.INVISIBLE);
                speakBtn.setText(getString(R.string.chat_record_to_speak));
            }
        }
        return false;
    }

    public boolean upMove(int y) {
        if ((lastY - y) > UP_MOVE_CHECK_NUM) {
            return true;
        }
        return false;
    }

    public void showVoiceWindow() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (voiceRecordWindow != null && !voiceRecordWindow.isShowing()) {
                    voiceRecordWindow.showAtLocation(
                            getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                }
            }
        });
    }

    public void hideVoiceWindow() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (voiceRecordWindow != null && voiceRecordWindow.isShowing()) {
                    voiceRecordWindow.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        if (!checkPermission()) {
            ShowUtil.showToast(this, getString(R.string.no_record_permission));
            speakBtn.setText(getString(R.string.chat_record_to_speak));
            return true;
        }
        if (adapter == null)
            return false;
//        adapter.stopVoice(); // 停止声音播放
        micImage.setVisibility(View.VISIBLE);
        speakBtn.setText(getString(R.string.chat_record_up_send));
        if (!recordManager.isRunning()) {
            String cacheName = Constant.VOICES_FOLDER + toID + "_" + fromID
                    + File.separator;
            FileOpreation.newFolder(cacheName);
            recordManager.startRecord(cacheName);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRecordStartLoading() {
        showVoiceWindow();
    }

    @Override
    public void onRecordStart() {
        showVoiceWindow();
        timeLen = 1;

    }

    /**
     * 录音完成回调
     */
    public void onRecordFinish(String path) {
        hideVoiceWindow();
        File voiceFile = new File(path);
        if (voiceFile.exists() && voiceFile.length() > 0) {
            sendVoice(toID, path);
        } else {
            ShowUtil.showToast(this, getString(R.string.do_fail));
        }
    }

    /**
     * 消息转发
     */
    public void resetSendMsg(MessageBean imbean) {
        if (imbean == null)
            return;
        SessionTypeEnum sessionType = imbean.getSessionType();
        switch (sessionType) {
            case NORMAL:
            case GROUPCHAT:
                switch (imbean.getMsgType()) {
                    case audio:
                        IMChatAudioBody audioBody =imbean.getAttachment();
                        sendVoice(imbean.getChatWith(), audioBody.getAttr1());
                        break;
                    case image:
                        IMChatImageBody imgBody =  imbean.getAttachment();
                        sendPicture(imbean.getChatWith(), imgBody.getLocalUrl(), imgBody.getAttr1());
                        break;
                    case gif:
                        IMChatBaseBody baseBody = imbean.getAttachment();
                        sendGif(imbean.getChatWith(), baseBody.getAttr1());
                        break;
                    case text:
                        sendText(imbean.getChatWith(), imbean.getSession());
                        break;
                }
                break;
            case RICH:
                break;
            default:
                break;
        }

    }

    @Override
    public void onRecordCancel() {
        speakBtn.setText(getString(R.string.chat_record_to_speak));
        hideVoiceWindow();
    }

    @Override
    public void onRecordVoiceChange(int v) {
        double percent = 0;
        if (v > 500) {
            percent = v / 10000d;
            percent = (percent > 1) ? 1 : percent;
        }
        if (percent > 0) {
            micImage.setVoicePercent(percent);
        }
    }

    @Override
    public void onTimeChange(int millseconds) {
        int seconds = millseconds / 1000;
        recording_time.setText(String.valueOf(seconds));
        recording_time.setTextColor(Color.WHITE);
        if (seconds > 50) {
            recording_time.setTextColor(Color.RED);
            recordingHint.setText(R.string.chat_title_recordtime_tip);
            recordingHint.setTextColor(Color.RED);
        }
        if (seconds >= 60) {
            timeLen = 60;
            recording_time.setText("60");
            recordManager.stopRecord();
        } else {
            timeLen = seconds;
        }
    }

    @Override
    public void onRecordError() {
        ShowUtil.showToast(this, getString(R.string.chat_record_failed));
    }

    /**
     * 录音时长太短
     */
    public void onTooShoot() {
        hideVoiceWindow();
        View view = LayoutInflater.from(this).inflate(
                R.layout.item_chat_ontooshoot, null);
        Toast toast = new Toast(this);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(2000);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoWallActivity.REQUEST_CODE_SELECT_PHOTO: {
                    try {// 存放所选照片路径的list
                        if (data != null) {
                            ArrayList<String> photoPaths = data
                                    .getStringArrayListExtra(PhotoWallActivity.KEY_SELECTED_PHOTOS);
                            if (photoPaths != null && photoPaths.size() > 0) {
                                if (photoPaths.size() > 1) {
                                    new FixPictureTasks(photoPaths).execute();
                                } else {
                                    String photoPath = photoPaths.get(0);
                                    System.out.println("photo_path:" + photoPath);
                                    ImageUtils.fixPicture(photoPath);
                                    sendPicture(toID, photoPath, "");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();// 没有选择图片
                    }
                }
                break;
                case REQUEST_CODE_CAMERA:
                    if (cameraFile != null && cameraFile.exists()) {
                        String imageUri = cameraFile.getAbsolutePath();
                        ImageUtils.fixPicture(imageUri);
                        sendPicture(toID, imageUri, "");
                    } else {
                        ShowUtil.showToast(this,
                                getString(R.string.chat_send_pic_fail));
                    }
                    break;
                case REQUEST_CODE_VIEW_PHOTO:
                    if (data != null) {
                        // 从ShowBigPhoto 返回
                    }
                    break;
                case REQUEST_TRANSPOND:
                    // 转发成功后的操作
                    if (data != null) {
                        MessageBean bean = (MessageBean) data
                                .getSerializableExtra(ChatActivity.MESSAGEBEAN);
                        if (bean != null) {
                            int type = boxType;// 保存当前的类型
                            boxType = bean.getSessionType().getValue();
                            resetSendMsg(bean);
                            boxType = type;// 转发后还原原来的类型
                            ShowUtil.showToast(
                                    this,
                                    getString(R.string.chat_transpond_success));
                        } else {
                            ShowUtil.showToast(this,
                                    getString(R.string.chat_transpond_fail));
                        }
                    }
                    break;
                case REQUEST_SETTING:
                    try {
                        boolean isDeleteGroup = data.getBooleanExtra(
                                "isDeleteGroup", false);
                        if (isDeleteGroup) {
                            hasChange = true;
                            onBackPressed();
                        }
                        ImUserBean userinfo = (ImUserBean) data
                                .getSerializableExtra("userinfo");
                        if (userinfo != null) {
                            imUser = userinfo;
                            if (TextUtils.isEmpty(imUser.getRemark()))
                                toChatUsername = imUser.getName();
                            else
                                toChatUsername = imUser.getRemark();
                            setTitleText();
                            adapter.notifyDataSetChanged();
                        }
                        String name = data.getStringExtra("name");
                        if (name != null && !toChatUsername.equals(name)) {
                            String empty = getString(R.string.chat_group_create_name_empty);
                            if (empty.equals(name))
                                return;
                            hasChange = true;
                            toChatUsername = name;
                            setTitleText();
                        }
                        hasChange |= data.getBooleanExtra("hasChanged", false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    boolean isDeleted = data.getBooleanExtra("isDeleted", false);
                    if (isDeleted) {
                        msgBeans.clear();
                        setToastPage(false);
//                        adapter.refresh();
                    }
                    break;
                case REQUEST_USRINFO:
                    try {
                        String name = chat.common.util.TextUtils.getString(data
                                .getStringExtra("name"));
                        String unName = getResources().getString(
                                R.string.chat_group_create_name_empty);
                        boolean isGroup = data.getBooleanExtra("isGroup", false);
                        if (name.length() == 0 || unName.equals(name))
                            return;
                        if (!isGroup
                                && sessionBean.getSessionType() == SessionTypeEnum.GROUPCHAT)
                            return;
                        String empty = getString(R.string.chat_group_create_name_empty);
                        if (empty.equals(name))
                            return;
                        toChatUsername = name;
                        setTitleText();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_CARD:
                    GiveCardBean giveCardBean = (GiveCardBean) data
                            .getSerializableExtra("giveCardBean");
                    if (giveCardBean == null)
                        return;
                    MessageBean bean = (MessageBean) data
                            .getSerializableExtra("bean");
                    showCardDialog(giveCardBean, bean, true);
                    break;
                default:
                    System.out.println(requestCode);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setTitleText() {
        if (toChatUsername != null && toChatUsername.length() > 0) {
            String content = toChatUsername;
            if (toChatUsername.contains(",")) {
                content = "";
                String[] names = toChatUsername.split(",");
                List<String> nameTemps = new ArrayList<String>();
                for (int i = 0; i < names.length; i++) {
                    if (names[i] != null && !names[i].equals("null")
                            && names[i].length() > 0) {
                        nameTemps.add(names[i]);
                    }
                }
                for (int i = 0; i < nameTemps.size(); i++) {
                    if (content.length() > 0) {
                        content += ",";
                    }
                    content += nameTemps.get(i);
                }
                if (content.length() <= 0) {
                    content = getString(R.string.chat_group_create_name_empty);
                }
            }
            title.setText(content);
        }
    }

    @Override
    public void handleReciveMessage(int event, final String msgCode, final int sendStatus, String sessionId, long time, String url, String nickName, String roomId) {
        if (event == MessageInfoReceiver.EVENT_CLEAR) {
            if (sessionBean.getSessionId().equals(sessionId)) {
                msgBeans.clear();
                adapter.notifyDataSetChanged();
                setToastPage(msgBeans.size() > 0);
            }
        } else if (event == MessageInfoReceiver.EVENT_VOICE_LOADED) {
            MessageBean updateVoice = ChatLoadManager.getInstance()
                    .updateVoice(msgBeans, msgCode, url, 0);
            if (updateVoice != null) {
                ChatMessageManager.getInstance().updateMessageReadStatus(updateVoice.getSessionId(), "",
                        updateVoice.getMsgCode(), 1);
                ChatSessionManager.getInstance().updateSessionReadStatus(
                        updateVoice.getMsgCode(), 1);
                ChatSessionManager.getInstance().updateSessionUnReadAll(
                        sessionBean.getSessionId());
                adapter.notifyDataSetChanged();
            }
        } else if (event == MessageInfoReceiver.EVENT_UPDATE_GROUP_MEMBER_NICKNAME) {
            doUpdateGrupMemberName(sessionId, nickName, roomId);
        } else if (event == MessageInfoReceiver.EVENT_CHAT_READ) {
        } else if (event == MessageInfoReceiver.EVENT_CARD) {
            if (cardBean != null && !msgBeans.contains(cardBean)) {
                msgBeans.add(cardBean);
                adapter.notifyDataSetChanged();
                setToastPage(msgBeans.size() > 0);
            }
        } else if (event == MessageInfoReceiver.EVENT_UPDATE_CHAT) {
            String index = ChatLoadManager.getInstance()
                    .updateMessage(msgBeans, msgCode, sendStatus);
            if (index != null) {
                adapter.refreshCurrentItem(msgListView, Integer.valueOf(index));
            }
        } else if (event == MessageInfoReceiver.EVENT_UPDATE_GROUPINFO) {
            String groupName = ChatGroupManager.getInstance().getGroupNameById(
                    roomId);
            if (!TextUtils.isEmpty(groupName)) {
                toChatUsername = groupName;
                setTitleText();
            }
        }
    }

    /**
     * @Description: 执行刷新群成员名称更新
     */
    private void doUpdateGrupMemberName(String contactId, String nickName, String roomId) {
        if (!roomId.equals(sessionBean.getChatWith())) {
            return;
        }
        List<MessageBean> updateBeans = ChatLoadManager.getInstance()
                .updateAllNickNameById(msgBeans, contactId, nickName);
        if (updateBeans != null) {
            msgBeans = updateBeans;
            adapter.notifyDataSetChanged();
        }
    }

    public void reSendDialog(final MessageBean message) {
        CustomBaseDialog dialog = CustomBaseDialog.getDialog(ChatActivity.this, null,
                getString(R.string.chat_textview_resend),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }, this.getString(R.string.rig_sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ChatLoadManager.getInstance().reSendMsg(message, true, ChatActivity.this);
                    }
                });
        dialog.setButton1Background(R.drawable.bg_button_dialog_1);
        dialog.setButton2Background(R.drawable.bg_button_dialog_2);
        dialog.show();

    }

    public void delete(MessageBean bean) {
        ChatMessageManager.getInstance().deleteMessageById(bean.getMsgCode(),
                bean.getSessionId(), "");
        String url = "";
        switch (bean.getMsgType()) {
            case image:
                IMChatImageBody img =  bean.getAttachment();
                url = img.getLocalUrl();
                break;
            case audio:
                IMChatAudioBody audio = bean.getAttachment();
                url = audio.getLocalUrl();
                break;
            default:
                break;
        }
        msgBeans.remove(bean);
        adapter.removeUpload(url);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, getString(R.string.chat_textview_menu_delete),
                Toast.LENGTH_SHORT).show();
        setToastPage(msgBeans.size() > 0);
        if (sessionBean != null) {
            String sessionId = sessionBean.getSessionId();
            int event = MessageInfoReceiver.EVENT_UPDATE_CHAT;
            if (sessionId.contains("_")) {
                sessionId = IMTypeUtil.SessionType.MOBIZ.name();
                event = MessageInfoReceiver.EVENT_UPDATE_MOBIZ;
            }
            ChatUtil.sendUpdateNotify(this,
                    event, "", sessionId);
        }
    }

    /**
     * 复制消息
     */
    @SuppressLint("NewApi")
    public void copy(MessageBean bean) {
        if (bean.getMsgType() == MsgTypeEnum.text) {
            clipboard.setPrimaryClip(ClipData.newPlainText("simple text",
                    bean.getSession()));
        }

    }

    /**
     * 转发
     */
    public void transpond(MessageBean bean) {
        Intent intent = ChatUtil.transpond(this, bean, true);
        startActivityForResult(intent, REQUEST_TRANSPOND);
    }

    /**
     * 复制图片
     */
    public void copyPhoto(MessageBean bean) {
        Toast.makeText(this, "复制图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (range >= mSensor.getMaximumRange()) {// == 的判断不行，三星和小米的手机不适用，>=就好了。
            if (ChatUtil.getInstance().getVoiceIsEarMode())//如果为听筒模式，就不需要启用扬声器播放了
                return;
            setEarPhoneMode(false);
        } else {
            setEarPhoneMode(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        cb_emoticons.setChecked(false);
    }

    /**
     * 判断是否有录音权限
     */
    public boolean checkPermission() {
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm
                .checkPermission("android.permission.RECORD_AUDIO",
                        getPackageName()));
        return permission;
    }

    private void showCardDialog(GiveCardBean giveCardBean, MessageBean bean,
                                boolean isFromChat) {
//        if (giveCardBean == null || bean == null)
//            return;
//        giveCardDialog = null;
//        giveCardDialog = new GiveCardDialog(this, giveCardBean, bean);
//        giveCardDialog.setIsFromChat(isFromChat);
//        cardBean = bean;
//        giveCardDialog.show();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (msgBeans == null || isLoading) {
            ph_chat.refreshFinish(PullToRefreshLayout.SUCCEED);
            return;
        }
        if (msgBeans.size() > 0) {
            currentTime = msgBeans.get(0).getMsgTime();
        }
        List<MessageBean> loadData = loadData();
        final int size = loadData.size();
        if (size == 0) {
            if (msgBeans != null && msgBeans.size() > 0) {
                msgListView.setPullToRefreshMode(Pullable.NONE);
            }
            ph_chat.refreshFinish(PullToRefreshLayout.SUCCEED);
            isHasMore = false;
            return;
        }
        if (msgBeans.size() == 0) {
            ph_chat.refreshFinish(PullToRefreshLayout.SUCCEED);
            return;
        }
        ph_chat.refreshFinish(PullToRefreshLayout.SUCCEED);
        msgBeans.addAll(0, loadData);
        position = size;
        adapter.notifyDataSetChanged();
        setCurrentPosition();
        setToastPage(msgBeans.size() > 0);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSuccess(final MessageBean messageBean) {
        if (messageBean != null) {
            if (messageBean.getSessionId().equals(sessionBean.getSessionId())) {
                ChatMessageManager.getInstance().updateMessageReadStatus(messageBean.getSessionId(), "",
                        messageBean.getMsgCode(), 1);
                String sessionId = messageBean.getSessionId();
                if (sessionId.contains("_")) {
                    sessionId = IMTypeUtil.SessionType.MOBIZ.name();
                }
                ChatSessionManager.getInstance().updateSessionUnReadAll(
                        sessionId);
                ChatSessionManager.getInstance().updateSessionReadStatus(
                        messageBean.getMsgCode(), 1);
                if (responseNewMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sessionBean.getSessionType() == SessionTypeEnum.SGROUP) {// 如果为群聊，检查是否群有效
                                if (messageBean.getMsgType().getValue() == IMTypeUtil.SGroupTy.UPDATE_BASE) {//更新群昵称
                                    IMSGroupBaseBean baseBean = messageBean.getAttachment();
                                    String roomName = baseBean.getRoomName();
                                    if (!TextUtils.isEmpty(roomName)) {
                                        toChatUsername = roomName;
                                        setTitleText();
                                    }
                                } else {
                                    if (messageBean.getMsgType().getValue() == IMTypeUtil.SGroupTy.ADD_MEMBER
                                            || messageBean.getMsgType().getValue() == IMTypeUtil.SGroupTy.REMOVE_MEMBER
                                            || messageBean.getMsgType().getValue() == IMTypeUtil.SGroupTy.DISSOLVE)
                                        checkGroupIsValid(messageBean.getFrom());
                                }
                            }
                            if (msgBeans != null)
                                msgBeans.add(messageBean);
                            if (msgListView != null
                                    && msgListView.getCount() > 0) {
                                int lastIndex = msgListView.getCount() - 1;
                                if (msgListView.getLastVisiblePosition() == lastIndex
                                        || messageBean.getDirect() == MsgDirectionEnum.Out) {
                                    // 只有发送的消息,或则可视位置为最后一条， 才需要定位到最后一条
                                    msgListView
                                            .smoothScrollToPosition(lastIndex);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            if (msgBeans != null) {
                                setToastPage(msgBeans.size() > 0);
                            }
                        }
                    });

                } else {
                    tMsg.add(messageBean);
                }
            }
        }
    }

    /**
     * @Description: 设置当前显示位置
     */
    private void setCurrentPosition() {
        if (runnableSelection == null)
            runnableSelection = new RunnableSelection();
        runnableSelection.setPosition(msgListView, position);
        handler.postDelayed(runnableSelection,200);
    }

    /**
     * @Description: 注册关注广播
     */
    private void doRegisterForFollow() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatUtil.ACTION_FOLLOW);
        registerReceiver(receiverFollow, filter);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (msgListView != null && msgListView.getFirstVisiblePosition() == 0) {
            if (isHasMore)
                onRefresh(ph_chat);
        }
        switch (scrollState) {
            case SCROLL_STATE_FLING:
            case SCROLL_STATE_TOUCH_SCROLL:
                onScrollFling();
                break;
            case SCROLL_STATE_IDLE:
                onScrollIdle();
                break;
        }
    }

    /**
     * 滑动操作，停止视频播放
     */
    private void onScrollFling() {
        if (isScrolling)//避免反复操作
            return;
        isScrolling = true;
        if (adapter != null) {
//            SparseArray<ScalableVideoView> videoViews = adapter.getVideoViews();
//            if (videoViews != null) {
//                int size=videoViews.size();
//                for(int i=0;i<size;i++){
//                    ScalableVideoView videoView = videoViews.valueAt(i);
//                    videoView.pause();
//                }
//            }
        }
    }

    /***
     * 停止滑动，恢复可见视频的播放
     */
    private void onScrollIdle() {
        isScrolling = false;
        if (adapter != null) {
//            SparseArray<ScalableVideoView> videoViews = adapter.getVideoViews();
//            if (videoViews != null) {
//                int size=videoViews.size();
//                for(int i=0;i<size;i++){
//                    int position=videoViews.keyAt(i);
//                    if(position>=firstVisibleItem&&position<=firstVisibleItem+visibleItemCount-1){
//                        ScalableVideoView videoView = videoViews.get(position);
//                        videoView.start();
//                    }
//                }
//            }
        }
    }

    @Override
    public void onCameraSuccess(String filePath, long time) {
        sendVideo(toID, filePath);
    }

    @Override
    public void onUploadPre(String filePath, ImageUploadEntity upImgloadListener) {
            adapter.addUpload(filePath, upImgloadListener);
        setCurrentPosition();
    }

    @Override
    public void onUploadPost(String filePath) {
        adapter.removeUpload(filePath);
    }

    @Override
    public void onUploadFail(MessageBean messageBean) {
        if (messageBean == null || msgBeans == null)
            return;
        messageBean.setMsgStatus(MsgStatusEnum.fail);
        ChatUtil.getInstance().notifyMessageSendState(messageBean.getSessionId(), messageBean.getMsgCode(), messageBean.getMsgStatus().getValue());
        position = msgBeans.size() - 1;
        setCurrentPosition();
    }

    @Override
    public int getViewTypeCount() {
        return MsgViewHolderFactory.getViewTypeCount();
    }

    @Override
    public Class<? extends ImViewHolder> viewHolderAtPosition(int position) {
        return MsgViewHolderFactory.getViewHolderByType(msgBeans.get(position));
    }

    @Override
    public boolean enabled(int position) {
        return true;
    }

    public void setEarPhoneMode(boolean earPhoneMode) {
        MessageAudioControl.getInstance(this).setEarPhoneModeEnable(earPhoneMode);
    }
    /**
     * 监听长按事件
     */
    private MsgAdapter.ViewHolderEventListener viewHolderEventListener = new MsgAdapter.ViewHolderEventListener() {
        @Override
        public boolean onViewHolderLongClick(View clickView, View viewHolderView, MessageBean item) {
            MenuUtil.getInstance().showLongMenu(ChatActivity.this,item,clickView);
            return true;
        }

        @Override
        public void onFailedBtnClick(MessageBean resendMessage) {
            MsgTypeEnum msgType = resendMessage.getMsgType();
            boolean isFile=msgType==MsgTypeEnum.image||msgType==MsgTypeEnum.audio||msgType==MsgTypeEnum.video;
            ChatUtil.getInstance().resendMessage(resendMessage,true,isFile ?ChatActivity.this:null);
        }
    };

    /**
     * 监听文本输入框
     */
    private class TextListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            /** 有输入内容的时候显示发送按钮否则隐藏 */
            sendBtn.setVisibility(TextUtils.isEmpty(s) ? View.GONE
                    : View.VISIBLE);
            btnMore.setVisibility(TextUtils.isEmpty(s) ? View.VISIBLE
                    : View.GONE);
            checkInputNum();
            clear_chat.setVisibility(s.toString().length() > 0 ? View.VISIBLE
                    : View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public class FixPictureTasks extends
            AsyncTask<String, String, ArrayList<String>> {
        private ArrayList<String> photoPaths = null;

        public FixPictureTasks(ArrayList<String> photoPaths) {
            this.photoPaths = photoPaths;
        }

        @Override
        protected ArrayList<String> doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            if (photoPaths != null) {
                for (int i = 0; i < photoPaths.size(); i++) {
                    String photoPath = photoPaths.get(i);
                    System.out.println("photo_path:" + photoPath);
                    ImageUtils.fixPicture(photoPath);
                }
            }
            return photoPaths;
        }

        @Override
        protected void onPostExecute(ArrayList<String> photoPaths) {
            if (photoPaths != null) {
                for (int i = 0; i < photoPaths.size(); i++) {
                    String photoPath = photoPaths.get(i);
                    sendPicture(toID, photoPath, "");
                }
            }
        }
    }

    class RunnableSelection implements Runnable {
        private int position = 0;
        private PullableListView msgListView;

        public void setPosition(PullableListView msgListView, int position) {
            this.msgListView = msgListView;
            this.position = position;
        }

        @Override
        public void run() {
            ListViewUtil.scrollToPosition(msgListView,position);
            adapter.notifyDataSetChanged();
        }
    }
}
