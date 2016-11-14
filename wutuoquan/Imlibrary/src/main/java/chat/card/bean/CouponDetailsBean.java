/**
 *
 */
package chat.card.bean;
import java.io.Serializable;

import chat.volley.WBaseBean;

/**
 *
 * @ClassName CouponDetailsBean
 * @Description 优惠券详情
 */
public class CouponDetailsBean extends WBaseBean {


    private CouponBean Data = new CouponBean();




    public CouponBean getData() {
        return Data;
    }




    public void setData(CouponBean data) {
        Data = data;
    }




    public static class CouponBean implements Serializable
    {
        private String goodsId;
        private String shopId;
        private String name;
        private String picUrl;
        private String valid;
        private int getCouponNumber;
        private String expirationTime;
        private String couponName;
        private String couponPrice;
        private String priceSymbol;
        private int goodsType;
        private String receiveCount;
        private String shopLogo;
        private String start;
        private String shopAddress;
        private int shopCount;
        private String ruleContent;
        private String internalCode;
        private String shopName;
        private String shopPhone;
        private String beginTime;
        private String endTime;
        private int companyStatus;

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public int getCompanyStatus() {
            return companyStatus;
        }

        public void setCompanyStatus(int companyStatus) {
            this.companyStatus = companyStatus;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getShopPhone() {
            return shopPhone;
        }

        public void setShopPhone(String shopPhone) {
            this.shopPhone = shopPhone;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
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
            this.valid = valid;
        }

        public int getGetCouponNumber() {
            return getCouponNumber;
        }

        public void setGetCouponNumber(int getCouponNumber) {
            this.getCouponNumber = getCouponNumber;
        }

        public String getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(String expirationTime) {
            this.expirationTime = expirationTime;
        }

        public String getCouponPrice() {
            return couponPrice;
        }

        public void setCouponPrice(String couponPrice) {
            this.couponPrice = couponPrice;
        }

        public String getPriceSymbol() {
            return priceSymbol;
        }

        public void setPriceSymbol(String priceSymbol) {
            this.priceSymbol = priceSymbol;
        }

        public int getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(int goodsType) {
            this.goodsType = goodsType;
        }

        public String getReceiveCount() {
            return receiveCount;
        }

        public void setReceiveCount(String receiveCount) {
            this.receiveCount = receiveCount;
        }

        public String getShopLogo() {
            return shopLogo;
        }

        public void setShopLogo(String shopLogo) {
            this.shopLogo = shopLogo;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getShopAddress() {
            return shopAddress;
        }

        public void setShopAddress(String shopAddress) {
            this.shopAddress = shopAddress;
        }

        public int getShopCount() {
            return shopCount;
        }

        public void setShopCount(int shopCount) {
            this.shopCount = shopCount;
        }

        public String getRuleContent() {
            return ruleContent;
        }

        public void setRuleContent(String ruleContent) {
            this.ruleContent = ruleContent;
        }

        public String getInternalCode() {
            return internalCode;
        }

        public void setInternalCode(String internalCode) {
            this.internalCode = internalCode;
        }
    }
}
