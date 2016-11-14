package net.skjr.wtq.viewmodel;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/13 14:37
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SaveApplyViewModel implements Serializable {
    private String name;
    private String insdutry;
    private String desc;
    private String area;
    private String directCount;
    private String leagueCount;

    private String user;
    private String phone;

    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsdutry() {
        return insdutry;
    }

    public void setInsdutry(String insdutry) {
        this.insdutry = insdutry;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDirectCount() {
        return directCount;
    }

    public void setDirectCount(String directCount) {
        this.directCount = directCount;
    }

    public String getLeagueCount() {
        return leagueCount;
    }

    public void setLeagueCount(String leagueCount) {
        this.leagueCount = leagueCount;
    }

    public CheckResult check() {
        if(TextUtils.isEmpty(name)) {
            return CheckResult.failure("请输入品牌名称");
        }
        if(insdutry.equals("请选择")) {
            return CheckResult.failure("请选择行业类型");
        }
        if(TextUtils.isEmpty(desc)) {
            return CheckResult.failure("请输入店铺介绍");
        }
        if(area.equals("请选择")) {
            return CheckResult.failure("请选择所在地");
        }
        if(TextUtils.isEmpty(directCount)) {
            return CheckResult.failure("请输入直营店数量");
        }
        if(TextUtils.isEmpty(leagueCount)) {
            return CheckResult.failure("请输入加盟店数量");
        }
        return CheckResult.success();
    }
}
