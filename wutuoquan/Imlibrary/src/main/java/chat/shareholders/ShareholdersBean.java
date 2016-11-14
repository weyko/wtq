package chat.shareholders;

import java.io.Serializable;

/**
 * Description: 股东会列表实体类
 * Created  by: weyko on 2016/5/26.
 */
public class ShareholdersBean implements Serializable {
    private long id;
    private String name;
    private String avatar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
