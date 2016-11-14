package chat.session.adapter.common;

/**
 * Description: 适配器委派工具
 * Created  by: weyko on 2016/4/5.
 */
public interface ImAdapterDelegate {
    public int getViewTypeCount();
    public Class<? extends ImViewHolder> viewHolderAtPosition(int position);
    public boolean enabled(int position);
}
