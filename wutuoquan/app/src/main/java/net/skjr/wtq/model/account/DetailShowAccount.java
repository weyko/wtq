package net.skjr.wtq.model.account;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/20 16:01
 * 描述	      证件展示详情
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DetailShowAccount {


    /**
     * projects : <p>LLLLLLLLLLLLLLLLLLLLL</p>
     * forecast : 来来来来来来来来
     * stockNO : 10251112397925
     * financingplan : <p><span style="white-space: normal;">LLLLLLLLLLLLLLLLLLLLL</span></p>
     * aptitude : <p><span style="white-space: normal;">LLLLLLLLLLLLLLLLLLLLL</span></p>
     * roadshow : <p><span style="white-space: normal;">LLLLLLLLLLLLLLLLLLLLL</span></p>
     * controlteam : 来来来来来来来来
     * assessment : 来来来来来来来来
     */

    public PaperworkInfoEntity paperworkInfo;

    public static class PaperworkInfoEntity {
        public String projects;
        public String forecast;
        public String stockNO;
        public String financingplan;
        public String aptitude;
        public String roadshow;
        public String controlteam;
        public String assessment;
    }
}
