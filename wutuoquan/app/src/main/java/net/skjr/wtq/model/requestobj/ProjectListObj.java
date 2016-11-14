package net.skjr.wtq.model.requestobj;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/9 9:17
 * 描述	      获取项目列表
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ProjectListObj {
    public String industryType ;
    public String orderType;
    public String stockStatus;
    public int pageIndex;
    public int pageSize;
    public String cityName;
    public String stockTitle;


    public ProjectListObj(String industryType, String orderType, String stockStatus, int pageIndex, int pageSize) {
        this.industryType = industryType;
        this.orderType = orderType;
        this.stockStatus = stockStatus;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }
}
