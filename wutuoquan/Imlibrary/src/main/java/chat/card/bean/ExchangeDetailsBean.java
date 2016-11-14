/**
 * 
 */
package chat.card.bean;

import com.imlibrary.R;

import java.io.Serializable;
import java.util.ArrayList;

import chat.base.IMClient;
import chat.volley.WBaseBean;

/**
 * 
 * @ClassName ExchangeDetailsBean
 * @Description 兑换券详情
 */
public class ExchangeDetailsBean extends WBaseBean {
	
	private static final long serialVersionUID = 3614710788904958427L;
	private ExchangeBean Data = new ExchangeBean();




	public ExchangeBean getData() {
		return Data;
	}




	public void setData(ExchangeBean data) {
		Data = data;
	}




	public static class ExchangeBean implements Serializable
	{

		private static final long serialVersionUID = 3680528057109947987L;
		private String goodsId;
		private String name;
		private String picUrl;
		private String valid;
		private String count;
		private String moPrice;
		private String price;
		private String detail;
		private String shopName;
		private String shoplogo;
		private String couponsId;
		private String detailedAddress;
		private String distance;
		private String goodsSummary;
		private String latitude;
		private String longitude;
		private String orderId;
		private String shopCount;
		private String shopId;
		private String goodStatus;
		private String snapshotNo;
		private String priceSymbol="";
		private String goodsMultiName;
		private String shopPhone;
		private String star;
		/**公司认证状态 0 没有填写资质认证  1 待审核、2 审核中、3 审核通过、4 审核退回（失败）5 不需要认证*/
		private int companyStatus;

		public String getShopPhone() {
			return shopPhone;
		}

		public void setShopPhone(String shopPhone) {
			this.shopPhone = shopPhone;
		}

		public String getStar() {
			return star;
		}

		public void setStar(String star) {
			this.star = star;
		}

		public int getCompanyStatus() {
			return companyStatus;
		}

		public void setCompanyStatus(int companyStatus) {
			this.companyStatus = companyStatus;
		}

		public String getGoodsMultiName() {
			return goodsMultiName;
		}

		public void setGoodsMultiName(String goodsMultiName) {
			this.goodsMultiName = goodsMultiName;
		}

		public String getPriceSymbol() {
			return priceSymbol;
		}

		public void setPriceSymbol(String priceSymbol) {
			this.priceSymbol = priceSymbol;
		}


		private ArrayList<ExchangeQrBean> List = new ArrayList<ExchangeQrBean>();

		public ArrayList<ExchangeQrBean> getList() {
			return List;
		}

		public void setList(ArrayList<ExchangeQrBean> list) {
			List = list;
		}


		public String getGoodStatus() {
			return goodStatus;
		}

		public void setGoodStatus(String goodStatus) {
			this.goodStatus = goodStatus;
		}

		public String getSnapshotNo() {
			return snapshotNo;
		}

		public void setSnapshotNo(String snapshotNo) {
			this.snapshotNo = snapshotNo;
		}

		public String getGoodsId() {
			return goodsId;
		}


		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public String getPicUrl() {
			return picUrl;
		}


		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}


		public String getValid() {
			return valid;
		}


		public void setValid(String valid) {
			if(valid==null||valid.length()==0)//无限期
			{
				this.valid = IMClient.getInstance().getContext().getResources().getString(R.string.no_overdue);
			}else{
				this.valid = valid;
			}
		}


		public String getCount() {
			return count;
		}


		public void setCount(String count) {
			this.count = count;
		}


		public String getMoPrice() {
			return moPrice;
		}


		public void setMoPrice(String moPrice) {
			this.moPrice = moPrice;
		}


		public String getPrice() {
			return price;
		}


		public void setPrice(String price) {
			this.price = price;
		}




		public String getDetail() {
			return detail;
		}


		public void setDetail(String detail) {
			this.detail = detail;
		}


		public String getShopName() {
			return shopName;
		}


		public void setShopName(String shopName) {
			this.shopName = shopName;
		}


		public String getShoplogo() {
			return shoplogo;
		}


		public void setShoplogo(String shoplogo) {
			this.shoplogo = shoplogo;
		}


		public String getCouponsId() {
			return couponsId;
		}


		public void setCouponsId(String couponsId) {
			this.couponsId = couponsId;
		}


		public String getDetailedAddress() {
			return detailedAddress;
		}


		public void setDetailedAddress(String detailedAddress) {
			this.detailedAddress = detailedAddress;
		}


		public String getDistance() {
			return distance;
		}


		public void setDistance(String distance) {
			this.distance = distance;
		}


		public String getGoodsSummary() {
			return goodsSummary;
		}


		public void setGoodsSummary(String goodsSummary) {
			this.goodsSummary = goodsSummary;
		}


		public String getLatitude() {
			return latitude;
		}


		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}


		public String getLongitude() {
			return longitude;
		}


		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}


		public String getOrderId() {
			return orderId;
		}


		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}


		public String getShopCount() {
			return shopCount;
		}


		public void setShopCount(String shopCount) {
			this.shopCount = shopCount;
		}


		public String getShopId() {
			return shopId;
		}


		public void setShopId(String shopId) {
			this.shopId = shopId;
		}


		public static class ExchangeQrBean implements Serializable
		{

			private static final long serialVersionUID = 1265019698723111144L;
			private String couponsId;
			private String picCode;
			private String cardNumber;
			public String getCouponsId() {
				return couponsId;
			}
			public void setCouponsId(String couponsId) {
				this.couponsId = couponsId;
			}
			public String getPicCode() {
				return picCode;
			}
			public void setPicCode(String picCode) {
				this.picCode = picCode;
			}
			public String getCardNumber() {
				return cardNumber;
			}
			public void setCardNumber(String cardNumber) {
				this.cardNumber = cardNumber;
			}
			
		}
	}
}
