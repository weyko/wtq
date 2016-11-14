package net.skjr.wtq.ui.h5;

/**
 * 需要处理的H5动作，由H5Parser解析出来
 */
public class H5Action {
    public static final String GOTOACCOUNT = "gotoaccount";
    public static final String GOTOHOME = "gotohome";
    public static final String GOTOLOGIN = "gotologin";
    public static final String GOTOREGISTER = "gotoregister";
    public static final String CALLMOBILE = "callmobile";
    public static final String CLOSEPAGE = "closepage";
    public static final String POPPAGE = "poppage";
    public static final String DATA_ID = "dataid";
    public static final String DATA = "data";
    public static final String POPADDCARD = "popaddcard";
    public static final String SHOWPROGRESS = "showprogress";
    public static final String HIDEPROGRESS = "hideprogress";
    public static final String SHOWTOAST = "showtoast";
    public static final String AJAX = "ajax";
    public static final String POPMSG = "popmsg";


    /**
     * 连连支付
     */
    public static final String PAYMENT = "payment";
    public static final String RECHARGE = "recharge";



    public static final String POPPAGE_GENERAL = "popage_general";

    public String phone = "";

    public String dataId;

    /**
     * closeType 1:不关闭当前窗口，新开窗口；2：关闭当前窗口，新开窗口；3：在当前窗口打开
     */
    public int closeType;
    public String msg = "";
    public String title = "";
    public String url = "";

    public String code = "";
    public String data = "";

    public boolean close;

    //用于不同的接口，popMsg:1确认 2确认取消
    public int type;
}




