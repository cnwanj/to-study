package com.cnwanj.es.esapi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: cnwanj
 * @date: 2021-05-04 16:51:31
 * @version: 1.0
 * @desc:
 */
@Configuration
public class ElasticSearchConfig {
    /**
     * 因为是SpringBoot项目，可以先分析源码：
     * xxxAutoConfiguration
     * xxxProperties
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 将RestHighLevelClient放入到spring中待使用
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http")
                )
        );
    }
}
