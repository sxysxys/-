package com.shen.shengeunion.base;

/**
 * 内容提供者的回调接口
 * @param <T>
 */
public interface IBasePresenter<T> {
    void registerCallback(T callback);

    void unregisterCallback(T callback);
}
