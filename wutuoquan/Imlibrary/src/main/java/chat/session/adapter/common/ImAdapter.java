package chat.session.adapter.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chat.common.util.output.LogUtil;

/**
 * Description:
 * Created  by: weyko on 2016/4/5.
 */
public class ImAdapter<T> extends BaseAdapter implements ImViewReclaimer{
    protected final Context context;
    /**
     * view list
     */
    private final List<T> items;
    public final ImAdapterDelegate delegate;
    private final LayoutInflater inflater;
    /**
     * view types of list
     */
    private final Map<Class<?>, Integer> viewTypes;
    private Object tag;
    /**
     * is mutable
     */
    private boolean mutable;
    /**
     * listeners of list
     */
    private Set<ImScrollStateListener> listeners;

    public ImAdapter(Context context, List<T> items, ImAdapterDelegate delegate) {
        this.context = context;
        this.items = items;
        this.delegate = delegate;
        this.inflater = LayoutInflater.from(context);
        this.viewTypes = new HashMap<Class<?>, Integer>(getViewTypeCount());
        this.listeners = new HashSet<ImScrollStateListener>();
    }
    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public T getItem(int position) {
        return position < getCount() ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public boolean isEnabled(int position) {
        return delegate.enabled(position);
    }

    public List<T> getItems() {
        return items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }
    @Override
    public int getViewTypeCount() {
        return delegate.getViewTypeCount();
    }
    @Override
    public int getItemViewType(int position) {
        if (getViewTypeCount() == 1) {//unknow
            return 0;
        }

        Class<?> clazz = delegate.viewHolderAtPosition(position);
        if (viewTypes.containsKey(clazz)) {
            return viewTypes.get(clazz);
        } else {
            int type = viewTypes.size();
            if (type < getViewTypeCount()) {
                viewTypes.put(clazz, type);
                return type;
            }
            return 0;
        }
    }
    public boolean isMutable() {
        return mutable;
    }
    public View viewAtPosition(int position) {
        ImViewHolder holder = null;
        View view = null;
        try {
            Class<?> viewHolder = delegate.viewHolderAtPosition(position);
            holder = (ImViewHolder) viewHolder.newInstance();
            holder.setAdapter(this);
        } catch (Exception e) {
           LogUtil.d(e.getMessage());
        }
        view = holder.getView(inflater);
        view.setTag(holder);
        holder.setContext(view.getContext());
        return view;
    }
    public View getView(final int position, View convertView, ViewGroup parent, boolean needRefresh) {
        if (convertView == null) {
            convertView = viewAtPosition(position);
        }

        ImViewHolder holder = (ImViewHolder) convertView.getTag();
        holder.setPosition(position);
        if (needRefresh) {
            try {
                holder.refresh(position,getItem(position));
            } catch (RuntimeException e) {
                LogUtil.d(e.getMessage());
            }
        }

        if (holder instanceof ImScrollStateListener) {
            listeners.add(holder);
        }

        return convertView;
    }
    @Override
    public void reclaimView(View view) {

    }
}
