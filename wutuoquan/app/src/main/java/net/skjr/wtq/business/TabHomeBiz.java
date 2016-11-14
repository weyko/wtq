package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.BrandsAccount;
import net.skjr.wtq.model.account.GeniusAccount;
import net.skjr.wtq.model.account.MyCollectListAccount;
import net.skjr.wtq.model.account.MyInvestAccount;
import net.skjr.wtq.model.account.MyPublishAccount;
import net.skjr.wtq.model.account.TabMineAccount;
import net.skjr.wtq.model.account.TabhomeAccount;
import net.skjr.wtq.model.requestobj.MyCollectObj;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/8 10:32
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class TabHomeBiz extends BaseBiz {

    /**
     * 获取首页数据
     *
     */
    public Observable<APIResult<TabhomeAccount>> getTabHomeAccount() {

        //后台对应的方法code
        String requestCode = Url.TAB_HOME;
        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), TabhomeAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 获取个人中心页数据
     */
    public Observable<APIResult<TabMineAccount>> getTabMineAccount() {

        //后台对应的方法code
        String requestCode = Url.TAB_MINE;
        JsonRequest request = new JsonRequest(requestCode);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), TabMineAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 我收藏的
     */
    public Observable<APIResult<MyCollectListAccount>> getMyCollectAccount(int index, int num) {

        //后台对应的方法code
        String requestCode = Url.MYCOLLECT;
        MyCollectObj obj = new MyCollectObj();
        obj.pageIndex = index;
        obj.pageSize = num;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), MyCollectListAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 我投资的
     */
    public Observable<APIResult<MyInvestAccount>> getMyInvestAccount(int index, int num) {

        //后台对应的方法code
        String requestCode = Url.MYINVEST;
        MyCollectObj obj = new MyCollectObj();
        obj.pageIndex = index;
        obj.pageSize = num;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), MyInvestAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 我发起的
     */
    public Observable<APIResult<MyPublishAccount>> getMyPublishAccount(int index, int num) {

        //后台对应的方法code
        String requestCode = Url.MYPUBLISH;
        MyCollectObj obj = new MyCollectObj();
        obj.pageIndex = index;
        obj.pageSize = num;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), MyPublishAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 管理牛人
     */
    public Observable<APIResult<GeniusAccount>> getGeniusList(int index, int num) {

        //后台对应的方法code
        String requestCode = Url.GETPOWERFULLIST;
        MyCollectObj obj = new MyCollectObj();
        obj.pageIndex = index;
        obj.pageSize = num;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), GeniusAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 入驻品牌
     */
    public Observable<APIResult<BrandsAccount>> getBrandList(int index, int num) {

        //后台对应的方法code
        String requestCode = Url.GETBRANDLIST;
        MyCollectObj obj = new MyCollectObj();
        obj.pageIndex = index;
        obj.pageSize = num;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), BrandsAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
