package com.rxbus.compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by jayuchou on 17/1/19.
 */
public class GenerateClass {

    public TypeElement typeElement;
    public Elements elements;
    public List<BindRxBusMethod> methods;

    public GenerateClass(TypeElement typeElement, Elements elements) {
        this.typeElement = typeElement;
        this.elements = elements;
    }

    public void addMethod(BindRxBusMethod method) {
        methods.add(method);
    }

    public String getFullClassName() {
        return typeElement.getQualifiedName().toString();
    }

    /**
     * 生成一个java文件
     */
    public JavaFile generateJavaFile() {
        // 构建inject函数 public inject(final )
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")// 函数名称
                .addModifiers(Modifier.PUBLIC)// 函数声明类型
                .addAnnotation(Override.class)// 添加注释
                .addParameter(TypeName.get(typeElement.asType()), "host", Modifier.FINAL);// 函数参数名

        // 在inject函数中插入RxBus.register函数
        methodBuilder.addStatement("$T.getInstance().register(host)", TypeUtil.RXBUS);
        // 生成一个xxx$$RxBus类
        TypeSpec finderClass = TypeSpec.classBuilder(typeElement.getSimpleName() + "$$RxBus")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.IRXBUS, TypeName.get(typeElement.asType())))
                .addMethod(methodBuilder.build())
                .build();

        String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();

        return JavaFile.builder(packageName, finderClass).build();
    }
}
