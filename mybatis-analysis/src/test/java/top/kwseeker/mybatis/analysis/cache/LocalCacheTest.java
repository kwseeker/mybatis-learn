package top.kwseeker.mybatis.analysis.cache;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import top.kwseeker.mybatis.analysis.dao.BlogMapper;

import java.io.IOException;
import java.io.InputStream;

public class LocalCacheTest {

    private static final String CONFIG_XML = "mybatis-config.xml";

    private static SqlSession sqlSession;

    @BeforeClass
    public static void beforeClass() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(CONFIG_XML);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession(true);
    }

    @Test
    public void testCache() {
        BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);
        System.out.println(">>>>>>> Query Result: " + blogMapper.selectBlog(1).toString());
        sqlSession.commit();    //这里需要commit，然后才会真正缓存到二级缓存
        System.out.println(">>>>>>> Query Result: " + blogMapper.selectBlog(1).toString());
        int ret = blogMapper.updateBlog(1, "kwseeker233");
        System.out.println(">>>>>>> " + ret);
        System.out.println(">>>>>>> Query Result: " + blogMapper.selectBlog(1).toString());
        System.out.println(">>>>>>> Query Result: " + blogMapper.selectBlog(1).toString());
    }
    //DEBUG [main] - Cache Hit Ratio [top.kwseeker.mybatis.analysis.dao.BlogMapper]: 0.0
    //DEBUG [main] - ==>  Preparing: select * from t_blog where id = ?
    //DEBUG [main] - ==> Parameters: 1(Integer)
    //TRACE [main] - <==    Columns: id, title, nickname
    //TRACE [main] - <==        Row: 1, Mybatis简介, kwseeker233
    //DEBUG [main] - <==      Total: 1
    //>>>>>>> Query Result: Blog(id=null, title=Mybatis简介, nickname=kwseeker233)
    //DEBUG [main] - Cache Hit Ratio [top.kwseeker.mybatis.analysis.dao.BlogMapper]: 0.0
    //>>>>>>> Query Result: Blog(id=null, title=Mybatis简介, nickname=kwseeker233)
    //DEBUG [main] - ==>  Preparing: update t_blog set nickname = ? where id = ?
    //DEBUG [main] - ==> Parameters: kwseeker233(String), 1(Integer)
    //DEBUG [main] - <==    Updates: 1
    //>>>>>>> 1
    //DEBUG [main] - Cache Hit Ratio [top.kwseeker.mybatis.analysis.dao.BlogMapper]: 0.0
    //DEBUG [main] - ==>  Preparing: select * from t_blog where id = ?
    //DEBUG [main] - ==> Parameters: 1(Integer)
    //TRACE [main] - <==    Columns: id, title, nickname
    //TRACE [main] - <==        Row: 1, Mybatis简介, kwseeker233
    //DEBUG [main] - <==      Total: 1
    //>>>>>>> Query Result: Blog(id=null, title=Mybatis简介, nickname=kwseeker233)
    //DEBUG [main] - Cache Hit Ratio [top.kwseeker.mybatis.analysis.dao.BlogMapper]: 0.0
    //>>>>>>> Query Result: Blog(id=null, title=Mybatis简介, nickname=kwseeker233)
}
