package chat.session.attachment;
import org.json.JSONObject;

import chat.common.util.storage.StorageType;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public class ImageAttachment extends FileAttachment {
    private static final String KEY_WIDTH = "w";
    private static final String KEY_HEIGHT = "h";
    private int width;
    private int height;

    public ImageAttachment() {
    }

    public ImageAttachment(String var1) {
        super(var1);
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int var1) {
        this.width = var1;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int var1) {
        this.height = var1;
    }

    public boolean isHdImage() {
        return false;
    }

    protected StorageType storageType() {
        return StorageType.TYPE_AUDIO;
    }

    protected void save(JSONObject var1) {
        JsonManager.a(var1, "w", this.width);
        JsonManager.a(var1, "h", this.height);
    }

    protected void load(JSONObject var1) {
        this.width = JsonManager.a(var1, "w");
        this.height = JsonManager.a(var1, "h");
    }
}
