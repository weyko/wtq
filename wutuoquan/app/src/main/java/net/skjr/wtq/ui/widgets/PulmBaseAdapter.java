package net.skjr.wtq.ui.widgets;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的Adapter.
 */
public abstract class PulmBaseAdapter<T> extends BaseAdapter {
    protected List<T> mList;

    public PulmBaseAdapter() {
        this.mList = new ArrayList<>();
    }

    public PulmBaseAdapter(List<T> items) {
        this.mList = items;
    }

    public void addMoreItems(List<T> newItems, boolean isFirstLoad) {
        if (isFirstLoad) {
            this.mList.clear();
        }
        this.mList.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addMoreItems(int location, List<T> newItems) {
        this.mList.addAll(location, newItems);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        this.mList.clear();
        notifyDataSetChanged();
    }
}
