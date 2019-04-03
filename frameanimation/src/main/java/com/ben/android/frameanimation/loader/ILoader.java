package com.ben.android.frameanimation.loader;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/19
 */
public interface ILoader<T,R> {
    T loader(R r);

    void free();
}
