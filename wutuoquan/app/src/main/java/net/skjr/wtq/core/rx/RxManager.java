package net.skjr.wtq.core.rx;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Rx订阅管理器
 * 进行Rx订阅管理
 */
public class RxManager {
    private CompositeSubscription compositeSubscription;

    /**
     * 添加Rx订阅
     *
     * @param subscription
     */
    public synchronized void addSubscription(Subscription subscription) {
        if (this.compositeSubscription == null) {
            this.compositeSubscription = new CompositeSubscription();
        }

        if (subscription != null && !subscription.isUnsubscribed()) {
            //L.d("添加Rx订阅");
            compositeSubscription.add(subscription);
        }
    }

    /**
     * 移除compositeSubscription中的Rx订阅
     */
    public void unsubscribe() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            //L.d("移除Rx订阅");
            compositeSubscription.unsubscribe();
        }
    }

    /**
     * 移除指定的订阅
     *
     * @param subscription
     */
    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            //L.d("移除Rx订阅");
            subscription.unsubscribe();
        }
    }
}
