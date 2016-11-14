package chat.session.attachment;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;

import chat.common.util.storage.StorageType;
import chat.common.util.string.MD5;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public class FileAttachment implements MsgAttachment {
    protected String path;
    protected long size;
    protected String md5;
    protected String url;
    protected String displayName;
    protected String extension;
    private static final String KEY_PATH = "path";
    private static final String KEY_NAME = "name";
    private static final String KEY_SIZE = "size";
    private static final String KEY_MD5 = "md5";
    private static final String KEY_URL = "url";
    private static final String KEY_EXT = "ext";

    public FileAttachment() {
    }

    public FileAttachment(String var1) {
        this.fromJson(var1);
    }

    public String getPath() {
        String var1 = this.getPathForSave();
        return (new File(var1)).exists()?var1:null;
    }

    public String getPathForSave() {
        return "";
//        return !TextUtils.isEmpty(this.path)?this.path:c.a(this.getFileName(), this.storageType());
    }

    public String getThumbPath() {
        String var1 = this.getThumbPathForSave();
        return (new File(var1)).exists()?var1:null;
    }

    public String getThumbPathForSave() {
        return "";
//        return c.a(this.getFileName(), MsgType.f);
    }

    public void setPath(String var1) {
        this.path = var1;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long var1) {
        this.size = var1;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String var1) {
        this.md5 = var1;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String var1) {
        this.url = var1;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String var1) {
        this.extension = var1;
    }

    public String getFileName() {
        if(!TextUtils.isEmpty(this.path)) {
            String var1 = this.path;
            int var2;
            return (var2 = this.path.lastIndexOf(47)) != -1?var1.substring(var2 + 1, var1.length()):var1;
        } else {
            return TextUtils.isEmpty(this.md5)? MD5.getStringMD5(this.url):this.md5;
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String var1) {
        this.displayName = var1;
    }

    protected StorageType storageType() {
        return StorageType.TYPE_FILE;
    }

    protected void save(JSONObject var1) {
    }

    protected void load(JSONObject var1) {
    }

    public String toJson(boolean var1) {
        JSONObject var2 = new JSONObject();

        try {
            if(!var1 && !TextUtils.isEmpty(this.path)) {
                var2.put("path", this.path);
            }

            if(!TextUtils.isEmpty(this.md5)) {
                var2.put("md5", this.md5);
            }

            if(!TextUtils.isEmpty(this.displayName)) {
                var2.put("name", this.displayName);
            }

            var2.put("url", this.url);
            var2.put("size", this.size);
            if(!TextUtils.isEmpty(this.extension)) {
                var2.put("ext", this.extension);
            }

            this.save(var2);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return var2.toString();
    }

    private void fromJson(String var1) {
        JSONObject var2 = JsonManager.a(var1);
        this.path = JsonManager.d(var2, "path");
        this.md5 = JsonManager.d(var2, "md5");
        this.url = JsonManager.d(var2, "url");
        this.displayName = JsonManager.d(var2, "name");
        this.size = JsonManager.b(var2, "size");
        this.extension = JsonManager.d(var2, "ext");
        this.load(var2);
    }
}
