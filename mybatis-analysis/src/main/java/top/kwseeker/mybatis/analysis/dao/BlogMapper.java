package top.kwseeker.mybatis.analysis.dao;

import org.apache.ibatis.annotations.Param;
import top.kwseeker.mybatis.analysis.domain.Blog;

public interface BlogMapper {

    //通过id查找对应的博客
    Blog selectBlog(int id);

    int updateBlog(@Param("id") int id, @Param("nickname") String nickname);
}

