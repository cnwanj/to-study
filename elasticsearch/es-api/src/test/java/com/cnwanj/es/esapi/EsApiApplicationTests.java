package com.cnwanj.es.esapi;

import com.alibaba.fastjson.JSON;
import com.cnwanj.es.esapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class EsApiApplicationTests {

    @Resource
    private RestHighLevelClient client;

    /**
     * 创建索引：create -> PUT
     */
    @Test
    void testCreateIndex() throws IOException {
        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("cnwanj_index");
        // 客户端执行请求
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    /**
     * 索引是否存在
     */
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("cnwanj_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 删除索引
     */
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("cnwanj_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 创建文档
     */
    @Test
    void testAddDocument() throws IOException {
        // 创建对象
        User user = new User("张三", 22);
        // 创建请求
        IndexRequest request = new IndexRequest("cnwanj_index");
        request.id("1")
                .timeout(TimeValue.timeValueSeconds(1))
                .timeout("1s")
                .source(JSON.toJSONString(user), XContentType.JSON);
        // 客户端发起请求
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());
    }

    /**
     * 文档是否存在
     */
    @Test
    void testExistDocument() throws IOException {
        GetRequest request = new GetRequest("cnwanj_index", "1");
        // 不返回_source上下文
        request.fetchSourceContext(new FetchSourceContext(false))
                .storedFields("_none_");
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 获取文档信息
     */
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("cnwanj_index", "1");
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
        // 输出文档内容
        System.out.println(getResponse.getSource());
        // 输出文档全部内容
        System.out.println(getResponse);
    }

    /**
     * 更新文档信息
     */
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("cnwanj_index", "1");
        // 设置超时时间
        request.timeout("1s");
        // 更新对象信息
        User user = new User("张三学Java", 11);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    /**
     * 删除文档信息
     */
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("cnwanj_index", "1");
        request.timeout("1s");
        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    /**
     * 批量插入
     */
    @Test
    void testBulkDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        List<User> userList = new ArrayList<>();
        userList.add(new User("zs1", 1));
        userList.add(new User("zs2", 2));
        userList.add(new User("zs3", 3));
        userList.add(new User("zs4", 4));
        userList.add(new User("zs5", 5));
        // 批量请求处理
        for (int i = 0; i < userList.size(); i++) {
            // 批量更新、删除就在这里修改对应的请求体
            request.add(new IndexRequest("cnwanj_index")
                    // id不添加表示默认为随机字符串
                    .id(i + 1 + "")
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        // 是否失败，返回false代表成功
        System.out.println(response.hasFailures());
    }

    /**
     * 查询
     */
    @Test
    void testSearch() throws Exception {
        SearchRequest request = new SearchRequest("cnwanj_index");
        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 查询所有
        // MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        // 精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "zs1");
        sourceBuilder.query(termQueryBuilder).timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 将构建的条件设置到请求体中
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}
