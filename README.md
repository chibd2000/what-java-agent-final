<<<<<<< HEAD
# what-java-agent-final
该工程用于在一些只有jre的环境下直接打包tools.jar包来利用的情况
=======

# what-java-agent-final

该工程用于在一些只有jre的环境下直接打包tools.jar包来进行java-agent内存马的注入

环境的tools.jar打包自行切换

![img.png](img.png)

MANIFEST.MF

```
Manifest-Version: 1.0
Main-Class: com.zpchcbd.mem.Attach
Agent-Class: com.zpchcbd.mem.Agent
Can-Redefine-Classes: true
Can-Retransform-Classes: true
```
>>>>>>> 7a3eaa1e3fdd0b09bc22034e9d77954511feec53
