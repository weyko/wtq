package chat.common.util.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatDownLoadFile {

    private static ArrayList<String> downloadList = new ArrayList<String>();

    public static String downloadFile(String cacheDir, String fileName, String url) {
        File file0 = new File(cacheDir);
        if (!file0.exists()) {
            file0.mkdirs();
        }
        String filename = cacheDir + fileName;
        File file = new File(filename);
        if (file.exists()) {
            if (file.length() > 0) {
                return filename;
            }
        }
        if (downloadList.contains(url)) {
            return null;
        }
        URL newUrl;
        File temp = new File(filename + "_temp");// 下载过程中的临时文件
        InputStream is = null;
        FileOutputStream fos = null;
        HttpURLConnection conn = null;
        try {
            newUrl = new URL(url);
            conn = (HttpURLConnection) newUrl
                    .openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            downloadList.add(url);
            if (!temp.exists()) {
                temp.createNewFile();
            }
            fos = new FileOutputStream(temp.getAbsolutePath());
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
            }
            temp.renameTo(file);// 下载完成重命名
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            downloadList.remove(url);
        }
        return null;
    }
}