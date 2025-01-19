package org.example.springaidemo.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "org.example.springaidemo.dao.mapper", sqlSessionFactoryRef = "sqlSessionFactory1")
public class DataSourceConfig {

    @Value("${datasource.url}")
    private String url;
    @Value("${datasource.username}")
    private String username;
    @Value("${datasource.password}")
    private String password;

    @Bean(name = "dataSource1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                // 使用HikariCP数据连接池管理
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "sqlSessionFactory1")
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:rojerTestMapper/*.xml"));
        return sessionFactoryBean.getObject();
    }
}
