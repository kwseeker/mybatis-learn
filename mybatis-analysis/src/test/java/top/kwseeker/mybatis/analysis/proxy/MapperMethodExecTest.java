package top.kwseeker.mybatis.analysis.proxy;

import org.junit.Assert;
import org.junit.Test;
import top.kwseeker.mybatis.analysis.dao.BlogMapper;
import top.kwseeker.mybatis.analysis.domain.Blog;

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

        Type returnType = method.getGenericReturnType();
        Class<?> declaringClass = method.getDeclaringClass();

        Type resolvedType;
        if (returnType instanceof TypeVariable) {

        } else if (returnType instanceof ParameterizedType) {

        } else if (returnType instanceof GenericArrayType) {

        } else {
            resolvedType = returnType;
        }

        Assert.assertEquals(resolvedType, Blog.class);
    }
}
