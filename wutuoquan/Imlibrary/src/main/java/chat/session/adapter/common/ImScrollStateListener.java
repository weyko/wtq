package chat.session.adapter.common;

/**
 * Description: 用于控制碎片
 * Created  by: weyko on 2016/4/5.
 */
public interface ImScrollStateListener {
    /**
     * 碎片回收
     */
    public void reclaim();
    /**
     * 空闲状态
     */
    public void onImmutable();
}
