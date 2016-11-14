package chat.session.attachment;
import org.json.JSONException;
import org.json.JSONObject;

import chat.common.util.storage.StorageType;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public class AudioAttachment extends FileAttachment {
    private long duration;
    private static final String KEY_DURATION = "dur";

    public AudioAttachment() {
    }

    public AudioAttachment(String var1) {
        super(var1);
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long var1) {
        this.duration = var1;
    }

    protected StorageType storageType() {
        return StorageType.TYPE_AUDIO;
    }

    protected void save(JSONObject var1) {
        try {
            var1.put("dur", this.duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void load(JSONObject var1) {
        try {
            this.duration = (long)var1.getInt("dur");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
