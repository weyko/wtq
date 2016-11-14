package chat.service.time;
import chat.volley.WBaseBean;

public class ServiceTimeBean extends WBaseBean {
    private ServiceTime data;

    public ServiceTime getData() {
        return data;
    }

    public void setData(ServiceTime data) {
        this.data = data;
    }
    public class ServiceTime {
        private String serverTime;
        public String getServerTime() {
            return serverTime;
        }
        public void setServerTime(String serverTime) {
            this.serverTime = serverTime;
        }
    }
}
