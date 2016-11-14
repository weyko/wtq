package net.skjr.wtq.viewmodel;

/**
 * 用于界面录入时,检查输入情况的数据对象
 */
public class CheckResult {

    public boolean isSuccess;
    public String errorMessage;

    public CheckResult(boolean isSuccess, String errorMessage) {
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
    }

    public static CheckResult success(){
        return new CheckResult(true, "");
    }

    public static CheckResult failure(String errorMessage){
        return new CheckResult(false, errorMessage);
    }
}
