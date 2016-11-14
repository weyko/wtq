package chat.common.fileupload;

import java.io.Serializable;

/**
 * Description:文件上传之后的实体类
 * Created  by: weyko on 2016/10/31.
 */

public class UploadBean implements Serializable{
    private UploadData data;
    private String info;
    private String msg;
    private int status;

    public UploadData getData() {
        return data;
    }

    public void setData(UploadData data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

   public class UploadData{
        private String exName;
        private String saveName;
        private String savePath;
        private int size;

        public String getExName() {
            return exName;
        }

        public void setExName(String exName) {
            this.exName = exName;
        }

        public String getSaveName() {
            return saveName;
        }

        public void setSaveName(String saveName) {
            this.saveName = saveName;
        }

        public String getSavePath() {
            return savePath;
        }

        public void setSavePath(String savePath) {
            this.savePath = savePath;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
    public boolean isResult(){
        return status==1;
    }
}
