package chat.session.attachment;

import org.json.JSONObject;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public class LocationAttachment implements MsgAttachment {
    private double latitude;
    private double longitude;
    private String address;
    private static final String KEY_LATITUDE = "lat";
    private static final String KEY_LONGITUDE = "lng";
    private static final String KEY_DESC = "title";

    public LocationAttachment() {
    }

    public LocationAttachment(String var1) {
        this.fromJson(var1);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double var1) {
        this.latitude = var1;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double var1) {
        this.longitude = var1;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String var1) {
        this.address = var1;
    }

    private void fromJson(String var1) {
        JSONObject var2 = JsonManager.a(var1);
        this.latitude = JsonManager.c(var2, "lat");
        this.longitude = JsonManager.c(var2, "lng");
        this.address = JsonManager.d(var2, "title");
    }

    public String toJson(boolean var1) {
        JSONObject var3 = new JSONObject();

        try {
            var3.put("lat", this.latitude);
            var3.put("lng", this.longitude);
            var3.put("title", this.address);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return var3.toString();
    }
}
