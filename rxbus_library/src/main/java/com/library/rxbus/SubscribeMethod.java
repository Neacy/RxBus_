package com.library.rxbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jayuchou on 17/1/18.
 */
public class SubscribeMethod {

    public Method method;
    public Object subscriber;
    public Class<?> event;

    public SubscribeMethod(Method method, Object subseriber, Class<?> event) {
        this.method = method;
        this.subscriber = subseriber;
        this.event = event;
    }

    public void invoke(Object event) {
        try {
            method.invoke(subscriber, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
