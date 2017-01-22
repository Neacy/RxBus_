package com.rxbus.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

/**
 * Created by jayuchou on 17/1/19.
 */
public class BindRxBusMethod {

    private ExecutableElement methodElement;
    private Name methodName;

    public BindRxBusMethod(Element element) {
        methodElement = (ExecutableElement) element;
        methodName = methodElement.getSimpleName();
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public void setMethodElement(ExecutableElement methodElement) {
        this.methodElement = methodElement;
    }

    public Name getMethodName() {
        return methodName;
    }

    public void setMethodName(Name methodName) {
        this.methodName = methodName;
    }
}
