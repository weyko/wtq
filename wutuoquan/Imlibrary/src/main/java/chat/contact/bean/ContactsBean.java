package chat.contact.bean;

import java.io.Serializable;
import java.util.ArrayList;

import chat.volley.WBaseBean;

/**
 * Description:联系人列表实体类
 * Created  by: weyko on 2016/11/7.
 */

public class ContactsBean extends WBaseBean implements Serializable{
    private ContactData data;
    public ContactData getData() {
        return data;
    }

    public void setData(ContactData data) {
        this.data = data;
    }

    public class ContactData implements Serializable{
      private ArrayList<ContactBean>list;

       public ArrayList<ContactBean> getList() {
           return list;
       }

       public void setList(ArrayList<ContactBean> list) {
           this.list = list;
       }
   }
}
