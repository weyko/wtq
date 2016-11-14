package chat.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.imlibrary.R;
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {
	public ProgressBar progressbar;
	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				5, 0, 0));
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.progress_horizontal);

		progressbar.setProgressDrawable(drawable);
		addView(progressbar);
		setWebChromeClient(new WebChromeClient());

	}

	public class WebChromeClient extends android.webkit.WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(GONE);
			} else {
				if (progressbar.getVisibility() == GONE)
					progressbar.setVisibility(VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	}
}