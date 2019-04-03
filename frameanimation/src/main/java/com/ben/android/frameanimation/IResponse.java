package com.ben.android.frameanimation;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/19
 * @desc 负责接收来自生产者处理后的bitmap
 */
public interface IResponse<T> {
    void response(T t);
}
