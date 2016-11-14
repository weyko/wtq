package chat.view;

import android.content.Context;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.imlibrary.R;
/**
 * 展开缩放文本控件
 */
public class MoreTextView extends LinearLayout implements OnClickListener {

	/** default text show max lines */
	private int maxShowLines = 3;

	private static final int COLLAPSIBLE_STATE_NONE = 0;
	private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;
	private static final int COLLAPSIBLE_STATE_SPREAD = 2;

	private TextView desc;
	private TextView descOp;

	private String shrinkup;
	private String spread;
	private int mState;
	private boolean flag;

	public MoreTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		shrinkup = context.getString(R.string.topic_three_text);
		spread = context.getString(R.string.topic_all_text);
		View view = inflate(context, R.layout.more_text, this);
		view.setPadding(0, -1, 0, 0);
		desc = (TextView) view.findViewById(R.id.desc_tv);
		descOp = (TextView) view.findViewById(R.id.desc_op_tv);
		descOp.setOnClickListener(this);
	}

	public MoreTextView(Context context) {
		this(context, null);
	}

	public final void setDesc(CharSequence charSequence) {
		desc.setText(charSequence);
		mState = COLLAPSIBLE_STATE_SPREAD;
		flag = false;
		requestLayout();
	}

	@Override
	public void onClick(View v) {
		flag = false;
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!flag) {
			flag = true;
			if (desc.getLineCount() <= maxShowLines) {
				mState = COLLAPSIBLE_STATE_NONE;
				descOp.setVisibility(View.GONE);
				desc.setMaxLines(maxShowLines + 1);
			} else {
				post(new InnerRunnable());
			}
		}
	}

	class InnerRunnable implements Runnable {
		@Override
		public void run() {
			if (mState == COLLAPSIBLE_STATE_SPREAD) {
				desc.setMaxLines(maxShowLines);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(spread);
				mState = COLLAPSIBLE_STATE_SHRINKUP;
			} else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
				desc.setMaxLines(Integer.MAX_VALUE);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(shrinkup);
				mState = COLLAPSIBLE_STATE_SPREAD;
			}
		}
	}

	public void setMovementMethod(MovementMethod instance) {
		if(desc != null){
			desc.setMovementMethod(instance);
		}
	}

	public CharSequence getText() {
		if(desc != null){
			return desc.getText();
		}
		return "";
	}

	public void setMaxShowLines(int line){
		this.maxShowLines = line;
	}
}
