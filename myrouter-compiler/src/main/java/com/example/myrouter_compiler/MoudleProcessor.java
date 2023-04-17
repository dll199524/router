package com.example.myrouter_compiler;

import com.example.myrouter_annotation.Action;
import com.example.myrouter_annotation.ThreadMode;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import jdk.javadoc.internal.doclets.toolkit.builders.MethodBuilder;


@AutoService(Processor.class)
public class MoudleProcessor extends AbstractProcessor {

    private Elements mElement;
    private Filer mFiler;
    private final String KEY_MODULE_NAME = "moduleName";
    private TypeMirror mRouterAction = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElement = processingEnv.getElementUtils();
        mRouterAction = mElement.getTypeElement(Consts.ROUTERACTION).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 1. 有没配置 modelName 防止 class 类冲突
        String moduleName = "";

        Map<String, String> options = processingEnv.getOptions();
        if (isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        System.out.println("moduleName = " + moduleName);

        if (!TextUtils.isEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        } else {
            String errorMessage = "These no module name, at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n";
            throw new RuntimeException("DRouter::Compiler >>> No module name, for more information, look at gradle log.\n" + errorMessage);
        }

        // 2. 生成 Java 类，效果如下
        /*public class DRouter$$Moudle implements RouterMoudle {

            Map<String, String> actions;

            public DRouter$$Assist() {
                actions = new HashMap<>();
                actions.put("login/module", ActionWrapper.build());
            }

            @Override
            public String findAction(String moduleName) {
                return actions.get(moduleName);
            }
        }*/
        // 生成类继承和实现接口
        ClassName routerClassName = ClassName.get("com.example.myrouter_api.action", "RouterMoudle");
        ClassName mapClassName = ClassName.get("java.util", "Map");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("DRouter$$Moudle$$" + moduleName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(routerClassName)
                .addField(mapClassName, "actions", Modifier.PRIVATE);
        // 构造函数
        MethodSpec.Builder constructBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        constructBuilder.addStatement("actions = new $T<>()", ClassName.bestGuess("java.util.HashMap"));

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Action.class);
        Map<String, String> moudles = new HashMap<>(elements.size());

        ClassName actionWrapperClassName = ClassName.get("com.example.myrouter_api.extra", "ActionWrapper");
        ClassName threadModeClassName = ClassName.get("com.example.myrouter_annotation", "ThreadMode");
        for (Element element : elements) {
            Action actionAnnotation = element.getAnnotation(Action.class);
            String actionName = actionAnnotation.path();
            if (!actionName.startsWith(moduleName + "/"))
                error(element, "Path name of the action must begin with %s%s", moduleName, "/");
            Element enclosingElement = element.getEnclosingElement();
            String packageName = mElement.getPackageOf(enclosingElement).getQualifiedName().toString();
            String actionClassName = packageName + "." + actionName;
            // 判断 Interceptor 注解类是否实现了 ActionInterceptor
            if (!((TypeElement) element).getInterfaces().contains(mRouterAction)) {
                error(element, "%s verify failed, @Action must be implements %s", element.getSimpleName().toString(), Consts.ROUTERACTION);
            }
            if (moudles.containsKey(actionName)) {
                error(element, "%s module name already exists", actionName);
            }
            moudles.put(actionName, actionClassName);
            /*public static ActionWrapper bulid(Class<? extends RouterAction> actionClass,
                    String path, ThreadMode threadMode,
            boolean extraProcess, RouterAction routerAction) {
                return new ActionWrapper(actionClass, path, threadMode, extraProcess, routerAction);
            }*/
            constructBuilder.addStatement("this.actions.put($S, $T.build($T.class, $S, " +
                    actionAnnotation.extraProcess() + ", $T." + actionAnnotation.threadMode() + "))",
                    actionName, actionWrapperClassName, ClassName.bestGuess(actionClassName), actionName, threadModeClassName);
        }

        //实现方法
        MethodSpec.Builder unbindMethodBuild = MethodSpec.methodBuilder("findAction")
                .addParameter(String.class, "actionName")
                .addAnnotation(Override.class)
                .returns(actionWrapperClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        unbindMethodBuild.addStatement("return (ActionWrapper) actions.get(actionName)");

        classBuilder.addMethod(constructBuilder.build());
        classBuilder.addMethod(unbindMethodBuild.build());

        try {
            JavaFile.builder(Consts.ROUTER_MODULE_PACK_NAME, classBuilder.build())
                    .addFileComment("myrouter 自动生成")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("翻车了");
        }
        return false;
    }

    private boolean isNotEmpty(Map<String, String> options) {
        return options != null && !options.isEmpty();
    }

    private void error(Element element, String message, String... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }


    // 1. 指定处理的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 2. 给到需要处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        // 需要解析的自定义注解 BindView  OnClick
        annotations.add(Action.class);
        return annotations;
    }
}