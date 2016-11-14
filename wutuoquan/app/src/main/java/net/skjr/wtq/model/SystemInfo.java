
package net.skjr.wtq.model;


import java.io.Serializable;

/**
 * result-getSystemInfo
 */
public class SystemInfo implements Serializable {
    public String versionCode;
    public String versionName;
    public String versionMessage;
    public String url;

    public String iosUpdate;

    public String serverUrl;

    public String startPageCode;
    public String startPageUrl;

    public String serverPhone;

    public String accountPageUrl;
}

/*

 "serverUrl":"http://hszt.daliuliang.com.cn",
        "versionCode":"1",
        "versionMessage":"没啥更新的",
        "versionName":"1.0.1",
        "iosUpdate":"1",
        "url":"http://a.apk",
        "startPageCode":"20160413",
        "startPageUrl":"/mobile/startPage?code={startPageCode}"

 */
