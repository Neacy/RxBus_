package com.library.rxbus;

import android.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;


/**
 * Created by jayuchou on 17/1/18.
 */
public class RxBusDao {

    private static final String TAG = "RxBus";

    // 单例对象
    private volatile static RxBusDao INSTANCE;

    // 主题
    private final SerializedSubject mBus;

    // 存放当前注册的类下所有的监听函数
    private ArrayMap<Object, List<Class<?>>> mEventTypeBySubscribers = new ArrayMap<>();

    // 存放相同参数的函数列表
    private ArrayMap<Class<?>, List<SubscribeMethod>> mSubscribeMethodsByEventType = new ArrayMap<>();

    // 存放当前的Rx监听<用于退出界面的时候解除接听>
    private ArrayMap<Object, Subscription> mSubscriptions = new ArrayMap<>();

    public RxBusDao() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    private <T>Observable<T> toObservable(Class<?> type) {
        return mBus.ofType(type);
    }

    private boolean hasObserver() {
        return mBus.hasObservers();
    }

    /**
     * 从单例中读取对象
     */
    public static RxBusDao getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBusDao.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxBusDao();
                }
            }
        }
        return INSTANCE;
    }

    public void register(Object o) {
        // 说明已经注册过。
        if (mEventTypeBySubscribers.get(o) != null) {
            return;
        }
        Method[] methods = o.getClass().getDeclaredMethods();
        Log.w(TAG, "=== register === " + methods.length);
        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] paramsType = method.getParameterTypes();
                Log.w(TAG, "=== register.paramsType === " + paramsType.length);
                if (paramsType != null && paramsType.length == 1) {
                    Class<?> eventClass = paramsType[0];
                    addEventTypeToMap(o, eventClass);
                    SubscribeMethod subscribeMethod = new SubscribeMethod(method, o, eventClass);
                    addSubscribeMethods(eventClass, subscribeMethod);

                    Subscription subscription = toObservable(subscribeMethod.event).subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object _o) {
                            Log.e(TAG, "=== call === " + _o.toString());
                            responseEvent(_o);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e(TAG, "=== throwable === " + throwable);
                        }
                    });
                    mSubscriptions.put(o, subscription);
                }
            }
        }
    }

    public void unrigister(Object o) {
        if (!mEventTypeBySubscribers.isEmpty()) {
            mEventTypeBySubscribers.remove(o);
        }
        if (!mSubscriptions.isEmpty()) {
            Subscription subscription = mSubscriptions.get(o);
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            mSubscriptions.remove(o);
        }
    }

    public void post(Object event) {
        Log.w(TAG, "=== hasObserver() === " + hasObserver());
        mBus.onNext(event);
    }

    private void responseEvent(Object o) {
        List<SubscribeMethod> methods = mSubscribeMethodsByEventType.get(o.getClass());
        for (SubscribeMethod method : methods) {
            method.invoke(o);
        }
    }

    private void addEventTypeToMap(Object subscriber, Class<?> eventType) {
        List<Class<?>> _classes = mEventTypeBySubscribers.get(subscriber);
        if (_classes == null) {
            _classes = new ArrayList<>();
            mEventTypeBySubscribers.put(subscriber, _classes);
        }
        if (!_classes.contains(eventType)) {
            _classes.add(eventType);
        }
    }

    private void addSubscribeMethods(Class<?> method, SubscribeMethod subscribeMethod) {
        List<SubscribeMethod> sms = mSubscribeMethodsByEventType.get(method);
        if (sms == null) {
            sms = new ArrayList<>();
            mSubscribeMethodsByEventType.put(method, sms);
        }

        if (!sms.contains(subscribeMethod)) {
            sms.add(subscribeMethod);
        }
    }
}
