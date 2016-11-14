package chat.session.group.bean;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import chat.session.bean.ImUserBean;

/**
 * @ClassName: RemoteMembesBean
 * @Description: 用于编辑的成员实体类
 */
public class RemoteContactsBean implements Serializable {
	private static final long serialVersionUID = 1111222333L;
	/** 传递成员的Key */
	public static final String MEMBES = "membes";
	/** 添加按钮的标签 */
	public static final String ADD_TAG = "add";
	/** 选择的成员 */
	private List<ImUserBean> membsers;

	@SuppressLint("UseSparseArrays")
	public RemoteContactsBean() {
		membsers = new ArrayList<ImUserBean>();
	}

	public List<ImUserBean> getMembsers() {
		return membsers;
	}

	public void setMembsers(List<ImUserBean> membsers) {
		if (this.membsers == null)
			return;
		this.membsers.clear();
		this.membsers.addAll(membsers);
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
	 * @Title: removeAddIcon
	 * @param:
	 * @Description:去掉"+"
	 * @return void
	 */
	public void removeAddIcon() {
		if (membsers != null && membsers.size() > 0) {
			String remark = membsers.get(0).getRemark();
			if (remark != null && remark.equals(ADD_TAG))
				membsers.remove(0);
		}
	}

	/**
	 * @Title: remove
	 * @param:
	 * @Description: 遍历删除
	 * @return void
	 */
	public void remove(ImUserBean item) {
		if (membsers != null) {
			Iterator<ImUserBean> iterator = membsers.iterator();
			while (iterator.hasNext()) {
				ImUserBean bean = iterator.next();
				if (bean.getMxId().equals(item.getMxId()))
					iterator.remove();
			}
		}
	}

	public void add(ImUserBean item) {
		if (membsers != null) {
			membsers.add(item);
		}
	}

	/**
	 * @Title: initSelects
	 * @param:
	 * @Description:初始化选择项
	 * @return void
	 */
	public void initSelects(HashMap<String, String> selects) {
		if (membsers == null)
			return;
		selects.clear();
		for (ImUserBean bean : membsers) {
			selects.put(bean.getMxId(), bean.getMxId());
		}
	}
}
