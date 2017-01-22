package com.rxbus.compiler;

import com.squareup.javapoet.ClassName;

/**
 * Created by jayuchou on 17/1/19.
 */
public class TypeUtil {

    public static final ClassName RXBUS = ClassName.get("com.library.rxbus", "RxBusDao");
    public static final ClassName IRXBUS = ClassName.get("com.library.rxbus", "IRxBus");
}
