package top.kwseeker.mybatis.analysis.proxy;

import org.junit.Test;
import top.kwseeker.mybatis.analysis.dao.BlogMapper;
import top.kwseeker.mybatis.analysis.domain.Blog;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MethodHandleTest {

    /**
     * Mybatis 获取Mapper代理对象时 MethodHandle 的使用
     *  看代码只有调用 default 方法时才会使用到，public 未实现的方法都会创建 MapperMethod
     */
    @Test
    public void testMapperProxyUseMethodHandle() throws Exception {
        Class<BlogMapper> mapperInterface = BlogMapper.class;

        BlogMapper blogMapperProxy = (BlogMapper) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[] { mapperInterface }, new MapperProxy());

        Blog blog = blogMapperProxy.selectBlog(1);
        System.out.println(blog.toString());
    }

    static class MapperProxy implements InvocationHandler {

        private static final int ALLOW_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                //final Class<?> declaringClass = method.getDeclaringClass();
                //// 1 创建查找（所有访问权限类型）和 MethodHandle
                //Constructor<MethodHandles.Lookup> lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                //lookupConstructor.setAccessible(true);
                //// default 方法
                //MethodHandle methodHandle = lookupConstructor.newInstance(declaringClass, ALLOW_MODES).unreflectSpecial(method, declaringClass);
                //// 2 调用 MethodHandle 方法句柄
                //return methodHandle.bindTo(proxy).invokeWithArguments(args);

                // 其他方法
                Class<BlogMapper> mapperInterface = BlogMapper.class;
                return new MapperMethod(mapperInterface, method).execute(args);
            }
        }
    }

    static class MapperMethod {

        private final SqlCommand command;
        private final MethodSignature method;

        public MapperMethod(Class<?> mapperInterface, Method method) {
            this.command = new SqlCommand(mapperInterface, method);
            this.method = new MethodSignature(mapperInterface, method);
        }

        public Object execute(Object[] args) {
            Object result;
            switch (command.getType()) {
                case SELECT:
                    Blog blog = new Blog();
                    blog.setId(1);
                    blog.setTitle("title");
                    blog.setNickname("nickname");
                    result = blog;
                    break;
                default:
                    throw new RuntimeException("Unknown execution method for: " + command.getName());
            }
            return result;
        }
    }

    static class SqlCommand {
        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Class<?> mapperInterface, Method method) {
            this.name = "ms id";
            this.type = SqlCommandType.SELECT;
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }

    static class MethodSignature {

        public MethodSignature(Class<?> mapperInterface, Method method) {
        }
    }

    enum SqlCommandType {
        UNKNOWN, INSERT, UPDATE, DELETE, SELECT, FLUSH
    }
}
