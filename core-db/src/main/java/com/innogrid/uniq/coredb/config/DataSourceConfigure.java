package com.innogrid.uniq.coredb.config;


import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by choijinwook on 16. 8. 16.
 */
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
                .url("jdbc:postgresql://localhost:5432/database")
                .username("USER_ID")
                .password("PASSWORD")
                .build();
        return dataSource;
    }

    @Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean(ApplicationContext applicationContext) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:sqlMapConfig.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:sql/**.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager getDataSourceTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }


}
