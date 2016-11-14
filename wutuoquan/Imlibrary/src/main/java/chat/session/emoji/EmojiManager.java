package chat.session.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chat.base.IMClient;
import chat.session.util.ChatFaceUtils;

public class EmojiManager {

    private static EmojiManager instance;
    private final String EMOT_DIR = "emoji/";
    // max cache size
    private final int CACHE_MAX_SIZE = 1024;
    // text to entry
    private final Map<String, Entry> text2entry = new HashMap<String, Entry>();
    // default entries
    private final List<Entry> defaultEntries = new ArrayList<Entry>();
    private Pattern pattern;
    // asset bitmap cache, key: asset path
    private LruCache<String, Bitmap> drawableCache;

    public EmojiManager() {
        initData();
    }

    public static EmojiManager getInstance() {
        if (instance == null) {
            instance = new EmojiManager();
        }
        return instance;
    }

    private void initData() {
        Context context = IMClient.getInstance().getContext();
        load(context, EMOT_DIR + "emoji.xml");
        pattern = makePattern();
        drawableCache = new LruCache<String, Bitmap>(CACHE_MAX_SIZE) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != newValue)
                    oldValue.recycle();
            }
        };
    }

    public final int getDisplayCount() {
        return defaultEntries.size();
    }

    //
    // display
    //

    public final Drawable getDisplayDrawable(Context context, int index) {
        String text = (index >= 0 && index < defaultEntries.size() ?
                defaultEntries.get(index).text : null);
        return text == null ? null : getDrawable(context, text);
    }

    public final String getDisplayText(int index) {
        return index >= 0 && index < defaultEntries.size() ? defaultEntries
                .get(index).text : null;
    }

    public final Pattern getPattern() {
        return pattern;
    }

    public final Drawable getDrawable(Context context, String text) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        Entry entry = text2entry.get(text);
        if (entry == null) {
            return null;
        }

        Bitmap cache = drawableCache.get(entry.assetPath);
        if (cache == null) {
            cache = loadAssetBitmap(context, entry.assetPath);
        }
        return new BitmapDrawable(context.getResources(), cache);
    }

    private Pattern makePattern() {
        return Pattern.compile(patternOfDefault());
    }

    //
    // internal
    //

    private String patternOfDefault() {
        return "\\[[^\\[]{1,10}\\]";
    }

    private Bitmap loadAssetBitmap(Context context, String assetPath) {
        InputStream is = null;
        try {
            Resources resources = IMClient.getInstance().getContext().getResources();
            Options options = new Options();
            options.inDensity = DisplayMetrics.DENSITY_HIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            if (bitmap != null) {
                drawableCache.put(assetPath, bitmap);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private final void load(Context context, String xmlPath) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        new EntryLoader().load(context, xmlPath);
    }

    /**
     * 获取带链接表情
     * @param context
     * @param textView
     * @param value
     * @param align
     * @param scale
     */
    public void getSmileTextWithUrl(Context context,
                                    TextView textView, String value, int align, float scale) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        SpannableStringBuilder spanBuilder;
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(value);
            CharSequence text = textView.getText();
            Spannable span = (Spannable) text;
            int end = text.length();
            URLSpan[] urls = span.getSpans(0, end, URLSpan.class);
            spanBuilder = new SpannableStringBuilder(text);
            spanBuilder.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                ChatFaceUtils.ChatClickSpan myURLSpan = new ChatFaceUtils.ChatClickSpan(url.getURL());
                spanBuilder.setSpan(myURLSpan, span.getSpanStart(url), span.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            Spannable spanText = replaceEmoticons(context, value, scale, align, spanBuilder);
            viewSetText(textView, spanText);
        }
    }

    /**
     * 获取表情
     * @param context
     * @param textView
     * @param value
     * @param align
     * @param scale
     */
    public void getSmileText(Context context,
                             TextView textView, String value, int align, float scale) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        if(TextUtils.isEmpty(value))
            value="";
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(value);
        if (textView != null) {
            Spannable spanText = replaceEmoticons(context, value, scale, align, spanBuilder);
            viewSetText(textView, spanText);
        }
    }

    /**
     * 获取表情
     * @param context
     * @param textView
     * @param value
     * @param scale
     * @param spanBuilder
     */
    public void getSmileText(Context context,
                             TextView textView, String value, float scale, SpannableStringBuilder spanBuilder) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        if (textView != null) {
            Spannable spanText = replaceEmoticons(context, value, scale, ImageSpan.ALIGN_BOTTOM, spanBuilder);
            viewSetText(textView, spanText);
        }
    }

    /**
     * 具体类型的view设置内容
     *
     * @param textView
     * @param spanText
     */
    private void viewSetText(View textView, Spannable spanText) {
        if (textView instanceof TextView) {
            TextView tv = (TextView) textView;
            tv.setText(spanText);
        } else if (textView instanceof EditText) {
            EditText et = (EditText) textView;
            et.setText(spanText);
        }
    }

    private Spannable replaceEmoticons(Context context, String value, float scale, int align, SpannableStringBuilder spannableBuilder) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        Matcher matcher = EmojiManager.getInstance().getPattern().matcher(value);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String emot = value.substring(start, end);
            Drawable d = getEmotDrawable(context, emot, scale);
            if (d != null) {
                ImageSpan span = new ImageSpan(d, align);
                spannableBuilder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableBuilder;
    }
    private Drawable getEmotDrawable(Context context, String text, float scale) {
        if(context==null)
            context=IMClient.getInstance().getContext();
        Drawable drawable = EmojiManager.getInstance().getDrawable(context, text);
        // scale
        if (drawable != null) {
            int width = (int) (drawable.getIntrinsicWidth() * scale);
            int height = (int) (drawable.getIntrinsicHeight() * scale);
            drawable.setBounds(0, 0, width, height);
        }

        return drawable;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        if (drawableCache != null) {
            drawableCache = null;
        }
        if (defaultEntries != null) {
            defaultEntries.clear();
        }
        if (text2entry != null) {
            text2entry.clear();
        }
        instance=null;
    }
    private class Entry {
        String text;
        String assetPath;

        Entry(String text, String assetPath) {
            this.text = text;
            this.assetPath = assetPath;
        }
    }

    //
    // load emoticons from asset
    //
    private class EntryLoader extends DefaultHandler {
        private String catalog = "";
        void load(Context context, String assetPath) {
            InputStream is = null;
            try {
                is = context.getAssets().open(assetPath);
                Xml.parse(is, Xml.Encoding.UTF_8, this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            if (localName.equals("Catalog")) {
                catalog = attributes.getValue(uri, "Title");
            } else if (localName.equals("Emoticon")) {
                String tag = attributes.getValue(uri, "Tag");
                String fileName = attributes.getValue(uri, "File");
                Entry entry = new Entry(tag, EMOT_DIR + catalog + "/" + fileName);

                text2entry.put(entry.text, entry);
                if (catalog.equals("default")) {
                    defaultEntries.add(entry);
                }
            }
        }
    }
}
