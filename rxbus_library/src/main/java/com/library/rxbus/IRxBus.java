package com.library.rxbus;

/**
 * Created by jayuchou on 17/1/19.
 */

public interface IRxBus<T> {
    void inject(T o);
}
