package net.skjr.wtq.model.account;

import java.util.List;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/20 17:09
 * 描述	      评论列表数据
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DiscussListAccount {


    /**
     * list : [{"userImg":"/common/download?token=user|27889|headImg.jpg?t=1477991717","userNickname":"波波","replyList":[{"userNickname":"波波","addDatetime":"2016-11-04","content":"哈哈哈哈哈哈，你好帅"}],"id":5,"addDatetime":"2016-11-04","content":"还刺激了是是是","from_uid":27889},{"userImg":"/common/download?token=user|27889|headImg.jpg?t=1477991717","userNickname":"波波","replyList":[],"id":4,"addDatetime":"2016-11-04","content":"水电费的谁","from_uid":27889},{"userImg":"/common/download?token=user|27889|headImg.jpg?t=1477991717","userNickname":"波波","replyList":[],"id":3,"addDatetime":"2016-11-04","content":"水电费水电费","from_uid":27889}]
     */
    public List<ListEntity> list;

    public static class ListEntity {
        /**
         * userImg : /common/download?token=user|27889|headImg.jpg?t=1477991717
         * userNickname : 波波
         * replyList : [{"userNickname":"波波","addDatetime":"2016-11-04","content":"哈哈哈哈哈哈，你好帅"}]
         * id : 5
         * addDatetime : 2016-11-04
         * content : 还刺激了是是是
         * from_uid : 27889
         */
        public String userImg;
        public String userNickname;
        public List<ReplyListEntity> replyList;
        public int id;
        public String addDatetime;
        public String content;
        public int from_uid;

        public static class ReplyListEntity {
            /**
             * userNickname : 波波
             * addDatetime : 2016-11-04
             * content : 哈哈哈哈哈哈，你好帅
             */
            public String userNickname;
            public String addDatetime;
            public String content;
        }
    }
}
