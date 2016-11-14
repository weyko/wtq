package net.skjr.wtq.common;

import android.text.TextUtils;

import net.skjr.wtq.application.MyPreference;

/**
 * Url
 */
public class Url {

    /**
     * 盛开金融网址
     */
    public static final String COMPANY_URL = "http://www.skjr.net/";

    /**
     * 基地址
     */
//    public static final String BASE_URL = "http://wtq.daliuliang.com.cn";
//    public static final String BASE_URL = "http://192.168.1.163:8880";
    public static final String BASE_URL = "http://ddysd.tunnel.qydev.com";

    /**
     * 后台接口地址
     */
    public static final String FIRST_URL = BASE_URL+"/mobile/api/appService";

    /**
     * 充值或支付反馈url
     */
    public static final String PAYBACK = "http://www.code1.com/";
    public static final String RECHARGEBACK = "http://www.code0.com/";




    /**
     * 请求数据格式
     */
//            {
//                "code": "listBank",
//                    "data": {
//
//            }
//            }


    /**
     * 图形验证码
     */
    public static final String IMGVARIFY = BASE_URL+"/mobile/code";
    /**
     * 上传头像
     */
    public static final String UPLOADIMG = BASE_URL+"/mobile/api/upload/userimg";
//    public static final String UPLOADIMG = "http://ddysd.tunnel.qydev.com/mobile/api/uploadChat/0/27889";
    /**
     * 上传项目封面
     */
    public static final String UPLOADSTOCKIMG = BASE_URL+"/mobile/api/upload/stock";
    /**
     * 支付
     */
    public static final String GOTOPAY = BASE_URL+"/mobile/api/appService";

    /**
     * H5-关于我们
     */
    public static final String ABOUTUS = BASE_URL+"/mobile/about/contact";
    /**
     * H5-帮助中心
     */
    public static final String HELPCENTER = BASE_URL+"/mobile/about/help";
    /**
     * H5-消息列表
     */
    public static final String MESSAGE = BASE_URL+"/mobile/about/news";
    /**
     * H5-股权协议
     */
    public static final String PROTOCOL = BASE_URL+"/mobile/about/about_protocol";
    /**
     * H5-股权协议
     */
    public static final String CONFIRM_ORDER = BASE_URL+"/mobile/about/confirm_order";
    /**
     * 站内信
     */
    public static final String GETSITELIST = BASE_URL+"/mobile/information";
    /**
     * 注册服务协议
     */
    public static final String REGISTERPROTOCOL = BASE_URL+"/mobile/agreement/register";
    /**
     * 认购服务协议
     */
    public static final String SUBSCRIBEPROTOCOL = BASE_URL+"/mobile/agreement/subscribe";
    /**
     * 风险说明
     */
    public static final String RISKTHAT = BASE_URL+"/mobile/agreement/riskThat";
    /**
     * 商家入驻服务协议
     */
    public static final String SHOPENTER = BASE_URL+"/mobile/agreement/shopEnter";
    /**
     * 项目详情
     */
    public static final String DETAIL = BASE_URL+"/mobile/stock/detail";
    /**
     * 项目详情-项目介绍等
     */
    public static final String PAPERWORK = BASE_URL+"/mobile/stock/paperwork";
    /**
     * 新人学堂
     */
    public static final String NEWSCHOOL = BASE_URL+"/mobile/about/newSchool";
    /**
     * 财富榜
     */
    public static final String ACCOUNTRANKING = BASE_URL+"/mobile/stock/accountRanking";

    /*-------------------店铺----------------------*/
    /**
     * 酒店主页
     */
    public static final String HOTEL = BASE_URL+"/mobile/shop/shopcenterHotel";
    /**
     * 商品详情
     */
    public static final String SHOPDETAIL = BASE_URL+"/mobile/shop/detail";
    /**
     * 购物车
     */
    public static final String CARTLIST = BASE_URL+"/mobile/shop/cartList";
    /**
     * 购物车-删除
     */
    public static final String CARTDELLIST = BASE_URL+"/mobile/shop/cartDelList";
    /**
     * 购物车-空
     */
    public static final String CARTNULL = BASE_URL+"/mobile/shop/cartNull";
    /**
     * 店铺相册
     */
    public static final String SHOPPHOTO = BASE_URL+"/mobile/shop/photo";
    /**
     * 分类
     */
    public static final String CLASSIFY = BASE_URL+"/mobile/shop/classify";
    /**
     * 会员卡详情
     */
    public static final String MEMBERSCARDDETAIL = BASE_URL+"/mobile/ticket/membersCardDetail";
    /**
     * 好友领取优惠券
     */
    public static final String GETTICKET = BASE_URL+"/mobile/ticket/getTicket";
    /**
     * 好友领取成功
     */
    public static final String GETTICKETSUC = BASE_URL+"/mobile/ticket/getTicketSuc";

    /**
     * 获取基地址
     *
     * @return
     */
    public static String getBaseUrl() {
        String url = MyPreference.getBaseUrl();

        if (TextUtils.isEmpty(url))
            url = FIRST_URL;

        if (url.endsWith("/"))
            url = url.substring(0, url.length() - 1);

        return url;
    }

    /**
     * 基地址-后台服务数据接口地址
     */
    public static final String BASE_SERVICE_URL = "/Api";

    /**
     * 基地址-bridge，用于app拦截url
     */
    public static final String BASE_BRIDGE_URL = "bridge://appLocal";

    //------------------------------------数据接口code-------------------------------------------------


    /**
     * 6.首页
     */
    public static final String TAB_HOME = "app-getStartPageData";

    /**
     * 个人中心
     */
    public static final String TAB_MINE = "zhaccount-getAccountIndex";
    /**
     * 获取行业列表
     */
    public static final String DICTLIST = "cyproduct-getDictList";
    /**
     * 获取项目列表
     */
    public static final String STOCKLIST = "cyproduct-getStockList";
    /**
     * 发布项目
     */
    public static final String PUBLISHPROJECT = "cyproduct-addStock";
    /**
     * 注册
     */
    public static final String REGISTER = "app-register";
    /**
     * 发送短信验证码
     */
    public static final String PHONECODE = "app-registerSendCode";
    /**
     * 校验短信验证码
     */
    public static final String VERIFYPHONECODE = "app-checkVerifyPhoneCode";
    /**
     * 登录
     */
    public static final String LOGIN = "app-login";
    /**
     * 忘记密码-获取验证码
     */
    public static final String FINDPWDGETCODE = "app-forgotLoginSendCode";
    /**
     * 忘记密码-校验验证码
     */
    public static final String FINDPWDCHECKCODE = "app-forgotLoginVerify";
    /**
     * 忘记密码-重置密码
     */
    public static final String RESETPWD = "app-resetPwd";
    /**
     * 项目详情
     */
    public static final String PROJECTDETAIL = "cyproduct-getStockInfo";
    /**
     * 我的资产
     */
    public static final String MYASSETS = "zhaccount-getMyAccount";
    /**
     * 认购订单信息
     */
    public static final String ORDERINFO = "cyproduct-getStockSureOrder";
    /**
     * 获取省市县
     */
    public static final String CITYLIST = "app-list";

    /**
     * 修改登录密码
     */
    public static final String LOGINPWD = "app-updateLoginPwd";
    /**
     * 修改交易密码
     */
    public static final String RESETTRADEPWD = "zhaccount-updatePayPassWord";
    /**
     * 设置交易密码
     */
    public static final String SETTRADEPWD = "zhaccount-settingPayPassWord";
    /**
     * 申请入驻
     */
    public static final String SAVEAPPLY = "about-saveApply";
    /**
     * 我收藏的
     */
    public static final String MYCOLLECT = "cyproduct-getStockFavorite";
    /**
     * 我投资的
     */
    public static final String MYINVEST = "cyproduct-getTenderList";
    /**
     * 我发起的
     */
    public static final String MYPUBLISH = "cyproduct-getCreateStockList";
    /**
     * 个人资料
     */
    public static final String USERINFO = "app-myUserDetails";
    /**
     * 银行卡列表
     */
    public static final String BANKCARDLIST = "zhaccount-getAccountBankList";
    /**
     * 设置为提现卡
     */
    public static final String SETBANK = "zhaccount-setBank";
    /**
     * 删除提现卡
     */
    public static final String DELBANK = "zhaccount-delBank";
    /**
     * 绑定银行卡
     */
    public static final String BINDBANK = "zhaccount-bindBank";

    /**
     * 添加银行卡短信验证码
     */
    public static final String ADDCARDSMSCODE = "zhaccount-getBindPhoneCode";
    /**
     * 忘记交易密码-发送验证码
     */
    public static final String FORGETTRADESMSCODE = "zhaccount-forgetPaySendCode";
    /**
     * 忘记交易密码-校验验证码
     */
    public static final String FORGETTRADECONFIRM = "zhaccount-confirmPaySendCode";
    /**
     * 忘记交易密码-保存密码
     */
    public static final String FORGETTRADESUBMIT = "zhaccount-forgetPayPassWord";

    /**
     * 提现信息
     */
    public static final String GETCASHINFO = "zhaccount-getCashInfo";
    /**
     * 提现
     */
    public static final String GETCASH = "zhaccount-withdrawOrderAdd";
    /**
     * 管理牛人
     */
    public static final String GETPOWERFULLIST = "about-getPowerfulList";
    /**
     * 入驻品牌
     */
    public static final String GETBRANDLIST = "about-getBrandList";
    /**
     * 安全退出
     */
    public static final String LOGINOUT = "app-loginOut";
    /**
     * 证件展示详情
     */
    public static final String DETAILSHOW = "cyproduct-getStockPaperworkInfo";
    /**
     * 评论列表
     */
    public static final String GETCOMMENTLIST = "cyproduct-getCommentList";
    /**
     * 发表评论
     */
    public static final String COMMENT = "cyproduct-comment";
    /**
     * 回复评论
     */
    public static final String REPLY = "cyproduct-reply";
    /**
     * 查询对应类型是否认证
     */
    public static final String CHECKUSER = "app-checkUser";
    /**
     * 认证投资人申请
     */
    public static final String INVESTORAUTH = "cyproduct-investorAuth";
    /**
     * 收藏项目
     */
    public static final String SUBCOLLECTION = "cyproduct-subCollection";
    /**
     * 取消收藏
     */
    public static final String CANCELCOLLECTION = "cyproduct-cancelCollection";
    /**
     * 实名认证页面信息
     */
    public static final String REALNAMEINFO = "app-realNameInfo";
    /**
     * 实名认证发送验证码
     */
    public static final String SENDCODEREALNAME = "app-sendCodeRealName";
    /**
     * 实名认证提交
     */
    public static final String CHECKREALNAME = "app-checkRealName";
    /**
     * 设置页面信息
     */
    public static final String SETINFO = "app-setInfo";
    /**
     * 余额支付
     */
    public static final String INVESTMENT = "cyproduct-investment";
    /**
     * 意见反馈
     */
    public static final String SUBMITFEEDBACK = "about-submitFeedback";
    /**
     * 个人资料修改
     */
    public static final String SAVEMYUSERDETAILS = "app-saveMyUserDetails";
    /**
     * 校验项目是否是当前用户投资
     */
    public static final String CHECKUSERSTOCK = "cyproduct-checkUserStock";
    /**
     * 收货地址列表
     */
    public static final String ADDRESSLIST = "basis-addressList";
    /**
     * 新增/修改地址
     */
    public static final String SAVEADDRESSDETAIL = "basis-saveAddressDetail";
    /**
     * 设置默认地址
     */
    public static final String SETDEFAULTADDRESS = "basis-setDefaultAddress";
    /**
     * 删除地址
     */
    public static final String DELETEUSERADDRESS = "basis-deleteUserAddress";
    /**
     * 校验是否可以添加地址
     */
    public static final String ADDRESSCOUNT = "basis-addressCount";
    /**
     * 我投资的项目详情
     */
    public static final String GETTENDERINFO = "cyproduct-getTenderInfo";
    /**
     * 店铺主页
     */
    public static final String SHOPHOME = "shop-shopHome";
    /**
     * 搜索店铺
     */
    public static final String SEARCHSHOP = "shop-searchShop";


}
