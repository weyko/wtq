package chat.contact.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chat.common.util.time.DateUtils;
import chat.volley.WBaseBean;

public class UserBean extends WBaseBean {

	private static final long serialVersionUID = 6755386096460806568L;

	private User data = new User();

	public User getData() {
		return data;
	}

	public void setData(User data) {
		this.data = data;
	}

	public static class User implements Serializable {

		private static final long serialVersionUID = 5231184714509338326L;
		private String nickName;
		/**女=2、男=1.无=0*/
		private int sex ;
		private String countryCode;
		private long birthday;
		private int birthdayViableType;
		private String lastDailyMood;
		private String homeTown;
		private String favoritePlace;
		private String school;
		private String major;
		private long mxId;
		private int guide;
		private String expertise;
		private String tags;
		private String areaCode;
		private int locationVisibility;
		private String regTime;
		private String qrcodeUrl;
		private String phoneNo;
		private String email;
		private List<AvatarBean> userAvatarList = new ArrayList<AvatarBean>();
		private String remark;
		private long followingCnt;
		private long followersCnt;
		private long friendsCnt;
		/** 关系 */
		private String relationship;
		private String showUserMsg;
		private int occupation;
		private String jobName;
		/**年龄是否可见  1--可见   0--不可见*/
		private int birthdayItype;

		public int getBirthdayItype() {
			return birthdayItype;
		}

		public void setBirthdayItype(int birthdayItype) {
			this.birthdayItype = birthdayItype;
		}

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName;
		}

		public int getOccupation() {
			return occupation;
		}

		public void setOccupation(int occupation) {
			this.occupation = occupation;
		}

		public void setFollowingCnt(long followingCnt) {
			this.followingCnt = followingCnt;
		}

		public void setFollowersCnt(long followersCnt) {
			this.followersCnt = followersCnt;
		}

		public void setFriendsCnt(long friendsCnt) {
			this.friendsCnt = friendsCnt;
		}

		public String getShowUserMsg() {
			return showUserMsg;
		}

		public void setShowUserMsg(String showUserMsg) {
			this.showUserMsg = showUserMsg;
		}

		public int getGuide() {
			return guide;
		}

		public void setGuide(int guide) {
			this.guide = guide;
		}

		public long getFollowingCnt() {
			return followingCnt;
		}

		public long getFollowersCnt() {
			return followersCnt;
		}

		public long getFriendsCnt() {
			return friendsCnt;
		}

		public void setFollowingCnt(Long followingCnt) {
			this.followingCnt = followingCnt;
		}

		public void setFollowersCnt(Long followersCnt) {
			this.followersCnt = followersCnt;
		}

		public void setFriendsCnt(Long friendsCnt) {
			this.friendsCnt = friendsCnt;
		}

		public String getRemark() {
			if (remark != null)
				return remark;
			return "";
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getRelationship() {
			return relationship;
		}

		public void setRelationship(String relationship) {
			this.relationship = relationship;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public String getRegTime() {
			if (regTime != null)
				return regTime;
			return "";
		}

		public void setRegTime(String regTime) {
			this.regTime = regTime;
		}

		public String getNickName() {
			if (nickName != null)
				return nickName;
			else
				return "";
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public int getSex() {

			return sex;
		}

		public void setSex(int sex) {
			this.sex = sex;
		}

		public long getBirthday() {
			return birthday;
		}

		public void setBirthday(long birthday) {
			this.birthday = birthday;
		}

		public void setBirthday(String birthdayStr) {
			if (null == birthdayStr || birthdayStr.equals("null")) {
				this.birthday = 0;
				return;
			}
			long dateFormat = 0;
			try {
				if (birthdayStr.contains("-")&&!birthdayStr.startsWith("-")) {
					dateFormat = DateUtils.getDateFormat(birthdayStr) / 1000;
				} else {
					dateFormat = Long.parseLong(birthdayStr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.birthday = dateFormat;

		}

		public int getBirthdayViableType() {
			return birthdayViableType;
		}

		public void setBirthdayViableType(int birthdayViableType) {
			this.birthdayViableType = birthdayViableType;
		}

		public String getLastDailyMood() {
			if (lastDailyMood != null)
				return lastDailyMood;
			else
				return "";
		}

		public void setLastDailyMood(String lastDailyMood) {
			this.lastDailyMood = lastDailyMood;
		}

		public String getHomeTown() {
			if (homeTown != null)
				return homeTown;
			else
				return "";
		}

		public void setHomeTown(String homeTown) {
			this.homeTown = homeTown;
		}

		public String getFavoritePlace() {
			if (favoritePlace != null)
				return favoritePlace;
			else
				return "";
		}

		public void setFavoritePlace(String favoritePlace) {
			this.favoritePlace = favoritePlace;
		}

		public String getSchool() {
			if (school != null)
				return school;
			else
				return "";
		}

		public void setSchool(String school) {
			this.school = school;
		}

		public String getMajor() {
			if (major != null)
				return major;
			else
				return "";
		}

		public void setMajor(String major) {
			this.major = major;
		}

		public String getExpertise() {
			if (expertise != null)
				return expertise;
			else
				return "";
		}

		public void setExpertise(String expertise) {
			this.expertise = expertise;
		}

		public String getTags() {
			if (tags != null)
				return tags;
			else
				return "";
		}

		public void setTags(String tags) {
			this.tags = tags;
		}

		public String getAreaCode() {
			if (null != areaCode)
				return areaCode;
			else
				return "";

		}

		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}

		public String getQrcodeUrl() {
			if (null != qrcodeUrl)
				return qrcodeUrl;
			else
				return "";
		}

		public void setQrcodeUrl(String qrcodeUrl) {
			this.qrcodeUrl = qrcodeUrl;
		}

		public List<AvatarBean> getUserAvatarList() {
			return userAvatarList;
		}

		public void setUserAvatarList(List<AvatarBean> userAvatarList) {
			this.userAvatarList = userAvatarList;
		}

		public long getMxId() {

			return mxId;

		}

		public void setMxId(long mxId) {
			this.mxId = mxId;
		}

		public int getLocationVisibility() {
			return locationVisibility;
		}

		public void setLocationVisibility(int locationVisibility) {
			this.locationVisibility = locationVisibility;
		}

		public String getPhoneNo() {
			if (null != phoneNo)
				return phoneNo;
			else
				return "";
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public String getEmail() {
			if (null != email)
				return email;
			else
				return "";
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}
}
