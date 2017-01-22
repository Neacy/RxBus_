package com.library.rxbus;

import android.util.ArrayMap;

/**
 * Created by jayuchou on 17/1/19.
 */
public class RxBus {

    private volatile static RxBus INSTANCE = null;

    private ArrayMap<String, IRxBus> mRxBuses = new ArrayMap<>();

    public static RxBus getInstance() {
        if (INSTANCE == null) {
            synchronized (RxBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxBus();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 注册
     */
    public void register(Object o) {
        String className = o.getClass().getName();
        try {
            IRxBus iRxBus = mRxBuses.get(className);
            if (iRxBus == null) {
                Class<?> _class = Class.forName(className + "$$RxBus");
                iRxBus = (IRxBus) _class.newInstance();
                mRxBuses.put(className, iRxBus);
            }
            iRxBus.inject(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反注册
     */
    public void unregister(Object o) {
        RxBusDao.getInstance().unrigister(o);
    }
}
