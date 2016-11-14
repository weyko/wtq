package chat.common.util.output;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import chat.base.IMClient;
import chat.common.Contact;
import chat.common.util.file.FileUtils;
import chat.common.util.network.Network;
import chat.common.util.time.TimeUtil;

/**
 * Description: 日志打印工具类
 * Created  by: weyko on 2016/4/5.
 */
public class LogUtil {
    private static final String TAG = "weyko";
    /**
     * isDebug 是否调试模式 （调试模式会开启日志打印）
     */
    private static boolean isDebug = true;

    private static boolean openRequestLog = false;

    private final static String logTag="moxian2_log";

    private final static String requestTag="request_log_";

    private static boolean clearExpireFilesFlag=true;
    /**
     * 日志文件缓存目录
     */
    private static String logCacheDir= Environment.getExternalStorageDirectory()
            + File.separator+logTag+ File.separator;

    /**
     *
     * @param cacheDir 日志文件缓存目录
     */
    public static void setLogCacheDir(String cacheDir){
        logCacheDir = cacheDir;
        if(isDebug){
            createCacheDir();
        }
    }

    private static void createCacheDir(){
        File file = new File(logCacheDir);
        if(!file.exists()){
            file.mkdir();
        }else{
            deleteExpireFiles(logCacheDir,requestTag,7);
        }
    }

    /**
     * 是否为调试模式
     * @return
     */
    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 设置是否为调试模式
     * @param debug
     */
    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

	/*public static void v(String msg){
		if(isDebug)
			Log.v(logTag, msg);
	}

	public static void v(String tag,String msg) {
		if (isDebug)
			Log.v(tag, msg);
	}*/

    public static void i(String msg){
        if(isDebug)
            Log.i(logTag, msg);
    }
    public static void i(String tag,String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void w(String msg){
        if(isDebug)
            Log.w(logTag, msg);
    }
    public static void w(String tag,String msg) {
        if (isDebug)
            Log.w(tag, msg);
    }

    public static void e(String msg){
        if(isDebug)
            Log.e(logTag, msg);
    }
    public static void e(String tag,String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }
    /**
     * 写日志文件
     * @param tag
     * @param text
     */
    public static void writeLogtoFile(String tag, String text){
        if(isDebug){
            System.out.println(text);
            createCacheDir();
            String time = TimeUtil.getCurrentTime(TimeUtil.TimeFormatType.TIME_FOEMAT_STANDARD);
            String needWriteMessage = time + "    " + tag + "    " + text;
            File file = new File(logCacheDir, "mx_log_" + TimeUtil.getCurrentTime(TimeUtil.TimeFormatType.TIME_FOEMAT_Y_M_D)+".txt");
            try {
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @Title: writeFluxLogtoFile
     * @param:
     * @Description: 流量监听日志
     * @return void
     */
    public static void writeFluxLogtoFile(String text){
        if(isDebug){
            System.out.println(text);
            createCacheDir();
            String needWriteMessage = text+"\r\n";
            File file = new File(logCacheDir, "mxflux.txt");
            if(file.exists()&&file.length()>200000){
                file.delete();
            }
            try {
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void writeCommonLogtoFile(String text){
        if(isDebug){
            System.out.println(text);
            createCacheDir();
            String needWriteMessage = text+"\r\n";
            File file = new File(logCacheDir, "mxcommon.txt");
            if(file.exists()&&file.length()>200000){
                file.delete();
            }
            try {
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 写日志文件
     * @param text
     */
    public static void writeRequestLogtoFile(String text){
        if(isDebug){
            System.out.println(text);
            if (openRequestLog){
                createCacheDir();
                String needWriteMessage = text+"\r\n";
                File file = new File(logCacheDir, requestTag + TimeUtil.getCurrentTime(TimeUtil.TimeFormatType.TIME_FOEMAT_Y_M_D)+".txt");
                try {
                    FileWriter filerWriter = new FileWriter(file, true);
                    BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                    bufWriter.write(needWriteMessage);
                    bufWriter.newLine();
                    bufWriter.close();
                    filerWriter.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * http 响应信息格式化
     * @param startTime
     * @param code
     * @param msg
     * @return
     */
    public static synchronized String responseInfoFromat(long startTime,String code,String msg){
        long endTime = System.currentTimeMillis();
        String str="cost time:"+(endTime-startTime)+"|http responseCode="+code+"|";
        str += msg;
        return str;
    }

    /**
     * 当前时间和网络类型
     * @param context
     * @return
     */
    public static synchronized String timeAndNetType(Context context){
        String str=TimeUtil.getCurrentTime(TimeUtil.TimeFormatType.TIME_FOEMAT_STANDARD);
        if(context!=null){
            Network mNetwork = new Network(context);
            str += "|"+mNetwork.getConnectedType();
            mNetwork = null;
            System.gc();
        }
        str += "|";
        return str;
    }

    /**
     * 格式化请求url和参数，并返回格式化后的字符串
     * @param url
     * @param mParams
     * @return
     */
    public static synchronized String urlLogFormat(String url,Map<String, Object> mParams){
        String str=url;
        if(mParams!=null){
            JSONObject jsonObject=new JSONObject(mParams);
            str += "|"+jsonObject.toString();
            jsonObject = null;
            System.gc();
        }else{
            str += "|null";
        }
        str += "|";
        return str;
    }

    private static void deleteExpireFiles(final String dir, final String tag, final int timeLen){
        if(clearExpireFilesFlag){
            clearExpireFilesFlag=false;
            new Thread(new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    FileUtils.deleteFilesInDirectory(dir, tag, timeLen);
                }

            }).start();
        }
    }
    /**
     * print the log
     * @param log
     */
    public static void d(String log) {
        if (Contact.isDebug) {
            Log.d(TAG, IMClient.getInstance().getContext().getClass().getName() + "-------->" + log);
        }
    }
    /**
     * print the log
     * @param  tag
     * @param  log
     */
    public static void d(String tag,String log) {
        if (Contact.isDebug) {
            Log.d(tag, IMClient.getInstance().getClass().getName() + "-------->" + log);
        }
    }
}
