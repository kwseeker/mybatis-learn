# Mybatis 缓存机制

官方文档：

[Mybatis缓存使用](https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#%E7%BC%93%E5%AD%98)



## 缓存原理

工作原理看根目录下流程图就够了；效果比之前写几百字的总结效果还好。

Mybatis一共有两级缓存：
+ **一级缓存**（会话缓存/本地缓存，同一个SqlSession共享）

  默认开启；

  + **实现类**

    PerpetualCache（就一个HashMap，甚至连Cache接口的读写锁都没有实现）；

    > Perpetual: 永久的、长期的

  + **如何理解一级缓存是同一个SqlSession共享的？**

    

  + **关闭方法**

    修改localCacheScope为STATEMENT，并不是禁用一级缓存，而是每次查询都会清除本地缓存，相当于没有使用一级缓存。

    | 设置名          | 描述                                                         | 有效值               | 默认值  |
    | :-------------- | :----------------------------------------------------------- | :------------------- | :------ |
    | localCacheScope | MyBatis 利用本地缓存机制（Local Cache）防止循环引用和加速重复的嵌套查询。 默认值为 SESSION，会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地缓存将仅用于执行语句，对相同 SqlSession 的不同查询将不会进行缓存。 | SESSION \| STATEMENT | SESSION |

    ```xml
    <configuration> 
    	<settings>
            <!-- 设置一级缓存为STATEMENT级别 -->
            <setting name="localCacheScope" value="STATEMENT"/>
        </settings>
    </configuration>
    <!-- 原理是：BaseExecutor 判断一级缓存作用范围是 LocalCacheScope.STATEMENT 的话，会执行清除操作
    	if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {        
            clearLocalCache();
        }
     -->
    ```

+ **二级缓存**（全局缓存，同一个namespace共享）

  二级缓存是事务性的。这意味着，当 SqlSession 完成并提交时，或是完成并回滚，但没有执行 flushCache=true 的 insert/delete/update 语句时，缓存会获得更新（即除了更删改外事务操作也可能更新二级缓存）。

  默认关闭，基本不会使用。
  
  + **如何理解二级缓存是同一个namespace共享的？**
  
    命名空间（namespace）的作用有两个，一个是利用更长的全限定名来将不同的语句隔离开来，同时也实现了你上面见到的接口绑定。
  
    ```xml
    <mapper namespace="top.kwseeker.mybatis.analysis.dao.BlogMapper">
    ```
  
    
  
  + **开启方法**
  
    要启用全局的二级缓存，只需要在你的 **SQL 映射文件**中添加一行。但是测试发现没有这么简单，还需要结果集对象实现序列化接口（Serializable），并在查询完毕之后执行`sqlSession.commit();`参考后面一节。
  
    ```xml
    <mapper namespace="top.kwseeker.mybatis.analysis.dao.BlogMapper">
    	<cache/>
    </mapper>
    ```
  
  + **二级缓存失效**
  
    参考Demo LocalCacheTest$testCache(), 发现只是配置 `<cache/>`，多次查询二级缓存始终返回为null; 
  
    需要修改结果集对象实现序列化接口（Serializable），并在查询完毕之后执行`sqlSession.commit();`
  
    ```
    public class Blog implements Serializable {
    }
    
    System.out.println(">>>>>>> Query Result: " + blogMapper.selectBlog(1).toString());
    sqlSession.commit();    //这里需要commit，然后才会真正缓存到二级缓存
    ```
  
    

> 注意：Mybatis自带的两级缓存都属于分布式节点的“本地缓存”，在分布式场景下都不可以被多节点共享，都可能存在读取到脏数据的问题。



## 源码流程 （废弃）

下面内容已废弃，源码流程参考流程图。

工作原理介绍参考 MyBatis知识点总结.md;

这里跟一下源码，验证一下。入口还是使用测试代码从dao层开始

```java
tbCallRecordDao.selectCallTicketList(req);
```

调用上面的dao层接口会被 `MapperProxy` 代理（MapperProxy.java line:48）然后执行Mapper代理执行流程；

获取接口及SQL节点信息构造 `MappedStatement`（这里省略了很多步骤）, 然后就进入缓存相关的操作了（DefaultSqlsession.java line 148）

```java
executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
```

executor 是 `CachingExecutor` 类型，源码实现

```java
public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, 							ResultHandler resultHandler, CacheKey key, BoundSql boundSql)
      throws SQLException {
    //
    //获取MappedStatement缓存，而MappedStatement是从全局配置configuration中获取的
    //MappedStatement ms = configuration.getMappedStatement(statement);
    //MappedStatement的cache又是从Configuration直接取的
    //Cache cache = configuration.getCache(namespace);
    //Configuration caches
    //protected final Map<String, Cache> caches = new StrictMap<>("Caches collection");
    //而Configuration的caches又是从 MapperBuilderAssistant 创建添加的
    //Cache cache = new CacheBuilder(currentNamespace)
    //    .implementation(valueOrDefault(typeClass, PerpetualCache.class))
    //    .addDecorator(valueOrDefault(evictionClass, LruCache.class))
    //    .clearInterval(flushInterval)
    //    .size(size)
    //    .readWrite(readWrite)
    //    .blocking(blocking)
    //    .properties(props)
    //    .build();
    //configuration.addCache(cache);
    //所以二级缓存是与命名空间相关联的，那么这个命名空间又是什么？继续找currentNamespace值的来源
    //String namespace = context.getStringAttribute("namespace"); //XMLMapperBuilder
    //对应Mapper文件的 <mapper namespace="top.kwseeker.mybatis.usage.dao.BlogMapper"></mapper>
    //综上：二级缓存是为每个Mapper文件创建的位于configuration全局配置对象
    Cache cache = ms.getCache();
    if (cache != null) {
      //是否需要刷新缓存，默认情况下，select不需要刷新缓存，insert,delete,update要刷新缓存
      flushCacheIfRequired(ms);
      if (ms.isUseCache() && resultHandler == null) {
        ensureNoOutParams(ms, boundSql);
        @SuppressWarnings("unchecked")
        //查询二级缓存，二级缓存是存放在PerpetualCache类中的HashMap中的，使用到了装饰器模式
        List<E> list = (List<E>) tcm.getObject(cache, key);
        if (list == null) {
          // 如果二级缓存没命中，则调用这个方法：这方法中是先查询一级缓存，如果还没命中，则会查询数据库
          list = delegate.<E> query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
          // 把查询出的数据放到TransactionCache的entriesToAddOnCommit这个HashMap中，要注意，只是暂时存放到这里，只有当事务提交后，这里的数据才会真正的放到二级缓存中
          tcm.putObject(cache, key, list); // issue #578 and #116
        }
        return list;
      }
    }
    //如果没有使用二级缓存则直接查询一级缓存，还没命中就查询数据库
    return delegate.<E> query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

继续执行，即上面的 `delegate.<E>query(...)` 方法

```java
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    if (queryStack == 0 && ms.isFlushCacheRequired()) {
      clearLocalCache();
    }
    List<E> list;
    try {
      queryStack++;
      //查询一级缓存
      //localCache 在BaseExector构造器实例化，也即在SimpleExector构造器实例化，
      //然后看SimpleExector在哪里构造的，查询到是在Configuration的newExector中构造的
      //查询到5处调用，其中一处构造的实例是局部变量方法执行结束就不存在了不用管，一处是方法的重载不用管，
      //一处是在ResultLoader（貌似是懒加载查询，TODO：https://blog.csdn.net/mingtian625/article/details/47358003）
      //最后一处是在 DefaultSqlSessionFactory openSessionFromDataSource() 和 openSessionFromConnection() 方法中被调用的，实例化的 executor 存储在 DefaultSqlSession 的 executor 成员，所以一级缓存是与SqlSession相关联的。
      //一级缓存的数据结构是带id的HashMap
      list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
      if (list != null) {
        //处理输出参数的（TODO）
        handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
      } else {
        //一级缓存没有命中，从数据库查询
        list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
      }
    } finally {
      queryStack--;
    }
    if (queryStack == 0) {
      for (DeferredLoad deferredLoad : deferredLoads) {
        deferredLoad.load();
      }
      // issue #601
      deferredLoads.clear();
      if (configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
        // issue #482
        clearLocalCache();
      }
    }
    return list;
  }
```

查询数据库

```java
private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    //这里需要回溯一下key（CacheKey）的生成，在 CachingExecutor 中生成
    //CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
    //但是这个方法内部通过delegate调用的BaseExecutor的createCacheKey()方法（BaseExecutor.java line:195）
    //public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) 不细看了估计反正是使用所有传参信息经过算法算出唯一的键
    //所以这一行是不管查数据库结果如何先在缓存中占个存储位置
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
      //获取连接，创建 PreparedStatement(Mapper中使用的默认的设置就是生成PreparedStatement)并执行
      list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
      //这一句和下一句一起用来刷新缓存
      localCache.removeObject(key);
    }
    localCache.putObject(key, list);
    if (ms.getStatementType() == StatementType.CALLABLE) {
      //如果是 CallableStatement 则需要更新 localOutputParameterCache
      localOutputParameterCache.putObject(key, parameter);
    }
    return list;
}
```

单元测试：参考 mybatis-analysis

```java
@Test
public void queryCallRecordInfo() {
    TbCallRecordInfo info = callBillsService.queryCallRecordInfo(1);
    //单步执行到这里，手动改一下数据库（Mybatis缓存应该不会刷新），查看返回结果，看是从一级缓存取出来的还是从数据库取出来的
    TbCallRecordInfo info1 = callBillsService.queryCallRecordInfo(1);
    //测试结果是确实是从一级缓存取的，而且一级缓存无法处理数据库被手动修改以及被其他应用修改导致的数据库数据和此应用Mybatis缓存不一致性问题
}
```

总结：

Mybatis缓存中心思想就是如果有设置二级缓存先从二级缓存中获取值，没有的话从一级缓存中获取值，一级缓存也没有的话则从数据库查询。

而二级缓存和一级缓存实现分别是CachingExecutor,  SimpleExector。**CachingExector中维持了SimpleExector的实例delegate**，从而实现二级缓存无法命中时，通过delegate从一级缓存查询。

```
CachingExector implement Exector
SimpleExector extends BaseExector implement Exector
```



#### Mybatis解决缓存与数据库一致性问题

##### 一级缓存的处理方式

首先提几个问题：

+ 多个SqlSession同时操作数据库，一个SqlSession怎么知道其他SqlSession是不是更新了自己访问的数据？如果使用了缓存，缓存数据不就过期了？

  不会知道，所以需要添加事务管理，就像平时在Spring Boot应用中添加的@Transactional。

  同时事务中的操作完成后就清除缓存。

+ 多个线程同时通过一个SqlSession更新查询数据库会有什么问题么？

  看SqlSession代码不是线程安全的，如果使用时使用多个线程通过一个SqlSession操作数据库必然会导致

  不一致性问题。同样需要做同步控制。

+ mybatis-spring 是怎么管理 SqlSession 的？

  其实前面两个问题是讲应该怎么合理使用SqlSession操作数据库，最简单的每个操作都新建SqlSession并添加

  事务管理控制更新操作，操作完成关闭SqlSession。Spring使用了一种更高效的方式，使得不一定每次操作都需要新建SqlSession。[spring如何管理mybatis(二) ----- SqlSession的线程安全性](https://www.cnblogs.com/zcmzex/p/9005194.html)

理解上面问题后就清除怎么保证**单机架构**下MyBatis一级缓存与数据库一致性了：

1）不要让多个SqlSession同时更新相同的数据表或一个查一个更新数据表（使用锁或者事务控制）。

2）操作完成后要清空缓存。

使用mybatis-spring，应用运行时手动修改数据库，如果有添加事务管理应该是不会有什么风险的，如果没有添加事务管理，估计会出现Mybatis缓存与数据库不一致的问题。

这个问题其实和两个线程同时通过SqlSession至少一个更新数据库的问题是一样的。

**分布式架构**下仍然需要分布式事务处理。

##### 二级缓存的处理方式

二级缓存的不一致性只存在于分布式系统，单机架构所有线程都是公用configuration 中的二级缓存，

分布式架构下，网上提供了一种思路是使此服务的多个实例使用同一个外部的Redis服务器作为二级缓存。

当然使用分布式事务也可以。