package chat.session.group.bean;

import java.util.Locale;

import chat.common.util.TextUtils;
import chat.common.util.string.PingYinUtil;
import chat.volley.WBaseBean;

public class GroupBean extends WBaseBean {

	private static final long serialVersionUID = 5454771748926567371L;
	private int id;
	private String name;
	private int count;
	private boolean selected;
	private boolean exist;
	private String pinYin;

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getPinYin() {
		if (TextUtils.getString(name).length() > 0)
			pinYin = PingYinUtil.converterToFirstSpell(name.substring(0, 1))
					.substring(0, 1).toUpperCase(Locale.CHINA);
		else {
			if (TextUtils.getString(getName()).length() > 0)
				pinYin = PingYinUtil
						.converterToFirstSpell(getName().substring(0, 1))
						.substring(0, 1).toUpperCase(Locale.CHINA);
			else
				pinYin = "#";
		}
		return pinYin;
	}
}
