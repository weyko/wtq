package chat.contact.bean;

import java.io.Serializable;

import chat.volley.WBaseBean;

/**
 * Description:
 * Created  by: weyko on 2016/11/11.
 */

public class ContactInfoBean extends WBaseBean implements Serializable{
    private ContactBean data;
    public ContactBean getData() {
        return data;
    }
    public void setData(ContactBean data) {
        this.data = data;
    }
}
