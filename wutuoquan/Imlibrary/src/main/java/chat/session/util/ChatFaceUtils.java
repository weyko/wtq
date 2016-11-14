package chat.session.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.imlibrary.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chat.base.IMClient;
import chat.image.ChatImageLoader;
import chat.session.activity.ChatActivity;
import chat.session.emoji.EmojiManager;
import chat.session.emoji.EmoticonView;
import chat.session.emoji.IEmoticonCategoryChanged;
import chat.session.emoji.IEmoticonSelectedListener;
import chat.session.emoji.StickerCategory;
import chat.session.emoji.StickerItem;
import chat.session.emoji.StickerManager;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import web.CommonWebActivity;

/**
 * 向文本域中插入表情 发表本类涉及到View的Id，均已设为固定值
 *
 * @author xk 2013-3-18
 */
public class ChatFaceUtils implements IEmoticonSelectedListener, IEmoticonCategoryChanged {
    private static final Factory spannableFactory = Factory
            .getInstance();
    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();
    // private BaseApplication mApp;
    private ChatActivity mContext;
    private EditText mEditField;
    private ViewPager mViewPager;
    /**
     * 按钮：切换默认表情和魔牙表情
     */

    private RadioGroup face_button;
    private LinearLayout chat_face_point_loy;
    private EmoticonView emojiView;

    public ChatFaceUtils() {
    }

    public ChatFaceUtils(final ChatActivity mContext, EditText mEditField,
                         ViewPager mViewPager) {
        this.mContext = mContext;
        this.mEditField = mEditField;
        this.mViewPager = mViewPager;
        face_button = (RadioGroup) mContext.findViewById(R.id.face_btn_layout);
        chat_face_point_loy = (LinearLayout) mContext
                .findViewById(R.id.chat_face_point_loy);
        face_button.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int pageIndex = 0;
                if (checkedId == R.id.default_face) {
                    pageIndex = 0;
                } else if (checkedId == R.id.moya_face_gif) {
                    pageIndex = 1;
                } else if (checkedId == R.id.stickerb_gif) {
                    pageIndex = 2;
                } else if (checkedId == R.id.face_other) {
                    pageIndex = 3;
                }
                showEmotPager(pageIndex);
            }
        });
        initViewPager();
    }

    public ChatFaceUtils(final Context context, View view, EditText mEditField,
                         ViewPager mViewPager) {
        this.mEditField = mEditField;
        this.mViewPager = mViewPager;
        chat_face_point_loy = (LinearLayout) view
                .findViewById(R.id.chat_face_point_loy);
        initViewPager();
    }

    /**
     * 根据密度获取设置后的drawable
     *
     * @param resId
     * @param density 缩放比例
     * @return
     */
    private static Drawable getDrawableByDensity(Context context, int resId,
                                                 float density) {
        Drawable d = context.getResources().getDrawable(resId);
        try {
            d.setBounds(0, 0, (int) (d.getIntrinsicWidth() * density),
                    (int) (d.getIntrinsicHeight()* density));// 设置表情图片的显示大小
        } catch (Exception e) {
            e.printStackTrace();
        }

        return d;
    }

    /**
     * 根据资源名称获取资源
     *
     * @param name
     * @return
     */
    public static int getImageResource(Context context, String name) {
        int img = context.getResources().getIdentifier(name, "drawable",
                context.getPackageName());
        return img;
    }

    /**
     * 初始化ViewPager，默认表情 2013-3-18
     */
    public void initViewPager() {
        if (mViewPager == null) {
            mViewPager = (ViewPager) mContext.findViewById(R.id.view_pager);
        }
    }

    /**
     * 只显示表情
     */
    public void showEmojiPage() {
        if (emojiView == null) {
            emojiView = new EmoticonView(mContext, this, mViewPager, chat_face_point_loy);
            emojiView.setCategoryChangCheckedCallback(this);
        }
        emojiView.showEmojis();
    }

    /**
     * 显示表情和动画
     *
     * @param index
     */
    public void showEmotPager(int index) {
        if (emojiView == null) {
            emojiView = new EmoticonView(mContext, this, mViewPager, chat_face_point_loy);
            emojiView.setCategoryChangCheckedCallback(this);
        }
        emojiView.showStickers(index);
    }

    private void cleanCache() {
        EmojiManager.getInstance().clearCache();
    }

    /**
     * 清除Cache内的全部内容
     */
    public void clearCache() {
        cleanCache();
        System.gc();
    }

    /**
     * @return void
     * @Title: deleteFace
     * @param:
     * @Description: 删除表情
     */
    public void deleteFace(EditText editText) {
        if (editText == null)
            return;
        Editable text = editText.getText();
        int cursorIndex = editText.getSelectionStart();
        if (cursorIndex == 0)
            return;
        int delLen = 1;// 需要删除光标之前字符的长度
        String faceStr = text.toString().substring(0, cursorIndex);// 截取光标之前的字符串，处理光标后面也有‘[’情况
        char charAt = faceStr.charAt(faceStr.length() - 1);
        if (String.valueOf(charAt).equals("]")) {
            int faceStart = faceStr.lastIndexOf("[");
            if (faceStart > -1) {
                String face = faceStr.substring(faceStart);// 截取‘[]’之前的文本内容
                Matcher matcher = EmojiManager.getInstance().getPattern().matcher(face);
                while (matcher.find()) {
                    delLen = face.length();// 设置光标之前长度为表情长度
                    break;
                }
            }
        }
        text.delete(cursorIndex - delLen, cursorIndex);// 删除光标之前长度为delLen的字符
    }

    /**
     * 插入图片到文本域EditText中
     *
     * @param resId
     * @param text
     * @param density 缩放比例
     */
    private void insertDrawableToText(final int resId, String text,
                                      float density) {
        // 插入的表情
        SpannableString ss = new SpannableString(text);
        Drawable d = getDrawableByDensity(mContext, resId, density);// 设置表情图片的显示大小
        if (d == null)
            return;
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        ss.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 在光标所在处插入表情
        mEditField.getText().insert(mEditField.getSelectionStart(), ss);
    }

    /**
     * 插入图片到文本域EditText中
     *
     * @param text
     * @param density 缩放比例
     */
    private void insertDrawableToText(String text,
                                      float density) {
        // 插入的表情
        SpannableString ss = new SpannableString(text);
        Drawable d = EmojiManager.getInstance().getDrawable(mContext, text);// 设置表情图片的显示大小
        if (d == null)
            return;
        d.setBounds(0, 0, (int) (d.getIntrinsicWidth() * density),
                (int) (d.getIntrinsicHeight()* density));// 设置表情图片的显示大小
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        ss.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 在光标所在处插入表情
        mEditField.getText().insert(mEditField.getSelectionStart(), ss);
    }

    /**
     * 获取gif动画
     *
     * @param content
     * @return
     */
    public void getGif(final GifImageView gifView, Context context,
                       String content) {
        if (content == null) {
            return;
        }
        int gifSize = (int) context.getResources().getDimension(
                R.dimen.chat_gif_width_min);
        List<StickerCategory> categories = StickerManager.getInstance().getCategories();
        if (content.startsWith("mt_")) {
            gifSize = (int) context.getResources().getDimension(
                    R.dimen.chat_gif_width);
        }
        gifView.setLayoutParams(new RelativeLayout.LayoutParams(gifSize,
                gifSize));
        for (StickerCategory cate : categories) {
            List<StickerItem> stickers = cate.getStickers();
            for (StickerItem sticker : stickers) {
                if (content.equals(sticker.getName())) {
                    GifDrawable gifDrawable = ChatImageLoader.loadGif(context, "sticker/" + sticker.getCategory() + "/" + sticker.getName());
                    if (gifDrawable != null) {
                        gifView.setDrawingCacheEnabled(false);
                        gifView.setImageDrawable(gifDrawable);
                    }
                }
            }
        }
    }
    @Override
    public void onEmojiSelected(String key) {
        if (key.equals("[del]")) {
            deleteFace(mEditField);
            return;
        }
        insertDrawableToText(key, 0.55f);
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName) {
        mContext.sendGif(stickerName);
    }

    @Override
    public void onCategoryChanged(int index) {
        if (face_button == null)
            return;
        int childCount = face_button.getChildCount();
        int tabIndex=0;
        for (int i = 0; i < childCount; i++) {
            if (face_button.getChildAt(i) instanceof RadioButton) {
                RadioButton tab = (RadioButton) face_button.getChildAt(i);
                tab.setBackgroundColor(IMClient.getInstance().getContext().getResources().getColor(tabIndex == index ? R.color.color_shallow_gray : R.color.white));
                tabIndex++;
            }
        }
    }

    public static class ChatClickSpan extends ClickableSpan {
        private String url;

        public ChatClickSpan(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(IMClient.getInstance().getContext(), CommonWebActivity.class);
            intent.putExtra(CommonWebActivity.WEB_URL, url);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CommonWebActivity.WEB_TITLE, "链接结果");
            IMClient.getInstance().getContext().startActivity(intent);
        }
    }
}
