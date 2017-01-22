package com.rxbus.compiler;


import com.google.auto.service.AutoService;
import com.rxbus.BindRxBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * 自定义生成class文件
 */
@AutoService(Processor.class)
public class RxBusProcesser extends AbstractProcessor {

    private static final String TAG = "RxBusProcesser";

    private Filer mFiler;// 文件相关辅导类
    private Elements mElements;// 元素相关
    private Messager mMessager;// 日志

    private Map<String, GenerateClass> mAnnotatedClassMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        System.out.println("====  RxBusProcesser -> init ====");
        mFiler = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    /**
     * 声明支持的注释
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        System.out.println("====  RxBusProcesser -> getSupportedAnnotationTypes ====");
        Set<String> types = new LinkedHashSet<>();
        types.add(BindRxBus.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("====  RxBusProcesser -> process ====");
        processBindRxBus(roundEnvironment);
        for (GenerateClass annotatedClass : mAnnotatedClassMap.values()) {
            try {
                info("Generating file for %s", annotatedClass.getFullClassName());
                annotatedClass.generateJavaFile().writeTo(mFiler);
            } catch (IOException e) {
                System.out.println("Generate file failed, reason: %s" + e.getMessage());
                return true;
            }
        }
        return true;
    }

    private void processBindRxBus(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindRxBus.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                GenerateClass generateClass = getAnnotatedClass(element);
            }
        }
    }

    private GenerateClass getAnnotatedClass(Element element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String fullClassName = classElement.getQualifiedName().toString();
        GenerateClass annotatedClass = mAnnotatedClassMap.get(fullClassName);
        if (annotatedClass == null) {
            annotatedClass = new GenerateClass(classElement, mElements);
            mAnnotatedClassMap.put(fullClassName, annotatedClass);
        }
        return annotatedClass;
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
