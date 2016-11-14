package chat.session.group.bean;

import java.io.Serializable;

import chat.contact.bean.ContactBean;

/**
 * @ClassName: RemoteMemberBean
 * @Description: 用于编辑的成员实体类
 */
public class RemoteMemberBean implements Serializable {
	private static final long serialVersionUID = 1111222333L;
	/** 传递成员的Key */
	public static final String MEMBES = "membes";
	/** 添加按钮的标签 */
	public static final String ADD_TAG = "add";
	/** 选择的成员 */
	private SparseArrayList<ContactBean> membsers;
	public RemoteMemberBean() {
		membsers = new SparseArrayList<ContactBean>();
	}

	public SparseArrayList<ContactBean> getMembsers() {
		if(membsers==null)
			membsers=new SparseArrayList<ContactBean>();
		return membsers;
	}

	public void setMembsers(SparseArrayList<ContactBean> membsers) {
		if (this.membsers == null){
			if(this.membsers==null)
				this.membsers=new SparseArrayList<ContactBean>();
		}
		this.membsers= membsers;
	}

	/**
	 * @Title: getSize
	 * @param:
	 * @Description: 获取选择的人数
	 * @return int
	 */
	public int getSize() {
		if (membsers != null)
			return membsers.size();
		return 0;
	}
	/**
	 * @Title: remove
	 * @param:
	 * @Description: 遍历删除
	 * @return void
	 */
	public void remove(ContactBean item) {
		if (membsers != null) {
			int size=membsers.size();
			for(int i=0;i<size;i++){
				ContactBean friendBean = membsers.valueAt(i);
				if (friendBean.getFriendID() == item.getFriendID())
					membsers.delete(membsers.keyAt(i));
			}
		}
	}
	public void add(ContactBean item) {
		if (membsers != null) {
			membsers.append(membsers.size(),item);
		}
	}
}
