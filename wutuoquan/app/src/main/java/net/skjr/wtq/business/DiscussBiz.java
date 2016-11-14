package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.DiscussListAccount;
import net.skjr.wtq.model.requestobj.DiscussListObj;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/20 16:34
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DiscussBiz extends BaseBiz {

    /**
     * 获取评论列表
     */
    public Observable<APIResult<DiscussListAccount>> getDiscussList(int id) {

        //后台对应的方法code
        String requestCode = Url.GETCOMMENTLIST;
        DiscussListObj dict = new DiscussListObj();
        dict.topic_id = id;
        JsonRequest request = new JsonRequest(requestCode, dict);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), DiscussListAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 发表评论
     */
    public Observable<APIResult<Object>> publishDiscuss(int id, String content) {

        //后台对应的方法code
        String requestCode = Url.GETCOMMENTLIST;
        DiscussListObj dict = new DiscussListObj();
        dict.topic_id = id;
        JsonRequest request = new JsonRequest(requestCode, dict);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
