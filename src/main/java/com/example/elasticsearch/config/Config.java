package com.example.elasticsearch.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.HttpHeaders;

import javax.annotation.Nonnull;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.elasticsearch.repository")
@ComponentScan(basePackages = "com.example.elasticsearch.repository")
public class Config extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    @Nonnull
    public RestHighLevelClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"))
                .setDefaultHeaders(compatibilityHeaders());
        return new RestHighLevelClient(builder);
    }

    @Bean
    public ElasticsearchRestTemplate template (RestHighLevelClient restHighLevelClient){
        return new ElasticsearchRestTemplate(restHighLevelClient);
    }

    /**
     * 防止保存時的報錯 Unable to parse response body for Response
     * 服務器起 8.x.x 以上版本 就會出錯
     * 這些為兼容性標頭
     */
    private Header[] compatibilityHeaders() {
        return new Header[]{new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7")};
    }
}
