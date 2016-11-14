package net.skjr.wtq.core.utils.common;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Serialize Utils
 */
public class SerializeUtils {

    public static <E> String list2String(List<E> list) {
        //实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream oos = null;
        String listString = "";
        try {
            oos = new ObjectOutputStream(baos);
            //writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
            oos.writeObject(list);
            //最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
            listString = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            //关闭oos
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listString;
    }

    public static String obj2Str(Object obj) {
        if (obj == null) {
            return "";
        }
        //实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream oos = null;
        String listString = "";
        try {
            oos = new ObjectOutputStream(baos);
            //writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
            oos.writeObject(obj);
            //最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
            listString = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            //关闭oos
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listString;
    }

    //将序列化的数据还原成Object
    public static Object str2Obj(String str) {
        try {
            byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
            ObjectInputStream ois = new ObjectInputStream(bais);


            return ois.readObject();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static <E> List<E> string2List(String str) {
        List<E> stringList = null;

        try {
            byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
            ObjectInputStream ois = new ObjectInputStream(bais);

            stringList = (List<E>) ois.readObject();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return stringList;
    }


    /**
     * deserialization from file
     *
     * @param filePath
     * @return
     * @throws RuntimeException if an error occurs
     */
    public static Object deserialization(String filePath) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filePath));
            Object o = in.readObject();
            in.close();
            return o;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * serialize to file
     *
     * @param filePath
     * @param obj
     * @return
     * @throws RuntimeException if an error occurs
     */
    public static void serialization(String filePath, Object obj) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filePath));
            out.writeObject(obj);
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }
}
