package com.innogrid.uniq.coredb.config;


import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Configuration
@EnableTransactionManagement
@Import(uniqCommonConfig.class)
public class DataSourceConfigure {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigure.class);

    public DataSourceConfigure() {

    }


    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:postgresql://localhost:5432/database_example")
                .username("username")
                .password("password")
                .build();
        return dataSource;
    }

//    @Bean
//    @ConfigurationProperties("spring.datasource2")
//    public DataSource dataSource2() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }

    @Bean(name = "dataSource2")
    public DataSource dataSource2() {
        DataSource dataSource2 = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:mysql://127.0.0.1:3306/innogrid_db?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul")
                .username("username")
                .password("password")
                .build();
        return dataSource2;
    }

    @Bean(name = "firstsqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean(ApplicationContext applicationContext) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:sqlMapConfig.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:sql/**.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean(name = "firstsqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("firstsqlSessionFactoryBean") SqlSessionFactory firstsqlSessionFactory) {
        return new SqlSessionTemplate(firstsqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }

    @Bean(name = "secondsqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean2(ApplicationContext applicationContext) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource2());
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:sqlMapConfig.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:sql/**.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean(name = "secondsqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate2(@Qualifier("secondsqlSessionFactoryBean") SqlSessionFactory secondsqlSessionFactory) {
        return new SqlSessionTemplate(secondsqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager2() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource2());
        return dataSourceTransactionManager;
    }


}
