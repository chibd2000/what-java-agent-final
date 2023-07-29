package com.zpchcbd.mem;

import com.sun.tools.attach.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;

public class Attach {
    public static String CLASS_NAME = "org.apache.catalina.startup.Bootstrap";
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String property = System.getProperty("user.dir");
        String agentPath = property+"/what-java-agent-final.jar";

        try {
            Class.forName("sun.tools.attach.HotSpotAttachProvider");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (args[0] != null && args[0].equals("print")){
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            System.out.println("vm count: " + vms.size());
            for (int i = 0; i < vms.size(); i++) {
                VirtualMachineDescriptor vm = vms.get(i);
                System.out.println(String.format("pid: %s displayName:%s",vm.id(),vm.displayName()));
            }
        }else if(args[0] != null && args[0].equals("inject") && args[1] != null && !args[1].equals("")){
            try {
                VirtualMachine attach = VirtualMachine.attach(args[1]);
                System.out.println(agentPath);
                attach.loadAgent(agentPath);
                attach.detach();
                System.out.println("success");
            } catch (AttachNotSupportedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AgentLoadException e) {
                e.printStackTrace();
            } catch (AgentInitializationException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("java -jar what-java-agent-final.jar print");
            System.out.println("java -jar what-java-agent-final.jar inject pid");
            System.out.println("for exmaple: java -jar what-java-agent-final.jar print");
            System.out.println("For exmaple: java -jar what-java-agent-final.jar inject 8");
        }
    }
}
