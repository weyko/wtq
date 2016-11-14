package chat.session.attachment;

import org.json.JSONObject;

import chat.common.util.storage.StorageType;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public class VideoAttachment extends FileAttachment {
    private int width;
    private int height;
    private long duration;
    private static final String KEY_DURATION = "dur";
    private static final String KEY_WIDTH = "w";
    private static final String KEY_HEIGHT = "h";

    public VideoAttachment() {
    }

    public VideoAttachment(String var1) {
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

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long var1) {
        this.duration = var1;
    }

    protected StorageType storageType() {
        return StorageType.TYPE_VIDEO;
    }

    protected void save(JSONObject var1) {
        JsonManager.a(var1, "w", this.width);
        JsonManager.a(var1, "h", this.height);
        JsonManager.a(var1, "dur", this.duration);
    }

    protected void load(JSONObject var1) {
        this.width = JsonManager.a(var1, "w");
        this.height = JsonManager.a(var1, "h");
        this.duration = (long)JsonManager.a(var1, "dur");
    }
}
