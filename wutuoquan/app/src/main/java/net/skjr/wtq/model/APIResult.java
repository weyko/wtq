package net.skjr.wtq.model;

import java.io.Serializable;

/**
 * 数据接口调用结果
 */
public class APIResult<T> implements Serializable {
    public boolean isSuccess;
    public String message = "";
    public T result;
}
