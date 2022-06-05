package top.kwseeker.mybatis.analysis.service;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import top.kwseeker.mybatis.analysis.dao.BlogMapper;
import top.kwseeker.mybatis.analysis.domain.Blog;

import java.io.IOException;
import java.io.InputStream;

public class BlogServiceImplTest {

    private static final String CONFIG_XML = "mybatis-config.xml";

    private static SqlSession sqlSession;

    @BeforeClass
    public static void beforeClass() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(CONFIG_XML);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession(true);
    }

    @Test
    public void getBlogById() {
        BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = blogMapper.selectBlog(1);
        System.out.println("Query Result: " + blog.toString());
        Assert.assertEquals("kwseeker", blog.getNickname());
        Blog blog2 = blogMapper.selectBlog(1);

    }
}