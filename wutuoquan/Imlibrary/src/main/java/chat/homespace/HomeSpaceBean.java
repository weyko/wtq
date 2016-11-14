package chat.homespace;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 */
public class HomeSpaceBean implements Serializable {
    private String uid;
    private String name;
    private String content;
    private String avatar;
    private long time;
    private String videoUrl;
    private List<String> picUrls;
    private List<CommentBean> commentList;
    public static class CommentBean implements Serializable {
        private String uid;
        private String name;
        private String replayId;
        private String replayName;
        private String comment;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getReplayId() {
            return replayId;
        }

        public void setReplayId(String replayId) {
            this.replayId = replayId;
        }

        public String getReplayName() {
            return replayName;
        }

        public void setReplayName(String replayName) {
            this.replayName = replayName;
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public List<String> getPicUrls() {
        return picUrls;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPicUrls(List<String> picUrls) {
        this.picUrls = picUrls;
    }

    public List<CommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentBean> commentList) {
        this.commentList = commentList;
    }
}
