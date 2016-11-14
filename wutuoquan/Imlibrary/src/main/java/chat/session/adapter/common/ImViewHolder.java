package chat.session.adapter.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Description:
 * Created  by: weyko on 2016/4/5.
 */
public abstract class ImViewHolder implements ImScrollStateListener {
    /**
     * context
     */
    protected Context context;

    /**
     * fragment
     */
    protected Fragment fragment;

    /**
     * list item view
     */
    protected View view;

    /**
     * adapter providing data
     */
    protected ImAdapter adapter;

    /**
     * index of item
     */
    protected int position;

    public ImViewHolder() {

    }

    protected void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setContext(Context context) {

        this.context = context;
    }

    public void setAdapter(ImAdapter adapter) {
        this.adapter = adapter;
    }

    protected ImAdapter getAdapter() {
        return this.adapter;
    }

    protected void setPosition(int position) {
        this.position = position;
    }

    public View getView(LayoutInflater inflater) {
        int resId = getResId();
        view = inflater.inflate(resId, null);
        inflate();
        return view;
    }

    public boolean isFirstItem() {
        return position == 0;
    }

    public boolean isLastItem() {
        return position == adapter.getCount() - 1;
    }

    protected abstract int getResId();

    protected abstract void inflate();

    protected abstract void refresh(int position,Object item);

    @Override
    public void reclaim() {
    }

    @Override
    public void onImmutable() {
    }

    protected boolean mutable() {
        return adapter.isMutable();
    }

    public void destory() {

    }

    protected <T extends View> T findView(int resId) {
        return (T) (view.findViewById(resId));
    }
}
