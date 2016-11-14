package chat.session.adapter.common;

import android.view.View;

/**
 * Description: 视图回收监听器
 * Created  by: weyko on 2016/4/5.
 */
public interface ImViewReclaimer {
    /**
     * 回收视图
     * @param view
     */
    public void reclaimView(View view);
}
