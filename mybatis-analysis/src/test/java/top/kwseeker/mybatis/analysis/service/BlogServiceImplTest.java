package top.kwseeker.mybatis.analysis.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import top.kwseeker.mybatis.analysis.domain.Blog;

public class BlogServiceImplTest {

    private static BlogService blogService;

    @BeforeClass
    public static void beforeClass() {
        blogService = new BlogServiceImpl();
    }

    @Test
    public void getBlogById() {
        Blog blog = blogService.getBlogById(1);
        System.out.println("Query Result: " + blog.toString());
        Assert.assertEquals("kwseeker", blog.getNickname());
    }
}