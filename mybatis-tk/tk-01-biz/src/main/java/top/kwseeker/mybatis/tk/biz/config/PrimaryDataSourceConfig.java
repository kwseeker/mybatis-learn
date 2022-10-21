package top.kwseeker.mybatis.tk.biz.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import top.kwseeker.mybatis.tk.base.mybatis.CommonMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = {"top.kwseeker.mybatis.tk.base.persistence.dao.primary"},
        markerInterface = CommonMapper.class,
        sqlSessionTemplateRef = "primarySqlSessionTemplate")
@EnableTransactionManagement
public class PrimaryDataSourceConfig {

    /**
     * 默认数据库配置
     */
    @Bean("primary")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource db1Source() {
        return new DruidDataSource();
    }

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primary") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(resolveMapperLocations());
        return factoryBean.getObject();
    }

    public Resource[] resolveMapperLocations() throws IOException {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/primary/*.xml");
        //mapperLocations.add("classpath*:/*Mapper*.xml");
        List<Resource> resources = new ArrayList<>();
        if (mapperLocations.size() > 0) {
            for (String mapperLocation : mapperLocations) {
                //TODO 资源为何加载失败
                Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            }
        }
        return resources.toArray(new Resource[0]);
    }

    @Bean(name = "primaryDataSourceTransactionManager")
    @Primary
    public DataSourceTransactionManager primaryDataSourceTransactionManager(@Qualifier("primary") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "primarySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate primarySqlSessionTemplate(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
