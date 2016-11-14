package net.skjr.wtq.business;

import net.skjr.wtq.common.Url;
import net.skjr.wtq.data.RxData;
import net.skjr.wtq.model.APIResult;
import net.skjr.wtq.model.JsonRequest;
import net.skjr.wtq.model.account.CityListAccount;
import net.skjr.wtq.model.account.DictInfo;
import net.skjr.wtq.model.account.DictListData;
import net.skjr.wtq.model.account.ProjectDetailAccount;
import net.skjr.wtq.model.account.ProjectListAccount;
import net.skjr.wtq.model.requestobj.CityListObj;
import net.skjr.wtq.model.requestobj.DetailShowObj;
import net.skjr.wtq.model.requestobj.ProjectDetailObj;
import net.skjr.wtq.model.requestobj.ProjectListObj;
import net.skjr.wtq.model.system.UploadImgObj;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/8 19:28
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectListBiz extends BaseBiz {

    /**
     * 获取行业列表
     *
     */
    public Observable<APIResult<DictInfo>> getDictList() {

        //后台对应的方法code
        String requestCode = Url.DICTLIST;
        DictListData dict = new DictListData();
        dict.code = "industry";
        JsonRequest request = new JsonRequest(requestCode, dict);
        String token = "";

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), DictInfo.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取项目列表
     */
    public Observable<APIResult<ProjectListAccount>> getProjectList(ProjectListObj obj) {
        String requestCode = Url.STOCKLIST;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = "";
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), ProjectListAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 获取项目详情
     */
    public Observable<APIResult<ProjectDetailAccount>> getProjectDetail(String pid) {
        String requestCode = Url.PROJECTDETAIL;
        ProjectDetailObj obj = new ProjectDetailObj();
        obj.stockNO = pid;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = "";
        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), ProjectDetailAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取所在地列表
     */
    public Observable<APIResult<CityListAccount>> getProvinceList(int type) {

        //后台对应的方法code
        String requestCode = Url.CITYLIST;
        CityListObj obj = new CityListObj();
        obj.class_parent_id = type;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), CityListAccount.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传封面图片
     * @param
     */
    public Observable<APIResult<UploadImgObj>> uploadImg(File[] files) {
        //后台对应的方法code
//        String requestCode = Url.UPLOADIMG;

        JsonRequest request = new JsonRequest();
        String token = getToken();

        return RxData.upload(Url.UPLOADSTOCKIMG, getDataId(), token, getRequestContent(request), files, UploadImgObj.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 上传用户头像
     * @param
     */
    public Observable<APIResult<UploadImgObj>> uploadHeadImg(File[] files) {
        //后台对应的方法code
//        String requestCode = Url.UPLOADIMG;

        JsonRequest request = new JsonRequest();
        String token = getToken();

        return RxData.upload(Url.UPLOADIMG, getDataId(), token, getRequestContent(request), files, UploadImgObj.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 证件展示详情
     */
    public Observable<APIResult<Object>> getDetailShow(String stockNo) {

        //后台对应的方法code
        String requestCode = Url.DETAILSHOW;
        DetailShowObj obj = new DetailShowObj();
        obj.stockNO = stockNo;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 收藏项目
     */
    public Observable<APIResult<Object>> collectProject(String stockNo) {

        //后台对应的方法code
        String requestCode = Url.SUBCOLLECTION;
        DetailShowObj obj = new DetailShowObj();
        obj.code = stockNo;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    /**
     * 取消收藏
     */
    public Observable<APIResult<Object>> cancelCollect(String stockNo) {

        //后台对应的方法code
        String requestCode = Url.CANCELCOLLECTION;
        DetailShowObj obj = new DetailShowObj();
        obj.code = stockNo;
        JsonRequest request = new JsonRequest(requestCode, obj);
        String token = getToken();

        return RxData.excute(requestUrl, getDataId(), token, getRequestContent(request), Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
