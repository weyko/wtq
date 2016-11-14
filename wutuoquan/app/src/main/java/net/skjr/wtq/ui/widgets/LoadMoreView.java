package net.skjr.wtq.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import net.skjr.wtq.R;


/**
 * 加载更多的View布局,可自定义.
 */
public class LoadMoreView extends LinearLayout {

    public View mFooter1;
    public View mFooter2;

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.footer_view, this);
        mFooter1 = findViewById(R.id.footer1);
        mFooter2 = findViewById(R.id.footer2);
    }
}
