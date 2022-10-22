# classpath* 与 classpath 的区别

测试Demo: tk-01-biz.CommandLineInitializer。

比如下面两个路径的区别：

```
classpath*:mapper/primary/*.xml
classpath:mapper/primary/*.xml
```

这里以 Spring PathMatchingResourcePatternResolver 为例解析。

源码参考 **PathMatchingResourcePatternResolver$getResources(String locationPattern)** 。

对于 `classpath*:mapper/primary/*.xml`  核心处理代码是这一句：

```java
Enumeration<URL> resourceUrls = (cl != null ? 
	cl.getResources(path) : ClassLoader.getSystemResources(path));
```

对于 `classpath:mapper/primary/*.xml`  核心处理代码是这一句：

```java
ClassUtils.getDefaultClassLoader();	//DefaultResourceLoader
getResourceLoader().getResource(locationPattern);
```

可以看到本质区别其实是加载资源的类加载器的区别。没时间，后续再看。

