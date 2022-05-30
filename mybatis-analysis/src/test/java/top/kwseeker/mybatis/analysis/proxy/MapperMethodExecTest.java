package top.kwseeker.mybatis.analysis.proxy;

import org.junit.Test;
import top.kwseeker.mybatis.analysis.dao.BlogMapper;

import java.lang.reflect.*;

/**
 * MapperMethod执行流程
 * 1）创建 MapperMethod 对象
 *      1.1）判断当前执行的代理类方法：SQL命令的类型、Statement名
 *      1.2）判断当前执行的代理类方法：返回结果类型、
 *      相关类：SqlCommand MappedStatement
 * 2）
 */
public class MapperMethodExecTest {

    @Test
    public void testMethodSignatureGen() throws Exception {
        Class<?> mapperInterface = BlogMapper.class;

        Method method = mapperInterface.getMethod("selectBlog", int.class);
        Type srcType = mapperInterface;

        Type returnType = method.getGenericReturnType();        //获取返回值的泛型类型，BlogMapper$selectBlog 返回 Blog.class
        Class<?> declaringClass = method.getDeclaringClass();   //方法定义类，即BlogMapper.class

        Type resolvedType;
        if (returnType instanceof TypeVariable) {   //泛型类型变量
            System.out.println(returnType);
        } else if (returnType instanceof ParameterizedType) {   //泛型参数化类型
            System.out.println(returnType);
        } else if (returnType instanceof GenericArrayType) {    //泛型数组类型
            System.out.println(returnType);
        } else {
            resolvedType = returnType;
        }
    }
}
