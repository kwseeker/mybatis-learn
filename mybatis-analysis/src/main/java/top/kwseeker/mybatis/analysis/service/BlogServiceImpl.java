package top.kwseeker.mybatis.analysis.service;

import org.apache.ibatis.session.SqlSession;
import top.kwseeker.mybatis.analysis.dao.BlogMapper;
import top.kwseeker.mybatis.analysis.domain.Blog;
import top.kwseeker.mybatis.analysis.util.MybatisUtil;

public class BlogServiceImpl implements BlogService {

    @Override
    public Blog getBlogById(int id) {
        SqlSession sqlSession = MybatisUtil.getSqlSession("mybatis-config.xml");
        BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
        return blogMapper.selectBlog(id);
    }
}
