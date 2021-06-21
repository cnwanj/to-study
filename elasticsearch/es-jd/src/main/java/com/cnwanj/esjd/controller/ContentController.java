package com.cnwanj.esjd.controller;

import com.cnwanj.esjd.service.ContentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: cnwanj
 * @date: 2021-06-14 22:58:03
 * @version: 1.0
 * @desc:
 */
@RestController
public class ContentController {

    @Resource
    private ContentService contentService;

    /**
     * 将解析到的数据存储到es中
     * @param keyword
     * @return
     * @throws Exception
     */
    @RequestMapping("parseContent/{keyword}")
    public Boolean parseContent(@PathVariable String keyword) throws Exception {
        return contentService.parseContent(keyword);
    }

    /**
     * 搜索
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(method = {RequestMethod.GET}, path = "searchContent/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> searchContent(@PathVariable String keyword, @PathVariable int pageNum, @PathVariable int pageSize) throws Exception {
        return contentService.searchContent(keyword, pageNum, pageSize);
    }

    /**
     * 高亮搜索
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(method = {RequestMethod.GET}, path = "searchContentHighlight/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> searchContentHighlight(@PathVariable String keyword, @PathVariable int pageNum, @PathVariable int pageSize) throws Exception {
        return contentService.searchContentHighlight(keyword, pageNum, pageSize);
    }
}
