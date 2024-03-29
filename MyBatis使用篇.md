# 从 MyBatis 源码挖掘最全的使用方法

## <font color="blue">Mybatis配置</font>

#### 1.1 mybatis-config.xml

+ Mapper类类型别名 `typeAliases`

    - `typeAlias` 为单个类型指定别名
    
    - `package + @Alias` 为包中所有类型指定别名（默认为类名），可以通过 @Alias 修改其中某个类别名

        ```
        public void registerAlias(Class<?> type) {
            String alias = type.getSimpleName();
            Alias aliasAnnotation = type.getAnnotation(Alias.class);
            //如果类有 @Alias 注解，则使用注解中的别名覆盖 xml 中的别名
            if (aliasAnnotation != null) {
              alias = aliasAnnotation.value();
            } 
            registerAlias(alias, type);
        }
        ```
    
    `package + @Alias` 的优先级高于 `typeAlias`;
    因为源码中`if...else...`先判断 `typeAliases` 中是否有 `package`节点，有的话则按 `package + @Alias`
    设置别名。
    
+ 多数据源

    比如数据库读写分离（一台主，多个从，主写从读）的时候可能需要配置连接多个数据源；
    首先看下加载数据源配置的源码，由《MyBatis源码分析篇.md》可知道在 `XMLConfigBuilder.environmentsElement()`;
    但是分析里面代码发现 xml 配置不能设置同时连接多数据库；
    
    但是官方文档给了通过API接口实现连接多数据库的方案
    ```
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment);
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, properties);
    ```
    要连接多个数据库就为每一个数据库创建一个 `SqlSessionFactory`, 通过传不同的 environment 实现。

+ typeHandler（自定义类型转换）

+ plugin

    两种方式：

    实现步骤：
    1）实现

#### 1.2 Mapper XML配置

#### 1.3 Mapper Annotation配置

## <font color="blue">Mybatis分页</font>

#### 2.1 使用 plugin 拦截器实现分页查询

使用plugin实现分页，有什么弊端？

#### 2.2 使用 SqlSession selectList() 实现分页查询 （逻辑分页）

```
<E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);
```

从JDBC查询结果中取出这页数据返回。

#### 2.2 使用分页插件 pageHelper 与通过limit实现分页的区别，使用分页插件有什么好处？

pageHelper 在数据量很大的时候千万级，select count(0) 会很慢。
什么原因？

## <font color="blue">Mybatis联合查询与动态SQL</font>

#### 3.1 一对多，多对多查询

+ 嵌套查询

    多次查询

+ 嵌套结果

    一次查询

#### 3.2 存储过程

#### 3.3 动态SQL

## <font color="blue">Mybatis批量操作</font>

批量操作的好处：效率高

批量操作的三种方式：

+ for循环 （性能低）

+ foreach 动态sql （性能最高，推荐）
  
    比如插入n条数据，foreach 动态 sql 会将sql拼接起来，组成一条
    insert into t_table (col1, col2, col3)
    values (1，2，3), (2，3，4), (3，4，5), ... 
    
    但是因为受 Mysql 对 sql 长度的限制，并不能一次插入大数据量；
    可以将大量数据拆分成多个批次然后使用 foreach 动态 sql 插入，
    比如每次插入1000条，同时需要对批量操作方法做数据大小检查。
    ```sql
    show variables like '%max_allowed_packet%';
    ```
    
+ 使用 ExecutorType.BATCH session（性能中）

    ```
    SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
    ```
    
    问题是执行完成后 id 随机返回一个数字。



## <font color="blue">Mybatis缓存</font>



#### Mybatis延迟加载（又称“嵌套查询”）

有用到关联对象时再查询。原理后面再分析吧。



## <font color="blue">Mybatis使用连接池</font>

+ 连接池管理 SqlSession 的内幕

## <font color="blue">Mybatis日志管理</font>

## 比较重要的问题

+ 关于Mybatis sql 实现 XML和注解两种方式选择问题

    - XML和注解方式的对比

        sql实现 | 优点 | 缺点
        ---|---|---
        XML | 接口分离，统一管理；复杂的语句不影响接口可读性 | 过多的xml文件 
        注解 | 方便 | 复杂的联合查询不好维护，代码可读性差

    - XML和注解是否可以同时使用

+ 懒查询

    关联查询只有在需要它的结果的时候才会去加载Mapper并查询；

+ Mybatis 批量查询到底能不能返回主键列表？