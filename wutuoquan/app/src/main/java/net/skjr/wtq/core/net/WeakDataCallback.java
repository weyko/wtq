package net.skjr.wtq.core.net;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * 对DataCallback进行一个弱引用的封装
 * 继承类要用static class来声明，不能使用内部匿名类或者非静态类
 * 否则会引起内存泄露
 */
public abstract class WeakDataCallback<A extends Activity, R> implements DataCallback<R> {
    private WeakReference<A> weak;

    public WeakDataCallback(A activity) {
        this.weak = new WeakReference<>(activity);
    }

    /**
     * 获取外部的Activity
     *
     * @return
     */
    protected A getActivity() {
        return weak.get();
    }
}
