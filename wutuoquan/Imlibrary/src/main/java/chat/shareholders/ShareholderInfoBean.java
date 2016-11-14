package chat.shareholders;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * Created  by: weyko on 2016/5/26.
 */
public class ShareholderInfoBean implements Serializable {
    private List<GroupBean> groups;
    public List<GroupBean> getGroups() {
        return groups;
    }
    public  void setGroups(List<GroupBean> groups) {
        this.groups = groups;
    }
    public static class GroupBean {
        private int sort;
        private String name;
        private List<ChildBean> childs;
        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ChildBean> getChilds() {
            return childs;
        }

        public void setChilds(List<ChildBean> childs) {
            this.childs = childs;
        }
    }
    public static class ChildBean{
        private String id;
        private String name;
        private String avater;
        private String post;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvater() {
            return avater;
        }

        public void setAvater(String avater) {
            this.avater = avater;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }
    }
}
