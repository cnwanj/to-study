package com.cnwanj.esjd.service;

import com.alibaba.fastjson.JSON;
import com.cnwanj.esjd.entity.Content;
import com.cnwanj.esjd.utils.JsoupUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: cnwanj
 * @date: 2021-06-14 22:48:04
 * @version: 1.0
 * @desc:
 */
@Service
public class ContentService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 将解析到的数据存储到es中
     * @param keyword
     * @return
     * @throws Exception
     */
    public Boolean parseContent(String keyword) throws Exception {
        // 获取解析数据
        List<Content> contentList = new JsoupUtil().parseJD(keyword);
        // 创建请求对象
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        // 将数据放入ES的请求对象中
        for (Content content : contentList) {
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(content), XContentType.JSON));
        }
        // 请求ES并存储数据
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    /**
     * 条件查询
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> searchContent(String keyword, int pageNum, int pageSize) throws IOException {
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页查询
        searchSourceBuilder.from(pageNum).size(pageSize)
            // 精准匹配
            .query(QueryBuilders.termQuery("title", keyword))
            .timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        // 请求ES获取数据
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            resultList.add(hit.getSourceAsMap());
        }
        return resultList;
    }

    /**
     * 高亮搜索
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> searchContentHighlight(String keyword, int pageNum, int pageSize) throws Exception {
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页查询
        searchSourceBuilder.from(pageNum).size(pageSize)
            // 精确匹配
            .query(QueryBuilders.termQuery("title", keyword))
            .timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title")
            .preTags("<span style='color:red'>")
            .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);

        // 执行搜索
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            // 获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // 获取高亮字段
            HighlightField titleField = highlightFields.get("title");
            // 原来的结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (titleField != null) {
                // 获取高亮字段的碎片
                Text[] fragments = titleField.getFragments();
                StringBuilder str = new StringBuilder();
                for (Text text : fragments) {
                    str.append(text);
                }
                // 将高亮部分替换原来结果
                sourceAsMap.put("title", str.toString());
            }
            results.add(sourceAsMap);
        }
        return results;
    }
}
