package top.kwseeker.mybatis.analysis.dao;

import top.kwseeker.mybatis.analysis.domain.Blog;

public interface BlogMapper {

    //通过id查找对应的博客
    Blog selectBlog(int id);
}

