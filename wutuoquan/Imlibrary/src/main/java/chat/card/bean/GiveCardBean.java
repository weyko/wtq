/**
 * 
 */
package chat.card.bean;

import java.io.Serializable;
import java.util.ArrayList;

import chat.volley.WBaseBean;

/**
 * 
 * @ClassName GiveCardBean
 * @Description 赠送券数据
 */
public class GiveCardBean extends WBaseBean implements Serializable {
	private static final long serialVersionUID = -6002117199897082470L;
	
	private String cardUrl;
	private String cardName;
	private String cardValid;
	private int maxNo;
	private String message;
	private String giveId;
	private String cardId;
	private String accepterId;
	public String limit;
	private String moPrice;
	private String priceSymbol;

	public String getMoPrice() {
		return moPrice;
	}

	public void setMoPrice(String moPrice) {
		this.moPrice = moPrice;
	}

	public String getPriceSymbol() {
		return priceSymbol;
	}

	public void setPriceSymbol(String priceSymbol) {
		this.priceSymbol = priceSymbol;
	}

	private ArrayList<ExchangeDetailsBean.ExchangeBean.ExchangeQrBean> List = new ArrayList<ExchangeDetailsBean.ExchangeBean.ExchangeQrBean>();
	public ArrayList<ExchangeDetailsBean.ExchangeBean.ExchangeQrBean> getList() {
		return List;
	}
	public void setList(ArrayList<ExchangeDetailsBean.ExchangeBean.ExchangeQrBean> list) {
		List = list;
	}
	public String getAccepterId() {
		return accepterId;
	}
	public void setAccepterId(String accepterId) {
		this.accepterId = accepterId;
	}
	public String getGiveId() {
		return giveId;
	}
	public void setGiveId(String giveId) {
		this.giveId = giveId;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getCardUrl() {
		return cardUrl;
	}
	public void setCardUrl(String cardUrl) {
		this.cardUrl = cardUrl;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardValid() {
		return cardValid;
	}
	public void setCardValid(String cardValid) {
		this.cardValid = cardValid;
	}
	public int getMaxNo() {
		return maxNo;
	}
	public void setMaxNo(int maxNo) {
		this.maxNo = maxNo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
