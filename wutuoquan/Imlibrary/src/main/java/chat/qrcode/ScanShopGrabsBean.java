package chat.qrcode;

import chat.volley.WBaseBean;

/**
 * Created by LiuMin on 2016/5/18.
 */
public class ScanShopGrabsBean extends WBaseBean {
    private Grabs data;

    public Grabs getData() {
        return data;
    }

    public void setData(Grabs data) {
        this.data = data;
    }

    public class Grabs {
        private String needReturnInfo;
        private String buttonInfo;
        private String linkClose;

        public String getNeedReturnInfo() {
            return needReturnInfo;
        }

        public void setNeedReturnInfo(String needReturnInfo) {
            this.needReturnInfo = needReturnInfo;
        }

        public String getButtonInfo() {
            return buttonInfo;
        }

        public void setButtonInfo(String buttonInfo) {
            this.buttonInfo = buttonInfo;
        }

        public String getLinkClose() {
            return linkClose;
        }

        public void setLinkClose(String linkClose) {
            this.linkClose = linkClose;
        }
    }
}
