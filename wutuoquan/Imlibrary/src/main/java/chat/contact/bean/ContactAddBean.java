package chat.contact.bean;

import java.io.Serializable;

import chat.volley.WBaseBean;

/**
 * Description:
 * Created  by: weyko on 2016/11/11.
 */

public class ContactAddBean extends WBaseBean implements Serializable {
    private ContactAddData data;
    public ContactAddData getData() {
        return data;
    }
    public void setData(ContactAddData data) {
        this.data = data;
    }

    public class ContactAddData{
    }
}
